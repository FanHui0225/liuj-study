package com.stereo.study.zk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.Executors;

/**
 * Created by stereo on 17-1-10.
 */
public class MainExample {

    private static Logger LOG = LoggerFactory.getLogger(MainExample.class);

    public static void find(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        while (left < right) {
            int sum = arr[left] + arr[right];
            if (sum == target) {
                System.out.println("left=" + arr[left] + "  right=" + arr[right]);
                left++;
            } else if (sum > target) {
                right--;
            } else if (sum < target) {
                left++;
            } else {
                break;
            }
        }
    }

    public static void sort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int[] arr = {11, 1, 3, 4, 5, 6, 7, 111, 233, 0, -1};
        sort(arr);
        System.out.println(Arrays.toString(arr));
        Executors.newFixedThreadPool(5);
        find(arr, 7);
    }
}
