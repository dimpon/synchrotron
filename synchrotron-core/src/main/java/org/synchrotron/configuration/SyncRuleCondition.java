package org.synchrotron.configuration;
import java.util.function.BiPredicate;

public interface SyncRuleCondition<T,K> {
	SyncRuleThen<T,K> and(BiPredicate<T,K> predicate);
}
