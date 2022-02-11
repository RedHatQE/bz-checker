/* -*- c-basic-offset: 2; indent-tab-mode: nil -*- */
package com.redhat.qe.auto.bugzilla;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import java.io.IOException;
import org.apache.http.HttpHeaders;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import com.redhat.qe.auto.bugzilla.IBugzillaAPI;
import com.redhat.qe.auto.bugzilla.BugzillaAPIException;
import org.apache.http.client.methods.HttpUriRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.stream.Stream;
import java.util.function.Supplier;

public class REST_API implements IBugzillaAPI {
	protected static Logger log = Logger.getLogger(REST_API.class.getName());

  private Map<String,Map<String,Object> > buglist;
  private URI uri;
  private CloseableHttpClient httpclient;

  public static String[] bugFields = {"status",
                                      "product",
                                      "keywords",
                                      "comments",
                                      "summary",
                                      "qa_contact",
                                      "tags"};


  public REST_API(){
    buglist = new HashMap<String, Map<String,Object> >();
  }

  public void connectBZ() throws BugzillaAPIException {
    try {
      uri = new URI(System.getProperty("bugzilla.url"));
      httpclient = HttpClients.custom()
        //.setDefaultCredentialsProvider(credsProvider)
        .build();
    } catch (URISyntaxException ex) {
      throw new BugzillaAPIException(ex.toString());
    }
  }


  public int login(String userid, String password) throws BugzillaAPIException{
    return 0;
  }

  protected Map<String,Object> parseComment(JSONObject commentObj) {
    return new HashMap<String, Object>() {
      private static final long serialVersionUID = -7353628974554585200L;
      {
        Iterator<String> it = commentObj.keys();
        while(it.hasNext()){
          String key = it.next();
          put(key, commentObj.get(key));
        }
      }};
  }


  public Map<String, Object> getBug(String bugId) throws BugzillaAPIException {
    try {
      String url = Stream.of(uri.toString(),"/bug/",bugId,
                             //"?api_key=",System.getProperty("bugzilla.apikey"),
                             "?include_fields=",String.join(",", bugFields))
        .map(s -> s.trim())
        .reduce((acc,s) -> acc + s).get();
      URI newURI = new URI(url);
      if( System.getProperty("bugzilla.cache","false").equals("true")){
        Map<String,Object> bug = buglist.get(bugId);
        if(bug != null) {
          log.finer("using cached bugzilla " + bugId);
          return bug;
        }
      }
      CloseableHttpResponse response = null;
      try {
	HttpUriRequest request = RequestBuilder.get()
	  .setUri(newURI.normalize())
	  .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + System.getProperty("bugzilla.apikey"))
	  .build();
        response = httpclient.execute(request);
        if(response.getStatusLine().getStatusCode() != 200){
          throw new BugzillaAPIException("Wrong status code returned. The response: "
					 + response.toString());
        };
        JSONTokener tokener = new JSONTokener(response.getEntity().getContent());
        JSONObject root = new JSONObject(tokener);
	/* {"code":32000,
	   "documentation":"https://bugzilla.stage.redhat.com/docs/en/html/api/index.html",
	   "message":"You have attempted to access the API either using an unsupported method or using one or more unsupported parameters. You must use the 'Authorization' header to authenticate to the API and you must remove all unsupported parameters from the query. The unsupported parameters are: Bugzilla_login, Bugzilla_password, Bugzilla_token, Bugzilla_api_key. See https://bugzilla.stage.redhat.com/docs/en/html/api/core/v1/general.html#authentication for details on using the 'Authorization' header.",
	   "error":true}
	*/
	/* assert that no error appears in the response */
	Object error = root.opt("error");
	if(error != null){
	  Boolean errorCode = (Boolean) error;
	  if( errorCode ){
	    throw new BugzillaAPIException("An error appeared in REST response. The response: "
					   + root.toString());
	  };
	};
        JSONArray bugs = (JSONArray) root.get("bugs");
        JSONObject bugObj = (JSONObject) bugs.get(0);
	Map<String, Object> bug = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
              put("status",bugObj.getString("status"));
              put("product",bugObj.getString("product"));
              put("qa_contact", bugObj.getString("qa_contact"));
              put("keywords", new ArrayList<String>() {
                  private static final long serialVersionUID = 1L;
                  {
                    JSONArray keywords = bugObj.getJSONArray("keywords");
                    Iterator<Object> it = keywords.iterator();
                    while(it.hasNext()){
                      add((String)it.next());
                    }
                  }});
              put("comments", new ArrayList<Object>() {
                  private static final long serialVersionUID = 1L;
                  {
                    Iterator<Object> it = bugObj.getJSONArray("comments").iterator();
                    while(it.hasNext()){
                      add(parseComment((JSONObject)it.next()));
                    }
                  }});
              put("summary", bugObj.getString("summary"));
            }};
        buglist.put(bugId,bug);
        return bug;
      } catch (IOException ex) {
        throw new BugzillaAPIException(ex.toString());
      } finally {
	try {
	  response.close();
	} catch (IOException e) {
	  e.printStackTrace();
	}
      }
    } catch (URISyntaxException ex) {
      throw new BugzillaAPIException(ex.toString());
    }
  }


  /*
   * Returns a Map containing an Array of Maps.  Within the innermost Maps (which represent bugs), there's another
   * Map under the key "internals", which has a key "bug_status".  ugh.
   */
  public Object[] getBugs(Map<String, Object> values) throws BugzillaAPIException
  {
    throw new BugzillaAPIException("not implemented yet");
  }


  public Object[] getBugs(String name, Object value) throws BugzillaAPIException {
    throw new BugzillaAPIException("not implemented yet");
    // Map<String, Object> map = new HashMap<String, Object>();
    // map.put(name, value);
    // return getBugs(map);
  }


  public Map<String,Object> update_bug_status(String bug_id, IBugzillaAPI.bzState newState) throws BugzillaAPIException{
    throw new BugzillaAPIException("not implemented yet");
    // Map<String,Object> updates = new HashMap<String,Object>();
    // updates.put("bug_status", newState.toString());
    // return update_bug(bug_id, updates);
  }


  public Map<String,Object> update_bug(String bug_id, Map<String,Object> updates)throws BugzillaAPIException{
    throw new BugzillaAPIException("not implemented yet");
    // Map map = new HashMap<String,Object>();
    // return map;
  }


  public Map<String,Object> add_bug_comment(String bug_id, String comment) throws BugzillaAPIException{
    throw new BugzillaAPIException("not implemented yet");
    // Map<String,Object> map = new HashMap<String,Object>();
    // return map;
  }
}
