package com.stereo.study.util;

import java.util.Arrays;
import java.util.Stack;

/**
 * Created by liuj-ai on 2020/9/15.
 */

public class Test<E extends Comparable<E>> {
    Stack<E> stack1 = new Stack<E>();
    Stack<E> stackMin = new Stack<E>();//存放求最小值的栈
    Stack<E> stackMax = new Stack<E>();//存放求最大值的栈

    public void push(E e) {
        stack1.push(e);
        if (stackMin.isEmpty() || e.compareTo(stackMin.peek()) < 0)
            stackMin.push(e);//若最小栈为空push进stack时就同时把它push进stackMin;
        else if (e.compareTo(stackMin.peek()) > 0)
            stackMin.push(stackMin.peek());
        if (stackMax.isEmpty() || e.compareTo(stackMin.peek()) > 0)
            stackMax.push(e);
        else if (e.compareTo(stackMax.peek()) < 0)
            stackMin.push(stackMax.peek());
    }

    public E pop()//一定要记着，非空才能pop()
    {
        if (!stack1.isEmpty() && !stackMin.isEmpty() && !stackMax.isEmpty()) {
            E e = stack1.pop();
            stackMin.pop();
            stackMax.pop();
            return e;
        } else {
            System.out.println("栈已经为空了");
            return null;
        }
    }

    public E getMin() {
        return stackMin.peek();
    }

    public E getMax() {
        return stackMax.peek();
    }

    public E getMed() {
        E[] ss = (E[]) stack1.toArray();
        Arrays.sort(ss);
        return ss[ss.length / 2];
    }

    public static void main(String[] args) {

    }

}