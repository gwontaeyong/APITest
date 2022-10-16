package concurrent.atomic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AtomicIntegerTest {

    int value = 1;
    AtomicInteger atomic = new AtomicInteger(value);

    @BeforeEach
    public void init() {
        atomic.set(value);
    }

    @Test
    public void value() {
        assertThat(atomic.get()).isEqualTo(value);
    }

    @Test
    public void plus() {
        int offset = 1;
        atomic.addAndGet(offset);
        assertThat(atomic.get()).isEqualTo(value + offset);
    }

    @Test
    public void compare() {
        int newValue = 2;
        atomic.compareAndSet(value, newValue);
        assertThat(atomic.get()).isEqualTo(newValue);
    }

    @Test
    public void concurrentPlus() throws InterruptedException {
        int target = 1000;
        Runnable incrementToTarget = () -> {
            while (atomic.getAcquire() < target) {
                atomic.incrementAndGet();
            }
        };
        Thread thread1 = new Thread(incrementToTarget);
        Thread thread2 = new Thread(incrementToTarget);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        assertThat(atomic.get()).isEqualTo(target);
    }
}