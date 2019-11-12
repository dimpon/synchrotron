package org.synchrotron.configuration;
import java.util.function.BiPredicate;

public interface SyncRuleWhen<T,K> {
	SyncRuleThen<T,K> when(BiPredicate<T, K> predicate);
}
