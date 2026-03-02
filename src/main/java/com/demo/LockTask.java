package com.demo;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class LockTask implements Runnable {

    private final String name;
    private final int periodMs;
    private final int minDelayMs;
    private final int maxDelayMs;
    private final List<ReentrantLock> locks;
    private final Random random;

    public LockTask(String name, int periodMs, int minDelayMs, int maxDelayMs, List<ReentrantLock> locks, Random random) {
        if (locks.size() < 2) {
            throw new IllegalArgumentException("Минимум 2 лока");
        }
        this.name = name;
        this.periodMs = periodMs;
        this.minDelayMs = minDelayMs;
        this.maxDelayMs = maxDelayMs;
        this.locks = locks;
        this.random = random;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            long iterationStart = System.currentTimeMillis();

            runIteration();

            long elapsed = System.currentTimeMillis() - iterationStart;
            long remaining = periodMs - elapsed;
            if (remaining > 0) {
                System.out.println(name + ": Итерация заняла " + elapsed + " мс, спим " + remaining + " мс до следующей итерации");
                try {
                    Thread.sleep(remaining);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                System.out.println(name + ": Итерация заняла " + elapsed + " мс, сразу начинаем следующую итерацию");
            }
        }
    }

    void runIteration() {
        int idx1 = random.nextInt(locks.size());
        int idx2 = random.nextInt(locks.size());
        while (idx2 == idx1) {
            idx2 = random.nextInt(locks.size());
        }

        ReentrantLock lock1 = locks.get(idx1);
        ReentrantLock lock2 = locks.get(idx2);

        if (!lock1.tryLock()) {
            System.out.println(name + ": Не удалось захватить лок " + idx1 + ", пропускаем итерацию");
            return;
        } else {
            System.out.println(name + ": Захватили лок " + idx1 + ", пробуем захватить лок " + idx2);
        }
        if (!lock2.tryLock()) {
            lock1.unlock();
            System.out.println(name + ": Не удалось захватить лок " + idx2 + ", освобождаем лок " + idx1 + " и пропускаем итерацию");
            return;
        } else {
            System.out.println(name + ": Захватили лок " + idx2);
        }

        try {
            long delayMs = minDelayMs + random.nextInt((maxDelayMs - minDelayMs + 1));
            System.out.println(name + ": Захватили локи [" + idx1 + ", " + idx2 + "] на " + delayMs + " мс");
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock2.unlock();
            lock1.unlock();
            System.out.println(name + ": Освободили локи [" + idx1 + ", " + idx2 + "]");
        }
    }
}
