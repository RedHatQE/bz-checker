package com.redhat.qe.auto.bugzilla;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.lang.Exception;
import java.net.URI;
import java.util.stream.Stream;
import java.util.Iterator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.CloseableHttpResponse;
import com.redhat.qe.auto.bugzilla.IBugzillaAPI;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class REST_API implements IBugzillaAPI {
	protected static Logger log = Logger.getLogger(REST_API.class.getName());

  private Map<String,Map> buglist;
  private URI uri;
  private CloseableHttpClient httpclient;

  public static String[] bugFields = {"status",
                                      "product",
                                      "keywords",
                                      "comments",
                                      "qa_contact",
                                      "tags"};


  public REST_API() throws Exception {
    buglist = new HashMap<String,Map>();
  }

 
  public void connectBZ() throws Exception {
    uri = new URI(System.getProperty("bugzilla.url") + "/rest");
    CredentialsProvider credsProvider = new BasicCredentialsProvider();
    credsProvider.setCredentials(AuthScope.ANY,
                                 new UsernamePasswordCredentials(System.getProperty("bugzilla.login"),
                                                                 System.getProperty("bugzilla.password")));
    httpclient = HttpClients.custom()
      .setDefaultCredentialsProvider(credsProvider)
      .build();
  }


  public int login(String userid, String password) throws Exception{
    return 0;
  }

  protected Map<String,Object> parseComment(JSONObject commentObj) throws Exception {
    return new HashMap<String,Object> () {{
      Iterator<String> it = commentObj.keys();
      while(it.hasNext()){
        String key = it.next();
        put(key, commentObj.get(key));
      }
    }};
  }


  public Map<String, Object> getBug(String bugId) throws Exception {
    URI newURI = new URI(uri.toString() + "/bug/" + bugId
                         + "?api_key=" + System.getProperty("bugzilla.apikey")
                         + "&include_fields=" + String.join(",", bugFields));
    if( System.getProperty("bugzilla.cache","false").equals("true")){
      Map<String,Object> bug = buglist.get(bugId);
      if(bug != null) {
        log.finer("using cached bugzilla " + bugId);
        return bug;
      }
    }
    CloseableHttpResponse response = httpclient.execute(new HttpGet(newURI.normalize()));
    try {
      if(response.getStatusLine().getStatusCode() != 200){
        throw new Exception("Wrong status code returned. The status line was: "
                            + response.getStatusLine());
      };
      JSONTokener tokener = new JSONTokener(response.getEntity().getContent());
      JSONObject root = new JSONObject(tokener);
      JSONArray bugs = (JSONArray) root.get("bugs");
      JSONObject bugObj = (JSONObject) bugs.get(0);
      Map<String,Object> bug = new HashMap<String,Object>() {{
          put("status",bugObj.getString("status"));
          put("product",bugObj.getString("product"));
          put("qa_contact", bugObj.getString("qa_contact"));
          put("keywords", new ArrayList<String> () {{
            JSONArray keywords = bugObj.getJSONArray("keywords");
            Iterator<Object> it = keywords.iterator();
            while(it.hasNext()){
              add((String)it.next());
            }
          }});
          put("comments",  new ArrayList<Object>() {{
            Iterator<Object> it = bugObj.getJSONArray("comments").iterator();
            while(it.hasNext()){
              add(parseComment((JSONObject)it.next()));
            }
          }});
        }};
      buglist.put(bugId,bug);
      return bug;
    } finally {
      response.close();
    }
  }


  /*
   * Returns a Map containing an Array of Maps.  Within the innermost Maps (which represent bugs), there's another
   * Map under the key "internals", which has a key "bug_status".  ugh.
   */
  public Object[] getBugs(Map<String, Object> values) throws Exception
  {
    throw new Exception("not implemented yet");
    //return null;
  }


  public Object[] getBugs(String name, Object value) throws Exception {
    throw new Exception("not implemented yet");
    // Map<String, Object> map = new HashMap<String, Object>();
    // map.put(name, value);
    // return getBugs(map);
  }


  public Map update_bug_status(String bug_id, IBugzillaAPI.bzState newState) throws Exception{
    throw new Exception("not implemented yet");
    // Map<String,Object> updates = new HashMap<String,Object>();
    // updates.put("bug_status", newState.toString());
    // return update_bug(bug_id, updates);
  }


  public Map update_bug(String bug_id, Map<String,Object> updates)throws Exception{
    throw new Exception("not implemented yet");
    // Map map = new HashMap<String,Object>();
    // return map;
  }


  public Map add_bug_comment(String bug_id, String comment) throws Exception{
    throw new Exception("not implemented yet");
    // Map<String,Object> map = new HashMap<String,Object>();
    // return map;
  }
}
