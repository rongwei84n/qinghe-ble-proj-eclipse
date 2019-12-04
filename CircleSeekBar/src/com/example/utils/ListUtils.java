package com.example.utils;

import java.util.List;

/**
 * 列表判断的相关工具类
 */
public class ListUtils {

    /**
     * 是否为null或者空
     * @param list 要判断的列表
     * @return 判断结果
     */
    public static boolean isEmpty(List<?> list){
        if (list == null) {
            return true;
        }
        return list.isEmpty();
    }
}
