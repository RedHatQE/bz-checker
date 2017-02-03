package com.redhat.qe.auto.bugzilla;

import java.util.Map;
import java.lang.Exception;

interface BugzillaAPI {

  public enum bzState { NEW, ASSIGNED, MODIFIED, ON_DEV, ON_QA, VERIFIED, FAILS_QA, RELEASE_PENDING, POST, CLOSED };

  void connectBZ() throws Exception;

  public int login(String userid, String password) throws Exception;

  Map<String, Object> getBug(String bugId) throws Exception;

  Object[] getBugs(String name, Object value) throws Exception;

  Object[] getBugs(Map<String, Object> values) throws Exception;

  Map update_bug_status(String bug_id, bzState newState) throws Exception;

  Map update_bug(String bug_id, Map<String,Object> updates) throws Exception;

  Map add_bug_comment(String bug_id, String comment) throws Exception;
};
