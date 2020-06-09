package com.qianxin.hiddentale.utils;

/*
 * Created by jingbin on 2016/11/26.
 */

public class StringFormatUtil {
    /*
     * 获取剪切板上的链接
     */
    public static String formatUrl(String url) {
        if (url.startsWith("http")) {
            return url;
        } else if (!url.startsWith("http") && url.contains("http")) {
            // 有http且不在头部
            url = url.substring(url.indexOf("http"));
        } else if (url.startsWith("www")) {
            // 以"www"开头
            url = "http://" + url;
        } else if (!url.startsWith("http") && (url.contains(".me") || url.contains(".com") || url.contains(".cn"))) {
            // 不以"http"开头且有后缀
            url = "http://www." + url;
        } else {
            url = "";
        }
        if (url.contains(" ")) {
            int i = url.indexOf(" ");
            url = url.substring(0, i);
        }
        return url;
    }
}
