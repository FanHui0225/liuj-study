package com.stereo.study.levedbqueue;


import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuj-ai on 2019/6/21.
 */
public class LevelDBQueue<E> implements BlockingQueue<E> {


    public LevelDBQueue(){
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public boolean offer(E e) {
        return false;
    }

    @Override
    public E remove() {
        return null;
    }

    @Override
    public E poll() {
        return null;
    }

    @Override
    public E element() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }

    @Override
    public void put(E e) throws InterruptedException {
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public E take() throws InterruptedException {
        return null;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }

    private static Lock lock = new ReentrantLock();
    private static InheritableThreadLocal<String> local = new InheritableThreadLocal<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                ExecutorService executorService = Executors.newFixedThreadPool(100);
//                for (int j = 0; j < 100; j++) {
//                    local.set(String.valueOf(ThreadLocalRandom.current().nextInt()));
//                    System.out.println(Thread.currentThread().getName() + " index:" + local.get());
//                    try {
//                        Thread.sleep(200L);
//                        executorService.submit(new Runnable() {
//                            @Override
//                            public void run() {
//                                System.out.println(Thread.currentThread().getName() + " index:" + local.get());
//                            }
//                        });
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                executorService.shutdown();
//                try {
//                    while (!executorService.awaitTermination(100L, TimeUnit.MILLISECONDS))
//                        ;
//                } catch (InterruptedException e) {
//
//                }
//                System.out.println("结束");
//            }
//        });
//        int task = 4;
//        int total = 3;
//        for (int i = 0; i < total; i++) {
//            System.out.println("=======================================================");
//            for (int j = 0; j < task; j++) {
//                int shard = j % total;
//                if (i == shard || total == 1) {
//                    System.out.println("执行 shard:" + shard + " total:" + total + " index:" + i);
//                } else
//                    System.out.println("忽略 shard:" + shard + " total:" + total);
//            }
//            System.out.println("=======================================================");
//        }
    }
}
