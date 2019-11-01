package org.synchrotron.core;

import java.util.function.BiPredicate;

import lombok.AllArgsConstructor;

//@Setter
//@Accessors(fluent = true, chain = true)
public class SyncRunnerConfiguration {

	public <T> Rule<T> ruleForKeyOfType(Class<T> clazz) {
		return new Rule<>(clazz);
	}

	public static void main(String[] args) {

		BiPredicate<String, String> bothStartsWithA = (a, b) -> a.startsWith("A") && b.startsWith("A");

		SyncRunnerConfiguration strategy = new SyncRunnerConfiguration()

				.ruleForKeyOfType(String.class).ifAnotherKeyOfType(String.class)
				.ifTrue((a, b) -> a.length() == b.length())
				.ifTrue(bothStartsWithA.and(bothStartsWithA).or(bothStartsWithA))

				.then(SyncRunnerStrategy.PUT_TO_QUEUE)

				.ruleForKeyOfType(Integer.class)
				.ifAnotherKeyOfType(Integer.class)
				.ifTrue(Integer::equals).then(SyncRunnerStrategy.INHERIT_RESULT);

	}

	enum SyncRunnerStrategy {
		PUT_TO_QUEUE,
		INHERIT_RESULT,
		REJECT
	}

	@AllArgsConstructor
	class Rule<T> {
		private Class<T> keyClass;

		public <K> SubRule<T, K> ifAnotherKeyOfType(Class<K> clazz) {
			return new SubRule<>(clazz);
		}

		@AllArgsConstructor
		class SubRule<T, K> {

			private Class<K> otherClass;

			public SubRule<T, K> ifTrue(BiPredicate<T, K> predicate) {
				return this;
			}

			public SyncRunnerConfiguration then(SyncRunnerStrategy strategy) {
				return SyncRunnerConfiguration.this;
			}

		}

	}

}
