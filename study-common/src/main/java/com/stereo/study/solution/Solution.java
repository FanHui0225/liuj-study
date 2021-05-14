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

    public static void main(String[] args) {
//        int length = 3;
//        int start = 0;
//        int list[] = new int[length];
//        for (int j = 0; j < length; j++) {
//            list[j] = j + 1;
//        }
//        arrage(list, start, length);
        //    输入: s = "abcabcbb"  pwwkew
//        System.out.println(Arrays.toString(findTxt("pwwkew")));
//        System.out.println(lengthOfLongestSubstring("pwwkew"));
//        System.out.println(zws(new int[]{1, 2, 3, 4, 10, 11}, new int[]{55, 66, 77, 88, 99, 100}));
        arrFindTwoNumSum(new int[]{100, 22, 9, 33, 43, 52, 61, 7, 8, 91, 1, 9, 8, 2, 1}, 10);
    }
}
