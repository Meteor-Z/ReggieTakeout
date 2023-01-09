package com.lzc.reggie.controller;

import com.lzc.reggie.common.R;
import com.lzc.reggie.entity.Orders;
import com.lzc.reggie.service.OrderDetailService;
import com.lzc.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController
{
    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders)
    {
        log.info("当前的orders的数据 : {}",orders.toString());
        ordersService.submit(orders);
        return R.success("下订单成功!");
    }
}
