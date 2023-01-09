package com.lzc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzc.reggie.entity.Orders;

public interface OrdersService extends IService<Orders>
{
    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
