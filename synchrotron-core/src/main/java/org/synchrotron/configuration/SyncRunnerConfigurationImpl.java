package org.synchrotron.configuration;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

//@Setter
//@Accessors(fluent = true, chain = true)
public class SyncRunnerConfigurationImpl implements SyncRunnerConfiguration {

	/*public <T> Rule<T> rulesForKey(Class<T> clazz) {
		return new Rule<>(clazz);
	}*/

	static BiPredicate<String, String> bothA = (a, b) -> a.equalsIgnoreCase("a") && a.equalsIgnoreCase("b");
	static BiPredicate<String, String> bothB = (a, b) -> b.equalsIgnoreCase("a") && b.equalsIgnoreCase("b");
	static BiPredicate<String, String> bothBb = (a, b) -> b.equalsIgnoreCase("a") && b.equalsIgnoreCase("b");
	static BiPredicate<String, String> bothC = (a, b) -> b.equalsIgnoreCase("c") && b.equalsIgnoreCase("c");
	private ConcurrentHashMap<Class<?>, Set<SyncRules<?, ?>>> rules = new ConcurrentHashMap<>();

	public static void main(String[] args) {

		SyncRunnerConfiguration config = new SyncRunnerConfigurationImpl();


		config
				.newRule()
				.ruleFor(String.class)
				.andFor(String.class)
				.when(bothA.or(bothB.and(bothBb)).or(bothC))
				.then(SyncRunnerStrategy.PUT_TO_QUEUE)

				.newRule("new rule")
				.ruleFor(String.class)
				.andFor(Integer.class)
				.when((s, i) -> s.length() == i)
				.then(SyncRunnerStrategy.REJECT);

		config
				.newRule()
				.ruleFor(BigDecimal.class)
				.when((b, o) -> o.equals(b))
				.then(SyncRunnerStrategy.INHERIT_RESULT);

		System.out.printf("");

	}

	<T> void addRule(Class<T> clazz, SyncRules<T, ?> rule) {
		rules.compute(clazz, (aClass, rules) -> {
			if (rules != null) {
				rules.add(rule);
				return rules;
			} else {
				Set<SyncRules<?, ?>> newRules = new HashSet<>();
				newRules.add(rule);
				return newRules;
			}
		});
	}

	@Override
	public SyncRulesFor newRule() {
		return new SyncRules<Object, Object>(this);
	}

	@Override
	public SyncRulesFor newRule(String name) {
		return  new SyncRules<Object, Object>(name, this);
	}
}
