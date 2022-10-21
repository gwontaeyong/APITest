package concurrent.lock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class ReadWriteLockTest {

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    @BeforeEach
    public void init() {
        while (lock.getReadLockCount() > 0)
            lock.readLock().unlock();
        while (lock.isWriteLocked())
            lock.writeLock().unlock();
    }

    @Test
    public void readLock() {
        boolean isReadLockAcquired = readLock.tryLock();
        try {
            assertThat(isReadLockAcquired).isTrue();
        } finally {
            readLock.unlock();
        }
    }

    @Test
    public void acquireReadLockAndWriteLock() {
        boolean isReadLockAcquired = readLock.tryLock();
        boolean isWriteLocAcquired = writeLock.tryLock();
        try {
            assertThat(isReadLockAcquired).isTrue();
            assertThat(isWriteLocAcquired).isFalse();
        } finally {
            readLock.unlock();
        }
    }

    @Test
    public void acquireReadLockTwice() {
        boolean canAcquireReadLock = readLock.tryLock();
        boolean canAcquireSecondReadLock = readLock.tryLock();
        try {
            assertThat(canAcquireReadLock).isTrue();
            assertThat(canAcquireSecondReadLock).isTrue();
        } finally {
            readLock.unlock();
            readLock.unlock();
        }
    }

    @Test
    public void acquireWriteLockTwice() {
        boolean canAcquireWriteLock = writeLock.tryLock();

        try {
            assertThat(canAcquireWriteLock).isTrue();
            Thread t = new Thread(() -> {
                boolean canAcquireSecondWriteLock = writeLock.tryLock();
                assertThat(canAcquireSecondWriteLock).isFalse();
            });
            t.start();
        } finally {
            writeLock.unlock();
        }
    }

    @Test
    public void doUnLockMoreThanLock() {
        assertThatCode(() -> {
            int lockCount = 3;
            int count = 0;
            while (count++ < lockCount) {
                readLock.lock();
            }

            count = 0;
            while (count++ < lockCount + 1) {
                readLock.unlock();
            }
        }).isInstanceOf(IllegalMonitorStateException.class);
    }
}

