package com.stereo.study.util;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * O(logN)
 *
 * @param <T> 最小堆优先队列
 * @User liujing
 */
public abstract class PriorityQueue<T> {

    private T[] heap;
    private int size;
    private int maxSize;

    protected abstract boolean lessThan(Object a, Object b);

    @SuppressWarnings("unchecked")
    protected final void initialize(int maxSize) {
        size = 0;
        int heapSize = maxSize + 1;
        heap = (T[]) new Object[heapSize];
        this.maxSize = maxSize;
    }

    public final void put(T element) {
        size++;
        heap[size] = element;
        upHeap();
    }

    /**
     * @param element
     * @return
     */
    public boolean insert(T element) {
        if (size < maxSize) {
            put(element);
            return true;
        } else if (size > 0 && !lessThan(element, top())) {
            heap[1] = element;
            adjustTop();
            return true;
        } else
            return false;
    }

    /**
     * @return
     */
    public final T top() {
        if (size > 0)
            return heap[1];
        else
            return null;
    }

    public final T pop() {
        if (size > 0) {
            T result = heap[1];
            heap[1] = heap[size];
            heap[size] = null;
            size--;
            downHeap();
            return result;
        } else
            return null;
    }

    public final void adjustTop() {
        downHeap();
    }

    public final int size() {
        return size;
    }

    public final void clear() {
        for (int i = 0; i <= size; i++)
            heap[i] = null;
        size = 0;
    }

    private final void upHeap() {
        int i = size;
        T node = heap[i];
        int j = i >>> 1;
        while (j > 0 && lessThan(node, heap[j])) {
            heap[i] = heap[j];
            i = j;
            j = j >>> 1;
        }
        heap[i] = node;
    }

    private final void downHeap() {
        int i = 1;
        T node = heap[i];
        int j = i << 1;
        int k = j + 1;
        if (k <= size && lessThan(heap[k], heap[j])) {
            j = k;
        }
        while (j <= size && lessThan(heap[j], node)) {
            heap[i] = heap[j];
            i = j;
            j = i << 1;
            k = j + 1;
            if (k <= size && lessThan(heap[k], heap[j])) {
                j = k;
            }
        }
        heap[i] = node;
    }

    /**
     * Test
     *
     * @param args
     */
    public static void main(String[] args) {
        int max = 10000000;
        long s = System.currentTimeMillis();
        Random r = new Random();
        System.out.println("---------- 有序数据结构比较 ----------");

        //最小堆
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>() {
            @Override
            protected synchronized boolean lessThan(Object a, Object b) {
                Integer a1 = (Integer) a;
                Integer a2 = (Integer) b;
                return a1 > a2;
            }
        };
        queue.initialize(max);
        for (int i = 0; i < max; i++) {
            queue.insert(r.nextInt());
        }
        while (queue.size() > 0) {
            queue.pop();
        }
        System.out.println("最小堆性能:" + (System.currentTimeMillis() - s) + "ms");

        s = System.currentTimeMillis();
        List<Integer> list = new ArrayList<Integer>(max);
        for (int i = 0; i < max; i++) {
            list.add(r.nextInt());
        }
        Collections.sort(list);
        System.out.println("归并性能:" + (System.currentTimeMillis() - s) + "ms");

        s = System.currentTimeMillis();
        ConcurrentSkipListSet<Integer> skiptable = new ConcurrentSkipListSet<Integer>();
        for (int i = 0; i < max; i++) {
            skiptable.add(r.nextInt());
        }
        System.out.println("跳跃表性能:" + (System.currentTimeMillis() - s) + "ms");

        s = System.currentTimeMillis();
        TreeSet<Integer> rbt = new TreeSet<Integer>() {
            @Override
            public synchronized boolean add(Integer integer) {
                return super.add(integer);
            }
        };
        for (int i = 0; i < max; i++) {
            rbt.add(r.nextInt());
        }
        System.out.println("红黑树性能:" + (System.currentTimeMillis() - s) + "ms");

    }
}
