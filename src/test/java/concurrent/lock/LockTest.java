package concurrent.lock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class LockTest {

    ReentrantLock lock = new ReentrantLock();

    @BeforeEach
    public void init() {
        if (lock.isLocked())
            lock.unlock();
    }

    @Test
    public void acquireLock() {
        boolean acquireLock = lock.tryLock();
        try {
            assertThat(acquireLock).isTrue();
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void failToAcquireLock() throws InterruptedException {
        lock.lock();
        try {
            blockingLockInOtherThread();
            assertThat(lock.isLocked()).isTrue();
        } finally {
            lock.unlock();
        }
    }

    private void blockingLockInOtherThread() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            boolean isLocked = lock.tryLock();
            assertThat(isLocked).isFalse();
        });
        t1.start();
        t1.join();
    }

    @Test
    public void waitingLockInterrupted() throws InterruptedException {
        lock.lock();
        try {
            Thread main = new Thread(() -> assertThatCode(() -> lock.lockInterruptibly())
                    .isInstanceOf(InterruptedException.class)
            );
            main.start();
            main.interrupt();
        } finally {
            lock.unlock();
        }
    }
}