package com.bmw.peek2slite.utils.singleThreadUtil;

/**
 * Created by admin on 2016/10/21.
 */

import java.util.Collection;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A wrapper class that exposes only the ExecutorService methods of an
 * ExecutorService implementation.
 */
class DelegatedExecutorService extends AbstractExecutorService {
    private final ExecutorService e;

    DelegatedExecutorService(ExecutorService executor) {
        e = executor;
    }

    public void execute(Runnable command) {
        e.execute(command);
    }

    public void shutdown() {
        e.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return e.shutdownNow();
    }

    public boolean isShutdown() {
        return e.isShutdown();
    }

    public boolean isTerminated() {
        return e.isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return e.awaitTermination(timeout, unit);
    }

    public Future<?> submit(Runnable task) {
        return e.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return e.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return e.submit(task, result);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return e.invokeAll(tasks);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return e.invokeAll(tasks, timeout, unit);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return e.invokeAny(tasks);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return e.invokeAny(tasks, timeout, unit);
    }
}