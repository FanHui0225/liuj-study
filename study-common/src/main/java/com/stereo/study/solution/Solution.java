package com.stereo.study.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liuj-ai on 2021/4/28.
 */
public class Solution {

    /**
     * 第三道
     *
     * @param s
     * @return
     */
    public static int lengthOfLongestSubstring(String s) {
        int[] count = new int[256];
        int max = 0;
        int j = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            count[c]++;
            while (j < i && count[c] > 1) {
                count[s.charAt(j)]--;
                j++;
            }
            max = Math.max(max, i - j + 1);
        }
        return max;
    }

    /**
     * 第三道
     *
     * @param txt
     * @return
     */
    private static char[] findQCTxt(String txt) {
        char[] nextChars = null;
        char[] chars = null;
        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            if (chars == null) {
                chars = new char[1];
                chars[0] = c;
                continue;
            } else {
                boolean isExist = false;
                for (int j = 0; j < chars.length; j++) {
                    char tmp = chars[j];
                    if (tmp == c) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    int length = chars.length;
                    chars = Arrays.copyOf(chars, length + 1);
                    chars[length] = c;
                } else {
                    if (nextChars == null) {
                        nextChars = chars;
                    } else {
                        if (chars.length > nextChars.length) {
                            nextChars = chars;
                        }
                    }
                    chars = new char[1];
                    chars[0] = c;
                }
            }
        }
        return nextChars != null ? nextChars : chars;
    }

    /**
     * 第四道
     *
     * @param num1
     * @param num2
     * @return
     */
    public static int zws(int[] num1, int[] num2) {
        int j = 0, i = 0, k = 0;
        int[] arr = new int[(num1.length + num2.length) / 2];
        while (true) {
            if (k == arr.length) {
                break;
            }
            int a;
            int b;
            if (j < num1.length && i < num2.length) {
                a = num1[j];
                b = num2[i];
                if (a < b) {
                    j++;
                    arr[k] = a;
                } else {
                    i++;
                    arr[k] = b;
                }
            } else if (j == num1.length - 1 && i < num2.length) {
                b = num2[i];
                i++;
                arr[k] = b;
            } else if (j < num1.length && i == num2.length - 1) {
                a = num1[j];
                j++;
                arr[k] = a;
            } else {
                break;
            }
            k++;
        }
        return arr[arr.length - 1];
    }

    public static void arrFindTwoNumSum(int[] arr, int sum) {
        if (arr.length <= 1) {
            return;
        }
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            int ii = arr[i];
            for (int j = i + 1; j < arr.length; j++) {
                int jj = arr[j];
                if (ii + jj == sum) {
                    if (!list.contains(i) && !list.contains(j)) {
                        System.out.println("Find TwoNum i = " + i + "   j = " + j);
                        list.add(i);
                        list.add(j);
                    }
                }
            }
        }
    }

    public static boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0) {
            return false;
        }
        int x = matrix.length - 1;
        int y = 0;
        int[] row1 = matrix[0];
        int maxY = row1.length - 1;
        while (x >= 0 && y <= maxY) {
            int item = matrix[x][y];
            if (item > target) {
                x--;
            } else if (item < target) {
                y++;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 原地删除数组中的元素
     * 给你一个有序数组 nums ，请你 原地 删除重复出现的元素，使每个元素 只出现一次 ，返回删除后数组的新长度。
     * 不要使用额外的数组空间，你必须在 原地 修改输入数组 并在使用 O(1) 额外空间的条件下完成。
     * <p>
     * //如果左指针和右指针指向的值一样，说明有重复的，
     * //这个时候，左指针不动，右指针继续往右移。如果他俩
     * //指向的值不一样就把右指针指向的值往前挪
     */
    public static int inPlaceRemove(int[] nums) {
        //1 1 1 1 1 2 3 4 5 6 7 8
        int j = 0;
        for (int i = 1; i < nums.length; i++) {
            int c = nums[i];
            int p = nums[j];
            if (p != c) {
                if (j + 1 == i) {
                    j++;
                } else {
                    nums[++j] = c;
                }
            }
        }
        return ++j;
    }

    /**
     * 给定一个数组 prices ，其中 prices[i] 是一支给定股票第 i 天的价格。
     * <p>
     * 设计一个算法来计算你所能获取的最大利润。你可以尽可能地完成更多的交易（多次买卖一支股票）。
     * <p>
     * 注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
     * <p>
     * 示例 1:
     * <p>
     * 输入: prices = [7,1,5,3,6,4]
     * 输出: 7
     * 解释: 在第 2 天（股票价格 = 1）的时候买入，在第 3 天（股票价格 = 5）的时候卖出, 这笔交易所能获得利润 = 5-1 = 4 。
     *      随后，在第 4 天（股票价格 = 3）的时候买入，在第 5 天（股票价格 = 6）的时候卖出, 这笔交易所能获得利润 = 6-3 = 3 。
     * 示例 2:
     * <p>
     * 输入: prices = [1,2,3,4,5]
     * 输出: 4
     * 解释: 在第 1 天（股票价格 = 1）的时候买入，在第 5 天 （股票价格 = 5）的时候卖出, 这笔交易所能获得利润 = 5-1 = 4 。
     *      注意你不能在第 1 天和第 2 天接连购买股票，之后再将它们卖出。因为这样属于同时参与了多笔交易，你必须在再次购买前出售掉之前的股票。
     * 示例 3:
     * <p>
     * 输入: prices = [7,6,4,3,1]
     * 输出: 0
     * 解释: 在这种情况下, 没有交易完成, 所以最大利润为 0。
     *
     * @param prices
     * @return [7, 1, 5, 3, 6, 4]
     */
    public static int maxProfit(int[] prices) {
        if (prices == null || prices.length < 2)
            return 0;
        int total = 0, index = 0, length = prices.length;
        while (index < length) {
            while (index < length - 1 && prices[index] >= prices[index + 1]) {
                index++;
            }
            int min = prices[index];
            while (index < length - 1 && prices[index] <= prices[index + 1]) {
                index++;
            }
            int max = prices[index];
            total += (max - min);
            index++;
        }
        return total;
    }

    /**
     * 给定一个数组，将数组中的元素向右移动k个位置，其中k是非负数。
     * 进阶：
     * 尽可能想出更多的解决方案，至少有三种不同的方法可以解决这个问题。
     * 你可以使用空间复杂度为 O(1) 的 原地 算法解决这个问题吗？
     * <p>
     * 示例 1:
     * 输入: nums = [1,2,3,4,5,6,7], k = 3
     * 输出: [5,6,7,1,2,3,4]
     * 解释:
     * 向右旋转 1 步: [7,1,2,3,4,5,6]
     * 向右旋转 2 步: [6,7,1,2,3,4,5]
     * 向右旋转 3 步: [5,6,7,1,2,3,4]
     * <p>
     * <p>
     * 示例 2:
     * 输入：nums = [-1,-100,3,99], k = 2
     * 输出：[3,99,-1,-100]
     * 解释:
     * 向右旋转 1 步: [99,-1,-100,3]
     * 向右旋转 2 步: [3,99,-1,-100]
     *
     * @param nums
     * @param k
     */
    public static void rotate(int[] nums, int k) {
        int c = nums[0];
        int index = 0;
        int len = nums.length;
        boolean[] visited = new boolean[len];
        for (int i = 0; i < len; i++) {
            index = (index + k) % len;
            if (visited[index]) {
                index = (index + 1) % len;
                c = nums[index];
                i--;
            } else {
                visited[index] = true;
                int tmp = nums[index];
                nums[index] = c;
                c = tmp;
            }
        }
    }

    public static void main(String[] args) {
//        int length = 3;
//        int start = 0;
//        int list[] = new int[length];
//        for (int j = 0; j < length; j++) {
//            list[j] = j + 1;
//        }
//        arrage(list, start, length);
//        //输入: s = "abcabcbb"  pwwkew
//        System.out.println(Arrays.toString(findTxt("pwwkew")));
//        System.out.println(lengthOfLongestSubstring("pwwkew"));
//        System.out.println(zws(new int[]{1, 2, 3, 4, 10, 11}, new int[]{55, 66, 77, 88, 99, 100}));
//        arrFindTwoNumSum(new int[]{100, 22, 9, 33, 43, 52, 61, 7, 8, 91, 1, 9, 8, 2, 1}, 10);
//        int[][] matrix = new int[][]{{1, 4, 7, 11, 15}, {2, 5, 8, 12, 19}, {3, 6, 9, 16, 22}, {10, 13, 14, 17, 24}, {18, 21, 23, 26, 30}};
//        System.out.println("searchMatrix 5 > " + searchMatrix(matrix, 30));
//        System.out.println(inPlaceRemove(new int[]{1, 1, 1, 1, 1, 2, 3, 4, 5, 6, 7, 8}));
//        System.out.println(maxProfit(new int[]{7, 1, 5, 3, 6, 4}));
//        System.out.println(maxProfit(new int[]{7, 6, 5, 3, 2, 1}));
        int[] nums = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        rotate(nums, 4);
        System.out.println(Arrays.toString(nums));
    }
}
