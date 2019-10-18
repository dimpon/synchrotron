package org.synchrotron.core;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncRunnerImpl implements SyncRunner {

	private static final Logger log = LoggerFactory.getLogger(SyncRunnerImpl.class);

	private final ExecutorService executor;
	private final CopyOnWriteArrayList<ImmutablePair<?, CompletableFuture<?>>> tasks = new CopyOnWriteArrayList<>();



	public SyncRunnerImpl(ExecutorService executor) {
		this.executor = executor;
	}



	/**
	 * Method runs a task in async mode.
	 * It checks all tasks already run and waits tasks are matched by predicate.
	 * Predicate checks key associated with a task.
	 * If there is no tasks for those predicate returns true,
	 * new task is run immediately in new thread.
	 *
	 * @param key object associated with task
	 * @param task async task to run
	 * @param predicate predicate for finding tasks which are needed to wait.
	 * First argument is a key of already running task, second class to cast the key.
	 * @param <T> type returned by task
	 * @return future object for controlling task execution.
	 */
	@Override
	public synchronized <T> Future<T> runTask(final Object key, final Supplier<T> task, final Predicate<Object> predicate) {

		log.info("Add task '{}' to task queue.", key);
		CompletableFuture<Void> futuresToWait = CompletableFuture.allOf(tasks.stream()
				.filter(e -> areKeysMatched(e.key, predicate))
				.map(e -> e.value)
				.toArray(CompletableFuture[]::new));

		CompletableFuture<T> future = futuresToWait.thenApplyAsync(aVoid -> task.get(), executor)
				.exceptionally(t -> {
					log.error("Error in task: " + key, t);
					throw new SyncRunnerException("Error in task with key: " + key, t);
				}
		);
		tasks.add(new ImmutablePair<>(key, future));
		return future;
	}

	private static boolean areKeysMatched(final Object key, final Predicate<Object> predicate) {
		return predicate.test(key);
	}

	/**
	 * Method waits until all tasks ended.
	 * Method synchronized, so it blocks new tasks running.
	 *
	 * @throws ExecutionException is thrown if any task finished with exception
	 * @throws InterruptedException is thrown if any task was interrupted
	 */
	@Override
	public synchronized void waitAllTasks() throws ExecutionException, InterruptedException {
		waitTasks(key -> true);
	}

	/**
	 * Same as {@link SyncRunner#waitAllTasks()} but waits only tasks are matched by predicate.
	 */
	@Override
	public synchronized void waitTasks(Predicate<Object> predicate) throws ExecutionException, InterruptedException {
		CompletableFuture.allOf(tasks.stream()
				.filter(e -> predicate.test(e.key))
				.map(e -> e.value)
				.toArray(CompletableFuture[]::new))
				.get();
	}

	private static final class ImmutablePair<K, V> {
		private final K key;
		private final V value;

		private ImmutablePair(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}


	public static Predicate<Object> immediately() {
		return o -> false;
	}

	public static Predicate<Object> afterAllTasks() {
		return o -> true;
	}

	public static Predicate<Object> afterTasksWithSameKey(final Object key) {
		return k -> k.equals(key);
	}

	public static Predicate<Object> afterTasksWithKeysContainPrefix(final String prefix) {
		return k -> {
			if (k instanceof String) {
				final String oldKey = (String) k;
				return oldKey.startsWith(prefix);
			} else {
				return false;
			}
		};
	}

}
