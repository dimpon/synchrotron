package org.synchrotron.configuration;

import java.util.function.BiPredicate;

public interface SyncRuleAndFor<T, K> /*extends SyncRuleWhen<T, K>*/ {
	<C> SyncRuleWhen<T, C> andFor(Class<C> clazz);
	SyncRuleThen<T,K> when(BiPredicate<T, K> predicate);
}
