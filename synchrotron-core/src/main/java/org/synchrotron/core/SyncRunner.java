package org.synchrotron.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface SyncRunner {

	<T> Future<T> runTask(final Object key, final Supplier<T> task, final Predicate<Object> predicate);

	void waitAllTasks() throws ExecutionException, InterruptedException;

	void waitTasks(Predicate<Object> predicate) throws ExecutionException, InterruptedException;

}
