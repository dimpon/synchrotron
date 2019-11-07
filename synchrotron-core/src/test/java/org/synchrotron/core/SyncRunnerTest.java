package org.synchrotron.core;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.synchrotron.core.SyncRunnerImpl.afterAllTasks;
import static org.synchrotron.core.SyncRunnerImpl.afterTasksWithKeysContainPrefix;
import static org.synchrotron.core.SyncRunnerImpl.immediately;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Disabled
@ExtendWith(MockitoExtension.class)
public class SyncRunnerTest {

	//useful for manual debug
	private static final boolean printToConsole = true;
	@Mock
	private OneTwoThree mock;
	private ExecutorService executor = Executors.newFixedThreadPool(5);

	@BeforeEach
	private void printBlankLine() {
		if (printToConsole) {
			System.out.println();
		}
	}

	@BeforeEach
	private void resetMockObject() {
		Mockito.reset(mock);
	}

	@Test
	@DisplayName("Run 3 Sequential Tasks")
	@Timeout(3)
	void testSequentialTasks() throws Exception {

		//Arrange
		when(mock.one()).then(i -> null);
		when(mock.two()).then(i -> null);
		when(mock.three()).then(i -> null);

		SyncRunner runner = new SyncRunnerImpl(executor);

		//Act
		runner.runTask("k1", () -> doItNTimes(mock::one, 10, AnsiColor.BRIGHT_BLACK), afterAllTasks());
		runner.runTask("k2", () -> doItNTimes(mock::two, 9, AnsiColor.BRIGHT_RED), afterAllTasks());
		runner.runTask("k3", () -> doItNTimes(mock::three, 8, AnsiColor.BRIGHT_GREEN), afterAllTasks());

		runner.waitTasks(afterTasksWithKeysContainPrefix("k"));

		//Assert
		InOrder order = Mockito.inOrder(mock);
		order.verify(mock, times(10)).one();
		order.verify(mock, times(9)).two();
		order.verify(mock, times(8)).three();
	}

	@Test
	@DisplayName("Run 3 Sequential Tasks With the same Key")
	@Timeout(3)
	void testSequentialTasksWithSameKey() throws Exception {
		//Arrange
		when(mock.one()).then(i -> null);
		when(mock.two()).then(i -> null);
		when(mock.three()).then(i -> null);

		SyncRunner runner = new SyncRunnerImpl(executor);

		//Act
		runner.runTask("k", () -> doItNTimes(mock::one, 10, AnsiColor.BRIGHT_YELLOW), afterAllTasks());
		runner.runTask("k", () -> doItNTimes(mock::two, 9, AnsiColor.BRIGHT_BLUE), afterAllTasks());
		runner.runTask("k", () -> doItNTimes(mock::three, 8, AnsiColor.BRIGHT_MAGENTA), afterAllTasks());
		runner.waitAllTasks();

		//Assert
		InOrder order = Mockito.inOrder(mock);
		order.verify(mock, times(10)).one();
		order.verify(mock, times(9)).two();
		order.verify(mock, times(8)).three();
	}

	@Test
	@DisplayName("Run 3 Sequential Tasks With the same prefix")
	@Timeout(3)
	void testSequentialTasksWithSamePreifx() throws Exception {
		//Arrange
		OneTwoThree mock = Mockito.mock(OneTwoThree.class);
		when(mock.one()).then(i -> null);
		when(mock.two()).then(i -> null);
		when(mock.three()).then(i -> null);

		SyncRunner runner = new SyncRunnerImpl(executor);

		//Act
		runner.runTask("k1", () -> doItNTimes(mock::one, 10, AnsiColor.DARK_RED), afterTasksWithKeysContainPrefix("k"));
		runner.runTask("k2", () -> doItNTimes(mock::two, 8, AnsiColor.DARK_GREEN), afterTasksWithKeysContainPrefix("k"));
		runner.runTask("k3", () -> doItNTimes(mock::three, 7, AnsiColor.DARK_YELLOW), afterTasksWithKeysContainPrefix("k"));
		runner.waitAllTasks();

		//Assert
		InOrder order = Mockito.inOrder(mock);
		order.verify(mock, times(10)).one();
		order.verify(mock, times(8)).two();
		order.verify(mock, times(7)).three();
	}

	@Test
	@DisplayName("Run 3 Parallel Tasks")
	@Timeout(3)
	void testParallelTasks() throws Exception {
		//Arrange
		OneTwoThree mock = Mockito.mock(OneTwoThree.class);
		when(mock.zero()).then(i -> null);
		when(mock.barrier()).then(i -> {
			if (printToConsole) {
				System.out.println("barrier");
			}
			return null;
		});
		final CyclicBarrier barrier = new CyclicBarrier(3, mock::barrier);

		SyncRunner runner = new SyncRunnerImpl(executor);

		//Act
		runner.runTask("k1", () -> callAndAwait(mock::zero, barrier, 3, AnsiColor.BRIGHT_RED), immediately());
		runner.runTask("k2", () -> callAndAwait(mock::zero, barrier, 3, AnsiColor.BRIGHT_GREEN), immediately());
		runner.runTask("k3", () -> callAndAwait(mock::zero, barrier, 3, AnsiColor.BRIGHT_YELLOW), immediately());
		runner.waitAllTasks();

		//Assert.
		//There is 3 threads running in parallel.
		//CyclicBarrier calls barrier() each time and 3rd thread calls zero() (e.g. 3 times)
		InOrder order = Mockito.inOrder(mock);
		order.verify(mock, times(3)).zero();
		order.verify(mock, times(1)).barrier();
		order.verify(mock, times(3)).zero();
		order.verify(mock, times(1)).barrier();
		order.verify(mock, times(3)).zero();
		order.verify(mock, times(1)).barrier();
	}

