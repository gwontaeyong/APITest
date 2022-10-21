package concurrent;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

public class BlockingQueueTest {

    @Test
    public void inQueueSuccess() {
        new LinkedBlockingQueue<Integer>();
        // given
        int queueSize = 10;
        int maxSize = queueSize;
        BlockingQueue<Integer> queue = new ArrayBlockingQueue(queueSize);
        // when
        new Thread(() -> addUntilMaxSize(queue, maxSize)).start();
        new Thread(() -> addUntilMaxSize(queue, maxSize)).start();
        // then
        assertThat(queue.size()).isEqualTo(queueSize);
    }

    @Test
    public void inQueueThrowException() {
        // given
        int queueSize = 10;
        int maxSize = queueSize + 1;
        BlockingQueue<Integer> queue = new ArrayBlockingQueue(queueSize);
        // when
        assertThatCode(() -> addUntilMaxSize(queue, maxSize))
                // then
                .isInstanceOf(Exception.class);
    }

    @Test
    public void inQueueFail() {
        // given
        int queueSize = 10;
        int maxSize = queueSize;
        // when
        BlockingQueue<Integer> queue = new ArrayBlockingQueue(queueSize);
        addUntilMaxSize(queue, maxSize);
        boolean isCanAdd = queue.offer(1);
        // then
        assertThat(isCanAdd).isFalse();
    }

    @Test
    public void isQueueDelaySuccess() throws InterruptedException {
        // given
        int queueSize = 10;
        int maxSize = queueSize;
        // when
        BlockingQueue<Integer> queue = new ArrayBlockingQueue(queueSize);
        addUntilMaxSize(queue, maxSize);
        long waitTime = 4; // off의 제한시간

        new Thread(() -> {
            try {
                Thread.sleep((waitTime - 1) * 1000);
                queue.poll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // enqueue 시도
        boolean isCanOffer = queue.offer(1, waitTime, TimeUnit.SECONDS);
        // then
        assertThat(isCanOffer).isTrue();
        queue.poll();
    }

    public void addUntilMaxSize(BlockingQueue<Integer> queue, int maxSize) {
        while (queue.size() < maxSize) {
            queue.add(1);
        }
    }
}