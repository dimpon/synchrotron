package org.synchrotron.configuration;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

//@Setter
//@Accessors(fluent = true, chain = true)
public class SyncRunnerConfiguration implements SyncRulesFor {

	/*public <T> Rule<T> rulesForKey(Class<T> clazz) {
		return new Rule<>(clazz);
	}*/

	static BiPredicate<String, String> bothA = (a, b) -> a.equalsIgnoreCase("a") && a.equalsIgnoreCase("b");
	static BiPredicate<String, String> bothB = (a, b) -> b.equalsIgnoreCase("a") && b.equalsIgnoreCase("b");
	static BiPredicate<String, String> bothBb = (a, b) -> b.equalsIgnoreCase("a") && b.equalsIgnoreCase("b");
	static BiPredicate<String, String> bothC = (a, b) -> b.equalsIgnoreCase("c") && b.equalsIgnoreCase("c");
	private ConcurrentHashMap<Class<?>, Set<SyncRules<?, ?>>> rules = new ConcurrentHashMap<>();

	public static void main(String[] args) {

		SyncRunnerConfiguration config = new SyncRunnerConfiguration();

		config
				.rulesFor(String.class)
				.andFor(String.class)
				.when(bothA.or(bothB.and(bothBb)).or(bothC))
				.then(SyncRunnerStrategy.PUT_TO_QUEUE)

				.rulesFor(String.class)
				.andFor(Integer.class)
				.when((s, i) -> s.length() == i)
				.then(SyncRunnerStrategy.REJECT);

		config
				.rulesFor(BigDecimal.class)
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
	public <T> SyncRuleAndFor<T, Object> rulesFor(Class<T> clazz) {
		SyncRules<T, Object> rule = new SyncRules<>(this);
		rule.rulesFor = clazz;
		return rule;
	}

}
