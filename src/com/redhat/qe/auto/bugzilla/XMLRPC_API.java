package com.redhat.qe.auto.bugzilla;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.net.URL;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.xmlrpc.XmlRpcException;

import com.redhat.qe.xmlrpc.BaseObject;
import com.redhat.qe.xmlrpc.Session;

import com.redhat.qe.auto.bugzilla.IBugzillaAPI;
import com.redhat.qe.auto.bugzilla.BugzillaAPIException;


public class XMLRPC_API extends BaseObject implements IBugzillaAPI {
	protected static Logger log = Logger.getLogger(XMLRPC_API.class.getName());

  private String BZ_URL;
  private Map<String,Map<String,Object>> buglist;


  public XMLRPC_API(){
    listMethod = "Bug.get_bugs";
    buglist = new HashMap<String,Map<String,Object>>();
  }


  public void connectBZ() throws BugzillaAPIException{
    BZ_URL = System.getProperty("bugzilla.url");
    try {
      session = new Session(System.getProperty("bugzilla.login"), System.getProperty("bugzilla.password"), new URL(BZ_URL));
      session.init();
      // initiate a login here because some bugzilla projects (e.g. Cloud Enablement Tools) are not anonymously
      // readable which will result in org.apache.xmlrpc.XmlRpcException: You are not authorized to access bug #
      // when calling lookupBugAndSkipIfOpen.  For reliability, we need to login.  jsefler 3/16/09
      login(System.getProperty("bugzilla.login"), System.getProperty("bugzilla.password"));
    } catch (IOException io) {
      throw new BugzillaAPIException(io.toString());
    } catch (XmlRpcException ex) {
      throw new BugzillaAPIException(ex.toString());
    } catch (GeneralSecurityException gex) {
      throw new BugzillaAPIException(gex.toString());
    }
  }


  public int login(String userid, String password) throws BugzillaAPIException {
    try{
      Map<String,Object> main = new HashMap<String,Object>();
      main.put("login", userid);
      main.put("password", password);

			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>) callXmlrpcMethod("User.login", main);
      return (Integer)map.get("id");
    } catch (XmlRpcException ex) {
      throw new BugzillaAPIException(ex.toString());
    }
  }


	@SuppressWarnings("unchecked")
	public Map<String, Object> getBug(String bugId) throws BugzillaAPIException {
    Map<String,Object> bug = null;
    if ( System.getProperty("bugzilla.cache", "false").equals( "true" ) ) {
      bug = buglist.get(bugId);
      if (bug!=null) log.finer("Using cached bugzilla "+bugId);
    }
    if ( bug == null ) {
      try {
        Map<String,Object> ids = new HashMap<String,Object>();
        ids.put("ids", bugId);
				Map<String,Object> map = (Map<String,Object>) callXmlrpcMethod("Bug.get", ids);
        Object[] oarray = (Object[])map.get("bugs");
        bug = (Map<String,Object>)oarray[0];
        buglist.put(bugId,bug);
      } catch (XmlRpcException ex) {
        throw new BugzillaAPIException(ex.toString());
      }
    }
    return (Map<String,Object>) bug;
  }


  /*
   * Returns a Map containing an Array of Maps.  Within the innermost Maps (which represent bugs), there's another
   * Map under the key "internals", which has a key "bug_status".  ugh.
   */
	@SuppressWarnings("unchecked")
	public Object[] getBugs(Map<String, Object> values) throws BugzillaAPIException
  {
    //some Testopia objects have no listing mechanism
    if(listMethod == null)
      return null;

		Map<String, Object> map;
		try {
			map = (Map<String, Object>) callXmlrpcMethod(listMethod, values);
	    return (Object[])map.get("bugs");
		} catch (XmlRpcException e) {
      throw new BugzillaAPIException(e.toString());
		}
  }


  public Object[] getBugs(String name, Object value) throws BugzillaAPIException {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put(name, value);
    return getBugs(map);
  }


	public Map<String,Object> update_bug_status(String bug_id, IBugzillaAPI.bzState newState) throws BugzillaAPIException{
    Map<String,Object> updates = new HashMap<String,Object>();
    updates.put("bug_status", newState.toString());
    return update_bug(bug_id, updates);
  }


  public Map<String,Object> update_bug(String bug_id, Map<String,Object> updates)throws BugzillaAPIException{
    try {
      Map<String,Object> main = new HashMap<String,Object>(); 
      main.put("updates", updates);
      main.put("ids", Integer.parseInt(bug_id));
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>) callXmlrpcMethod("Bug.update", main);
      return map;
    } catch (XmlRpcException ex) {
      throw new BugzillaAPIException(ex.toString());
    }
  }


  public Map<String,Object> add_bug_comment(String bug_id, String comment) throws BugzillaAPIException{
    try {
      Map<String,Object> main = new HashMap<String,Object>(); 
      main.put("id", Integer.parseInt(bug_id));
      main.put("comment", comment);
      @SuppressWarnings("unchecked")
      Map<String,Object> map = (Map<String,Object>) callXmlrpcMethod("Bug.add_comment", main);
      return map;
    } catch (XmlRpcException ex) {
      throw new BugzillaAPIException(ex.toString());
    }
  }
}
