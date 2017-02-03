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
import java.net.URL;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.xmlrpc.XmlRpcException;

import com.redhat.qe.xmlrpc.BaseObject;
import com.redhat.qe.xmlrpc.Session;

import com.redhat.qe.auto.bugzilla.BugzillaAPI;


public class XMLRPC_API extends BaseObject implements BugzillaAPI {
	protected static Logger log = Logger.getLogger(XMLRPC_API.class.getName());

  private String BZ_URL;
  private Map<String,Map> buglist;


  public XMLRPC_API(){
    listMethod = "Bug.get_bugs";
    buglist = new HashMap<String,Map>();
  }


  public void connectBZ() throws Exception{
    BZ_URL = System.getProperty("bugzilla.url");
    session = new Session(System.getProperty("bugzilla.login"), System.getProperty("bugzilla.password"), new URL(BZ_URL));
    session.init();
    // initiate a login here because some bugzilla projects (e.g. Cloud Enablement Tools) are not anonymously
    // readable which will result in org.apache.xmlrpc.XmlRpcException: You are not authorized to access bug #
    // when calling lookupBugAndSkipIfOpen.  For reliability, we need to login.  jsefler 3/16/09
    login(System.getProperty("bugzilla.login"), System.getProperty("bugzilla.password"));
  }


  public int login(String userid, String password) throws Exception{
    Map<String,Object> main = new HashMap<String,Object>();
    main.put("login", userid);
    main.put("password", password);
    Map map = (Map) this.callXmlrpcMethod("User.login", main);
    return (Integer)map.get("id");
  }


  public Map<String, Object> getBug(String bugId) throws Exception {
    Map bug = null;
    if ( System.getProperty("bugzilla.cache", "false").equals( "true" ) ) {
      bug = buglist.get(bugId);
      if (bug!=null) log.finer("Using cached bugzilla "+bugId);
    }
    if ( bug == null ) {
      Map<String,Object> ids = new HashMap<String,Object>();
      ids.put("ids", bugId);
      Map map = (Map) this.callXmlrpcMethod("Bug.get", ids);
      Object[] oarray = (Object[])map.get("bugs");
      bug = (Map)oarray[0];
      buglist.put(bugId,bug);
    }
    return (Map) bug;
  }


  /*
   * Returns a Map containing an Array of Maps.  Within the innermost Maps (which represent bugs), there's another
   * Map under the key "internals", which has a key "bug_status".  ugh.
   */
  public Object[] getBugs(Map<String, Object> values) throws Exception
  {
    //some Testopia objects have no listing mechanism
    if(listMethod == null)
      return null;

    Map map = (Map) this.callXmlrpcMethod(listMethod, values);
    return (Object[])map.get("bugs");
    //return result;
  }


  public Object[] getBugs(String name, Object value) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put(name, value);
    return getBugs(map);
  }


  public Map update_bug_status(String bug_id, BugzillaAPI.bzState newState) throws Exception{
    Map<String,Object> updates = new HashMap<String,Object>();
    updates.put("bug_status", newState.toString());
    return update_bug(bug_id, updates);
  }


  public Map update_bug(String bug_id, Map<String,Object> updates)throws Exception{
    Map<String,Object> main = new HashMap<String,Object>(); 
    main.put("updates", updates);
    main.put("ids", Integer.parseInt(bug_id));
    Map map = (Map) this.callXmlrpcMethod("Bug.update", main);
    return map;
  }


  public Map add_bug_comment(String bug_id, String comment) throws Exception{
    Map<String,Object> main = new HashMap<String,Object>(); 


    main.put("id", Integer.parseInt(bug_id));
    main.put("comment", comment);
    Map map = (Map) this.callXmlrpcMethod("Bug.add_comment", main);
    //Map map = (Map) this.callXmlrpcMethod("bug.add_comment", Integer.parseInt(bug_id), comment);
    
    //System.out.println(map);
    return map;
  }
}
