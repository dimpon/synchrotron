package org.synchrotron.autoconfigure;

import org.synchrotron.configuration.SyncRunnerConfiguration;

public interface SyncRunnerConfigurerAdapter {
	default void configure(SyncRunnerConfiguration configuration) throws Exception{}
}
