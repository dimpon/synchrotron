package org.synchrotron.configuration;

public interface SyncRulesFor {

	<T> SyncRuleAndFor<T, Object> ruleFor(Class<T> clazz);
}
