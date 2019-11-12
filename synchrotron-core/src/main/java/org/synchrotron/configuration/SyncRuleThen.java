package org.synchrotron.configuration;
public interface SyncRuleThen<T,K> {

	SyncRunnerConfiguration then(SyncRunnerStrategy strategy);

}
