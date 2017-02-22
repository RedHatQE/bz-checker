package com.redhat.qe.auto.bugzilla;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


import com.redhat.qe.auto.bugzilla.IBugzillaAPI;
import com.redhat.qe.auto.bugzilla.BugzillaAPIException;

import com.google.inject.Inject;

/**
 * Example code to retrieve a bugzilla bug's status, given its ID.  This is for future use with testng,
 * so that testng can decide whether to execute a test, based on the group annotation (which may contain
 * a bug id), and the status of that bug.  If the status is ON_QA, for example, it can be tested.<br>
 * Example Usage: if (BzChecker.getInstance().getBugState("12345") == IBugzillaAPI.bzState.ON_QA) {...
 * @author weissj
 *
 */
public class BzChecker {
	protected static Logger log = Logger.getLogger(BzChecker.class.getName());

  private final IBugzillaAPI bug;

	protected static IBugzillaAPI.bzState[] defaultFixedBugStates = new IBugzillaAPI.bzState[] {
			IBugzillaAPI.bzState.ON_QA,
			IBugzillaAPI.bzState.VERIFIED,
			IBugzillaAPI.bzState.RELEASE_PENDING,
			IBugzillaAPI.bzState.POST,
			IBugzillaAPI.bzState.CLOSED };
	protected static IBugzillaAPI.bzState[] fixedBugStates;

  @Inject
  BzChecker(IBugzillaAPI bug){
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

	public IBugzillaAPI.bzState getBugState(String bugId) throws BugzillaAPIException{
		return IBugzillaAPI.bzState.valueOf(getBugField(bugId, "status").toString());
	}

	public Object getBugField(String bugId, String fieldId) throws BugzillaAPIException{
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

	public void setBugState(String bugId, IBugzillaAPI.bzState state) {
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
	 * @throws BugzillaAPIException - when the bug state cannot be determined.
	 */
	public boolean isBugOpen(String bugId) throws BugzillaAPIException {
		IBugzillaAPI.bzState state = getBugState(bugId);

		for (IBugzillaAPI.bzState fixedBugState: fixedBugStates) {
			if (state.equals(fixedBugState)) return false;
		}
		return true;
	}


	protected IBugzillaAPI.bzState[] extractStates(String states) {
		String[] splits = states.split(",");
		List<IBugzillaAPI.bzState> list = new ArrayList<IBugzillaAPI.bzState>();
		for (String state: splits) {
			list.add(IBugzillaAPI.bzState.valueOf(state.trim().toUpperCase()));
		}
		return list.toArray(new IBugzillaAPI.bzState[] {});
	}
}
