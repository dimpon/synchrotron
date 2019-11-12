package org.synchrotron.configuration;

public interface SyncRuleAndFor<T, K> extends SyncRuleWhen<T, K> {
	<C> SyncRuleWhen<T, C> andFor(Class<C> clazz);
}
