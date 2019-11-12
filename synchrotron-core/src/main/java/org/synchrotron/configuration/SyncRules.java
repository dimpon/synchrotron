package org.synchrotron.configuration;
import java.util.function.BiPredicate;

public class SyncRules<T, K> implements SyncRulesFor, SyncRuleAndFor<T, K>, SyncRuleWhen<T, K>, SyncRuleThen<T, K> {

	private final SyncRunnerConfigurationImpl configuration;
	String name;
	Class<T> rulesFor;
	Class<K> andFor;
	BiPredicate<T, K> condition;
	SyncRunnerStrategy strategy;

	SyncRules(SyncRunnerConfigurationImpl configuration) {
		this.configuration = configuration;
		this.name = "n/a";
	}

	SyncRules(String name, SyncRunnerConfigurationImpl configuration) {
		this.configuration = configuration;
		this.name = name;
	}

	@Override
	public <C> SyncRuleWhen<T, C> andFor(Class<C> clazz) {
		SyncRules<T, C> rule = new SyncRules<>(this.name, this.configuration);
		rule.rulesFor = this.rulesFor;
		rule.andFor = clazz;
		return rule;
	}

	@Override
	public SyncRuleThen<T, K> when(BiPredicate<T, K> predicate) {
		SyncRules<T, K> rule = new SyncRules<>(this.name, this.configuration);
		rule.rulesFor = this.rulesFor;
		rule.andFor = this.andFor;
		rule.condition = predicate;
		return rule;
	}

	@Override
	public SyncRunnerConfiguration then(SyncRunnerStrategy strategy) {
		SyncRules<T, K> rule = new SyncRules<>(this.name, this.configuration);
		rule.rulesFor = this.rulesFor;
		rule.andFor = this.andFor;
		rule.condition = this.condition;
		rule.strategy = strategy;
		this.configuration.addRule(rule.rulesFor, rule);
		return this.configuration;
	}

	@Override
	public <C> SyncRuleAndFor<C, Object> ruleFor(Class<C> clazz) {
		SyncRules<C, Object> rule = new SyncRules<>(this.name, this.configuration);
		rule.rulesFor = clazz;
		return rule;
	}
}
