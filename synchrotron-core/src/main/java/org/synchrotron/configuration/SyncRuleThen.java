package org.synchrotron.configuration;
public interface SyncRuleThen<T,K> {

	SyncRulesFor then(SyncRunnerStrategy strategy);

}
