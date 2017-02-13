package com.redhat.qe.auto.bugzilla;

import com.redhat.qe.auto.bugzilla.BzChecker;
import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.AbstractModule;

public class OldBzChecker {
  public static Injector injector = null;

  /* An old version of using of Bz-Checker. Static factory */
  public static BzChecker getInstance() {
    if (injector == null){
      injector = Guice.createInjector(new XMLRPCModule());
    }
    BzChecker checker = injector.getInstance(BzChecker.class);
    return checker;
  }

  public static BzChecker getInstance(AbstractModule module) {
    if (injector == null){
      injector = Guice.createInjector(module);
    }
    BzChecker checker = injector.getInstance(BzChecker.class);
    return checker;
  }
}
