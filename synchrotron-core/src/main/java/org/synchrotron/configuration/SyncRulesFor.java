package org.synchrotron.configuration;

public interface SyncRulesFor {

	<T> SyncRuleAndFor<T, Object> rulesFor(Class<T> clazz);
}
