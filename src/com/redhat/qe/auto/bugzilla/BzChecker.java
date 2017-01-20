package com.redhat.qe.auto.bugzilla;


import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;

import com.redhat.qe.xmlrpc.BaseObject;
import com.redhat.qe.xmlrpc.Session;

import com.redhat.qe.auto.bugzilla.BugzillaAPI;

import com.google.inject.Inject;

/**
 * Example code to retrieve a bugzilla bug's status, given its ID.  This is for future use with testng,
 * so that testng can decide whether to execute a test, based on the group annotation (which may contain
 * a bug id), and the status of that bug.  If the status is ON_QA, for example, it can be tested.<br>
 * Example Usage: if (BzChecker.getInstance().getBugState("12345") == BugzillaAPI.bzState.ON_QA) {...
 * @author weissj
 *
 */
public class BzChecker {
	protected static Logger log = Logger.getLogger(BzChecker.class.getName());

  private final BugzillaAPI bug;

	protected static BugzillaAPI.bzState[] defaultFixedBugStates = new BugzillaAPI.bzState[] {
			BugzillaAPI.bzState.ON_QA,
			BugzillaAPI.bzState.VERIFIED,
			BugzillaAPI.bzState.RELEASE_PENDING,
			BugzillaAPI.bzState.POST,
			BugzillaAPI.bzState.CLOSED };
	protected static BugzillaAPI.bzState[] fixedBugStates;

  @Inject
  BzChecker(BugzillaAPI bug){
    this.bug = bug;
    init();
  }

  private void init() {
		try {
      bug.connectBZ();

			//read in custom "fixed" bug states if any
			String fixedStates = System.getProperty("bugzilla.fixedBugStates");
			if (fixedStates != null && fixedStates.length() >0) {
				fixedBugStates = extractStates(fixedStates);
			}
			else fixedBugStates = defaultFixedBugStates;

		}catch(Exception e){
			throw new RuntimeException("Could not initialize BzChecker." ,e);
		}
	}

	public BugzillaAPI.bzState getBugState(String bugId) throws Exception{
		return BugzillaAPI.bzState.valueOf(getBugField(bugId, "status").toString());
	}

	public Object getBugField(String bugId, String fieldId) throws Exception{
		/*Object[] bugs = null;
		try {
			bugs = bug.getBugs("ids", new Object[] {bugId});
		}catch(Exception e){
			throw new RuntimeException("Could not retrieve bug " + bugId + " from bugzilla." ,e);
		}
		log.finer("Retrieved bugs: " + Arrays.deepToString(bugs));
		if (bugs.length ==0) throw new IllegalStateException("No bug found matching ID " + bugId);
		else if (bugs.length > 1) throw new IllegalStateException("Multiple matches found for bug ID " + bugId);
		else {
			Object thisbug = bugs[0];
			Map bmap = (Map)thisbug;

			log.finer("Found bug: " + thisbug.toString() );
			Map internals = (Map)bmap.get("internals");
			Object fieldValue = internals.get(fieldId);
			log.finer("Bug field " + fieldId + " of " + bugId + " is " + fieldValue.toString());
			return fieldValue;
		}*/
		return bug.getBug(bugId).get(fieldId);
	}

	public void setBugState(String bugId, BugzillaAPI.bzState state) {
		try {
			bug.update_bug_status(bugId, state);
		}
		catch(Exception e){
			throw new RuntimeException("Could not set bug status " + bugId + " in bugzilla." ,e);
		}
	}
	public void login(String userid, String password) {
		try {
			bug.login(userid, password);
		}
		catch(Exception e){
			throw new RuntimeException("Could not log in to bugzilla as " + userid ,e);
		}
	}

	public void addComment(String bugId, String comment){
		try {
			bug.add_bug_comment(bugId, comment);
		}
		catch(Exception e){
			throw new RuntimeException("Could not add comment to bug " + bugId ,e);
		}
	}

	public void addKeywords(String bugId, String... keywords){
		editKeywords(bugId, true, keywords);
	}
	public void deleteKeywords(String bugId, String... keywords){
		editKeywords(bugId, true, keywords);
	}

	protected void editKeywords(String bugId, boolean add, String... keywords){
		try {
			Map<String,Object> updates = new HashMap<String,Object>();
			updates.put(add?"add_keyword":"delete_keyword", keywords);
			bug.update_bug(bugId, updates);
		}
		catch(Exception e){
			throw new RuntimeException("Could not " + (add? "add":"remove") + " keywords for bug " + bugId ,e);
		}
	}

	/**
	 * @param bugId
	 * @return
	 * 	 	true (when bug is NOT in any of these states: ON_QA, VERIFIED, RELEASE_PENDING, POST, CLOSED)<br>
	 * 		false (when IS in any one of these states: ON_QA, VERIFIED, RELEASE_PENDING, POST, CLOSED)<br>
	 * @throws XmlRpcException - when the bug state cannot be determined.
	 */
	public boolean isBugOpen(String bugId) throws Exception {
		BugzillaAPI.bzState state = getBugState(bugId);

		for (BugzillaAPI.bzState fixedBugState: fixedBugStates) {
			if (state.equals(fixedBugState)) return false;
		}
		return true;
	}


	protected BugzillaAPI.bzState[] extractStates(String states) {
		String[] splits = states.split(",");
		List<BugzillaAPI.bzState> list = new ArrayList<BugzillaAPI.bzState>();
		for (String state: splits) {
			list.add(BugzillaAPI.bzState.valueOf(state.trim().toUpperCase()));
		}
		return list.toArray(new BugzillaAPI.bzState[] {});
	}
}
