package test.config;

import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;

/**
 * Created by Administrator on 2015-11-06.
 */
public class ThrottledTaskExecutor implements TaskExecutor {

    private final Queue<Runnable> completionQueue;

    private final Semaphore semaphore;

    private volatile int count = 0;

    private Object lock = new Object();

    private TaskExecutor taskExecutor = new SyncTaskExecutor();

    /**
     * Create a {@link ThrottledTaskExecutor} with infinite (Integer.MAX_VALUE)
     * throttle limit. A task can always be submitted.
     */
    public ThrottledTaskExecutor() {
        this(null, Integer.MAX_VALUE);
    }

    /**
     * Create a {@link ThrottledTaskExecutor} with infinite (Integer.MAX_VALUE)
     * throttle limit. A task can always be submitted.
     *
     * @param taskExecutor the {@link TaskExecutor} to use
     */
    public ThrottledTaskExecutor(TaskExecutor taskExecutor) {
        this(taskExecutor, Integer.MAX_VALUE);
    }

    /**
     * Create a {@link ThrottledTaskExecutor} with finite throttle limit. The
     * submit method will block when this limit is reached until one of the
     * tasks has finished.
     *
     * @param taskExecutor the {@link TaskExecutor} to use
     * @param throttleLimit the throttle limit
     */
    public ThrottledTaskExecutor(TaskExecutor taskExecutor, int throttleLimit) {
        super();
        if (taskExecutor != null) {
            this.taskExecutor = taskExecutor;
        }
        this.completionQueue = new ConcurrentLinkedQueue<Runnable>();
        this.semaphore = new Semaphore(throttleLimit);
    }

    /**
     * Public setter for the {@link TaskExecutor} to be used to execute the
     * tasks submitted. The default is synchronous, executing tasks on the
     * calling thread. In this case the throttle limit is irrelevant as there
     * will always be at most one task pending.
     *
     * @param taskExecutor
     */
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * Submit a task for execution by the delegate task executor, blocking if
     * the throttleLimit is exceeded.
     *
     * @see TaskExecutor#execute(Runnable)
     */
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("Task is null in ThrottledTaskExecutor.");
        }
        doSubmit(task);
    }

    /**
     * Get an estimate of the number of pending requests.
     *
     * @return the estimate
     */
    public int size() {
        return count;
    }

    private Runnable doSubmit(final Runnable task) {

        try {
            synchronized (lock) {
                semaphore.acquire();
                count++;
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TaskRejectedException("Task could not be submitted because of a thread interruption.");
        }

        taskExecutor.execute(new FutureTask<Object>(task, null) {
            @Override
            protected void done() {
                semaphore.release();
                synchronized (lock) {
                    count--;
                }
                completionQueue.add(task);
            }
        });

        return task;
    }

}
