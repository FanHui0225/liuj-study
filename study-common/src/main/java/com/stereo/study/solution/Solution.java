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
    public static List<int[]> inPlaceRemove(int[] nums) {
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
        return null;
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
     * <p>
     * 1，动态规划解决
     * 定义dp[i][0]表示第i+1天交易完之后手里没有股票的最大利润，dp[i][1]表示第i+1天交易完之后手里持有股票的最大利润。
     * 当天交易完之后手里没有股票可能有两种情况，一种是当天没有进行任何交易，又因为当天手里没有股票，所以当天没有股票的利润只能取前一天手里没有股票的利润。
     * 一种是把当天手里的股票给卖了，既然能卖，说明手里是有股票的，所以这个时候当天没有股票的利润要取前一天手里有股票的利润加上当天股票能卖的价格。这两种情况我们取利润最大的即可，所以可以得到
     * dp[i][0]=max(dp[i-1][0],dp[i-1][1]+prices[i]);
     * 当天交易完之后手里持有股票也有两种情况，一种是当天没有任何交易，又因为当天手里持有股票，所以当天手里持有的股票其实前一天就已经持有了。
     * 还一种是当天买入了股票，当天能买股票，说明前一天手里肯定是没有股票的，我们取这两者的最大值，所以可以得到
     * dp[i][1]=max(dp[i-1][1],dp[i-1][0]-prices[i]);
     * 动态规划的递推公式有了，那么边界条件是什么，就是第一天
     * 如果买入：dp[0][1]=-prices[0];
     * 如果没买：dp[0][0]=0;
     *
     * @param prices
     * @return [7, 1, 5, 3, 6, 4]
     */
    public static int maxProfit(int[] prices) {
        if (prices == null || prices.length < 2)
            return 0;
        int length = prices.length;
        int[][] dp = new int[length][2];
        //初始条件
        dp[0][1] = -prices[0];
        dp[0][0] = 0;
        System.out.println("[0] = " + dp[0][0] + " -------- " + "[1] = " + dp[0][1]);
        for (int i = 1; i < length; i++) {
            //递推公式
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1] + prices[i]);
            dp[i][1] = Math.max(dp[i - 1][1], dp[i - 1][0] - prices[i]);
            System.out.println("[0] = " + dp[i][0] + " -------- " + "[1] = " + dp[i][1]);
        }
        //最后一天肯定是手里没有股票的时候，利润才会最大，
        //只需要返回dp[length - 1][0]即可
        return dp[length - 1][0];
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
        int maxProfit = maxProfit(new int[]{7, 1, 5, 3, 6, 4});
        System.out.println("maxProfit = " + maxProfit);
    }
}
