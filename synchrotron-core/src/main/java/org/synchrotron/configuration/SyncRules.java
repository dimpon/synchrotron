package org.synchrotron.configuration;
import java.util.function.BiPredicate;

public class SyncRules<T, K> implements SyncRuleWhenType<T, K>, SyncRuleCondition<T, K>,SyncRuleThen<T, K> {

	@Override
	public <C> SyncRuleCondition<T, C> whenFindType(Class<C> clazz) {
		return null;
	}

	@Override
	public SyncRuleThen<T, K> when(BiPredicate<T, K> predicate) {
		return null;
	}

	@Override
	public SyncRuleThen<T, K> and(BiPredicate<T, K> predicate) {
		return null;
	}

	@Override
	public SyncRuleWhenType<T, Object> then(SyncRunnerStrategy strategy) {
		return null;
	}

	@Override
	public <T> SyncRuleWhenType<T, Object> rulesFor(Class<T> clazz) {
		return null;
	}
}
