package com.redhat.qe.auto.bugzilla;

import java.lang.Exception;

/* Guice module for using XMLRPC_API as API for BzChecker */
import com.redhat.qe.auto.bugzilla.BzChecker;
import com.redhat.qe.auto.bugzilla.IBugzillaAPI;
import com.redhat.qe.auto.bugzilla.XMLRPC_API;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

public class XMLRPCModule extends AbstractModule {

  @Override
  protected void configure(){
    /* We can use XMLRPC_API class in every place where
       BugzillaAPI implementation is needed. */
    bind(IBugzillaAPI.class).to(XMLRPC_API.class).in(Singleton.class);
  }


  public void  main() throws Exception {
    Injector injector = Guice.createInjector(new XMLRPCModule());
    BzChecker checker = injector.getInstance(BzChecker.class);
    System.out.println(checker.getBugState("1"));
  }
}
