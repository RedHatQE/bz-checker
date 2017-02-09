package com.redhat.qe.auto.bugzilla;

import java.lang.Exception;

public class BugzillaAPIException extends Exception {
	/**
	 * A generic exception.
	 */
	private static final long serialVersionUID = 743512415372045567L;
  public BugzillaAPIException(String message) {
    super(message);
  }
};
