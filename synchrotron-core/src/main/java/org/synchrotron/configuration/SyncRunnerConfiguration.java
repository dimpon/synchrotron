package org.synchrotron.configuration;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.function.BiPredicate;

//@Setter
//@Accessors(fluent = true, chain = true)
public class SyncRunnerConfiguration implements SyncRulesFactory {

	/*public <T> Rule<T> rulesForKey(Class<T> clazz) {
		return new Rule<>(clazz);
	}*/

	static BiPredicate<String, String> bothA = (a, b) -> a.equalsIgnoreCase("a") && a.equalsIgnoreCase("b");
	static BiPredicate<String, String> bothB = (a, b) -> b.equalsIgnoreCase("a") && b.equalsIgnoreCase("b");
	static BiPredicate<String, String> bothC = (a, b) -> b.equalsIgnoreCase("c") && b.equalsIgnoreCase("c");

	public static void main(String[] args) {

		SyncRunnerConfiguration strategy = new SyncRunnerConfiguration();

		strategy
				.rulesFor(String.class)
				.whenFindType(String.class)
				.and(bothA.or(bothB).or(bothC))
				.then(SyncRunnerStrategy.PUT_TO_QUEUE)

				.whenFindType(Integer.class).and((s, i) -> s.length() == i).then(SyncRunnerStrategy.REJECT);

		strategy
				.rulesFor(BigDecimal.class)
				.when((b, o) -> o.equals(b)).then(SyncRunnerStrategy.INHERIT_RESULT)

				.rulesFor(Array.class);







		/*BiPredicate<String, String> bothStartsWithA = (a, b) -> a.startsWith("A") && b.startsWith("A");

		SyncRunnerConfiguration strategy = new SyncRunnerConfiguration()

				.rulesForKey(String.class)
				.and(String.class)
				.and((a, b) -> a.length() == b.length())
				.and(bothStartsWithA.and(bothStartsWithA).or(bothStartsWithA))

				.then(SyncRunnerStrategy.PUT_TO_QUEUE)

				.rulesForKey(Integer.class)
				.and(Integer.class)
				.and(Integer::equals).then(SyncRunnerStrategy.INHERIT_RESULT);

		Object d = new Object();

		Class<Object> c = Object.class;

		boolean instance = c.isInstance(d);
*/
	}

	@Override
	public <T> SyncRuleWhenType<T, Object> rulesFor(Class<T> clazz) {
		return new SyncRules<>();
	}

	/*enum SyncRunnerStrategy {
		PUT_TO_QUEUE,
		INHERIT_RESULT,
		REJECT
	}

	@AllArgsConstructor
	class Rule<T> {
		private Class<T> keyClass;

		public <K> SubRule<T, K> and(Class<K> clazz) {
			return new SubRule<>(clazz);
		}

		@AllArgsConstructor
		class SubRule<T, K> {

			private Class<K> otherClass;

			public SubRule<T, K> and(BiPredicate<T, K> predicate) {
				return this;
			}

			public SyncRunnerConfiguration then(SyncRunnerStrategy strategy) {
				return SyncRunnerConfiguration.this;
			}

		}

	}
*/
}
