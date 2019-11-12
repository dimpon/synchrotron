package org.synchrotron.configuration;
import java.util.function.BiPredicate;

public class SyncRules<T, K> implements SyncRuleAndFor<T, K>, SyncRuleWhen<T, K>, SyncRuleThen<T, K> {

	private final SyncRunnerConfiguration configuration;
	Class<T> rulesFor;
	Class<K> andFor;
	BiPredicate<T, K> condition;
	SyncRunnerStrategy strategy;

	SyncRules(SyncRunnerConfiguration configuration) {
		this.configuration = configuration;

	}

	@Override
	public <C> SyncRuleWhen<T, C> andFor(Class<C> clazz) {
		SyncRules<T, C> rule = new SyncRules<>(this.configuration);
		rule.rulesFor = this.rulesFor;
		rule.andFor = clazz;
		return rule;
	}

	@Override
	public SyncRuleThen<T, K> when(BiPredicate<T, K> predicate) {
		SyncRules<T, K> rule = new SyncRules<>(this.configuration);
		rule.rulesFor = this.rulesFor;
		rule.andFor = this.andFor;
		rule.condition = predicate;
		return rule;
	}

	@Override
	public SyncRulesFor then(SyncRunnerStrategy strategy) {
		SyncRules<T, K> rule = new SyncRules<>(this.configuration);
		rule.rulesFor = this.rulesFor;
		rule.andFor = this.andFor;
		rule.condition = this.condition;
		rule.strategy = strategy;
		this.configuration.addRule(rule.rulesFor, rule);
		return this.configuration;
	}
}
