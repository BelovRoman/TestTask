package com.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static final int LOCK_COUNT = 5;
    private static final long DURATION_MS = 60_000;

    public static void main(String[] args) throws InterruptedException {
        final Random random = new Random();
        final List<ReentrantLock> locks = new ArrayList<>(LOCK_COUNT);
        for (int i = 0; i < LOCK_COUNT; i++) {
            locks.add(new ReentrantLock());
        }

        System.out.println("Начали");

        Thread thread1 = new Thread(new LockTask("Thread-1", 10_000, 3_000, 12_000, locks, random), "Thread-1");
        Thread thread2 = new Thread(new LockTask("Thread-2", 5_000, 2_000, 7_000, locks, random), "Thread-2");

        thread1.setDaemon(true);
        thread2.setDaemon(true);

        thread1.start();
        thread2.start();

        Thread.sleep(DURATION_MS);

        System.out.println("Закончили");
    }
}