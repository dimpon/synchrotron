package org.synchrotron.configuration;

public interface SyncRulesFactory {

	<T> SyncRuleWhenType<T, Object> rulesFor(Class<T> clazz);
}
