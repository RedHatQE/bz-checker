package com.redhat.qe.auto.bugzilla;

import com.redhat.qe.auto.bugzilla.BugzillaAPIException;

import java.util.Map;

public interface IBugzillaAPI {

  public enum bzState { NEW, ASSIGNED, MODIFIED, ON_DEV, ON_QA, VERIFIED, FAILS_QA, RELEASE_PENDING, POST, CLOSED };
  
  void connectBZ() throws BugzillaAPIException;

  public int login(String userid, String password) throws BugzillaAPIException;

  Map<String, Object> getBug(String bugId) throws BugzillaAPIException;

  Object[] getBugs(String name, Object value) throws BugzillaAPIException;

  Object[] getBugs(Map<String, Object> values) throws BugzillaAPIException;

  Map<String,Object> update_bug_status(String bug_id, bzState newState) throws BugzillaAPIException;

  Map<String,Object> update_bug(String bug_id, Map<String,Object> updates) throws BugzillaAPIException;

  Map<String,Object> add_bug_comment(String bug_id, String comment) throws BugzillaAPIException;
};
