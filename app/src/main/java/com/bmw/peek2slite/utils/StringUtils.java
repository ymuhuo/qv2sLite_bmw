package com.bmw.peek2slite.utils;

/**
 * Created by admin on 2018/1/15.
 */

public class StringUtils {

    public static int countMatches(String str, char b) {
        int count = 0;
        char c[] = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == b) {
                count++;
            }
        }
        return count;
    }
}
