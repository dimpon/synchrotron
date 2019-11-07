package org.synchrotron.configuration;

import java.util.function.BiPredicate;

public interface SyncRuleWhenType<T, K> extends SyncRulesFactory{
	<C> SyncRuleCondition<T, C> whenFindType(Class<C> clazz);

	SyncRuleThen<T,K> when(BiPredicate<T, K> predicate);

}
