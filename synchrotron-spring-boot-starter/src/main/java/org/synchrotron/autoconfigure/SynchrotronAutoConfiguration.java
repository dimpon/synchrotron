package org.synchrotron.autoconfigure;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.synchrotron.core.SyncRunner;
import org.synchrotron.core.SyncRunnerImpl;

@Configuration
@ConditionalOnClass(SyncRunner.class)
public class SynchrotronAutoConfiguration {


	@Bean
	@ConditionalOnMissingBean
	public SyncRunner syncRunner() {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		return new SyncRunnerImpl(executorService);
	}

}