	@Test
	@DisplayName("Run 2 Parallel Tasks")
	@Timeout(3)
	void testTwoParallelTasks() throws Exception {
		//Arrange
		when(mock.one()).then(i -> null);
		when(mock.two()).then(i -> null);

		SyncRunner runner = new SyncRunnerImpl(executor);

		final Object lock = new Object();

		//this code guarantee that static() is always the first
		final AtomicBoolean flag = new AtomicBoolean(true);
		Runnable runMe = () -> {
			Void v = (flag.getAndSet(!flag.get())) ? mock.one() : mock.two();
		};

		//Act
		runner.runTask("k1", () -> lockAndNotify(runMe, lock, 3, AnsiColor.BRIGHT_BLUE), immediately());
		runner.runTask("k2", () -> lockAndNotify(runMe, lock, 3, AnsiColor.BRIGHT_MAGENTA), immediately());
		runner.waitAllTasks();

		//Assert
		InOrder order = Mockito.inOrder(mock);
		order.verify(mock).one();
		order.verify(mock).two();
		order.verify(mock).one();
		order.verify(mock).two();
		order.verify(mock).one();
		order.verify(mock).two();
	}

	@Test
	@DisplayName("Run 2 Parallel Tasks and third waits both")
	@Timeout(3)
	void testTwoParallelTasksAndThirdWaitsBoth() throws Exception {
		//Arrange
		when(mock.one()).then(i -> null);
		when(mock.two()).then(i -> null);
		when(mock.three()).then(i -> null);

		SyncRunner runner = new SyncRunnerImpl(executor);
		final Object lock = new Object();

		//this code guarantee that static() is always the first
		final AtomicBoolean flag = new AtomicBoolean(true);
		Runnable runMe = () -> {
			Void v = (flag.getAndSet(!flag.get())) ? mock.one() : mock.two();
		};

		//Act
		runner.runTask("k1", () -> lockAndNotify(runMe, lock, 3, AnsiColor.BRIGHT_CYAN), immediately());
		runner.runTask("k2", () -> lockAndNotify(runMe, lock, 3, AnsiColor.BRIGHT_GREEN), immediately());
		runner.runTask("k3", () -> doItNTimes(mock::three, 3, AnsiColor.BRIGHT_RED), afterAllTasks());
		runner.waitAllTasks();

		//Assert
		InOrder order = Mockito.inOrder(mock);
		order.verify(mock).one();
		order.verify(mock).two();
		order.verify(mock).one();
		order.verify(mock).two();
		order.verify(mock).one();
		order.verify(mock).two();
		order.verify(mock, times(3)).three();
	}

	@Test
	@DisplayName("Exceptions in tasks")
	@Timeout(3)
	void testExceptionInTasks() {
		//Arrange
		SyncRunner runner = new SyncRunnerImpl(executor);

		//Act
		runner.runTask("k1", () -> {
			throw new IllegalStateException("exception in task");

		}, afterAllTasks());

		//Assert
		ExecutionException exception = Assertions.assertThrows(ExecutionException.class, () -> {
			runner.waitTasks(afterTasksWithKeysContainPrefix("k"));
		});

		Assertions.assertTrue(exception instanceof ExecutionException);
		Assertions.assertTrue(exception.getCause() instanceof SyncRunnerException);
		Assertions.assertTrue(exception.getCause().getCause() instanceof CompletionException);
		Assertions.assertTrue(exception.getCause().getCause().getCause() instanceof IllegalStateException);

		Assertions.assertEquals(
				"org.synchrotron.core.SyncRunnerException: Error in task with key: k1",
				exception.getMessage());
		Assertions.assertEquals("Error in task with key: k1", exception.getCause().getMessage());
		Assertions.assertEquals("java.lang.IllegalStateException: exception in task", exception.getCause().getCause().getMessage());
		Assertions.assertEquals("exception in task", exception.getCause().getCause().getCause().getMessage());
	}

	/*
	 * aux private methods
	 */
	private static Void lockAndNotify(Runnable r, final Object lock, final int num, AnsiColor color) {
		for (int i = 0; i < num; i++) {
			synchronized (lock) {
				if (printToConsole) {
					System.out.println(color + Thread.currentThread().toString() + " ..." + AnsiColor.RESET);
				}
				try {
					r.run();
					lock.notify();
					lock.wait();
					lock.notify();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private static Void callAndAwait(Runnable r, final CyclicBarrier barrier, int num, AnsiColor color) {
		for (int i = 0; i < num; i++) {
			try {
				r.run();
				sleep10();
				barrier.await(10, TimeUnit.SECONDS);
				if (printToConsole) {
					System.out.println(color + Thread.currentThread().toString() + " ..." + AnsiColor.RESET);
				}
			} catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static Void doItNTimes(Runnable r, int num, AnsiColor color) {
		for (int i = 0; i < num; i++) {
			r.run();
			sleep10();
			if (printToConsole) {
				System.out.println(color + Thread.currentThread().toString() + " ..." + AnsiColor.RESET);
			}
		}
		return null;
	}

	private static void sleep10() {
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public interface OneTwoThree {
		Void zero();

		Void one();

		Void two();

		Void three();

		Void barrier();
	}

}
