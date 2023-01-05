package com.lzc.reggie.common;

/**
 * 基于 ThreadLocal 封装工具类， 保存和获取当前登录用户id
 * 作用于每一个 线程 中
 */
public class BaseContext
{
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();

    public static void setCurrentId(Long id)
    {
        threadLocal.set(id);
    }

    public static Long getCurrentId()
    {
        return threadLocal.get();
    }
}
