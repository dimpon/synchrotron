package org.synchrotron.configuration;
public interface SyncRunnerConfiguration {

	SyncRulesFor newRule();

	SyncRulesFor newRule(String name);

}
