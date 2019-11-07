package org.synchrotron.configuration;
public interface SyncRuleThen<T,K> {

	SyncRuleWhenType<T, Object> then(SyncRunnerStrategy strategy);

}
