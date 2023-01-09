package com.lzc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzc.reggie.common.BaseContext;
import com.lzc.reggie.common.CustomException;
import com.lzc.reggie.entity.*;
import com.lzc.reggie.mapper.OrdersMapper;
import com.lzc.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService
{
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单的具体实现实现方法
     *
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders)
    {
        // 获得当前用户 id
        Long userId = BaseContext.getCurrentId();
        // 获取当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        // 这是用户的购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0)
        {
            throw new CustomException("购物车为空,不能下单");
        }
        // 查询用户数据
        User user = userService.getById(userId);

        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null)
        {
            throw new CustomException("用户信息有误,不能下单");
        }
        AtomicInteger atomicInteger = new AtomicInteger(0);
        long orderId = IdWorker.getId();

        // 向订单表中插入一条数据
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map((item) ->
        {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            // 累加总金额
            atomicInteger.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setAmount(new BigDecimal(atomicInteger.get()));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName()
                        + (addressBook.getCityName() == null ? "" : addressBook.getCityName()
                        + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                        + (addressBook.getDetail() == null ? "" : addressBook.getDetail())
                )
        );


        this.save(orders);

        // 向订单明细中插入数据,多表数据
        orderDetailService.saveBatch(orderDetailList);
        // 清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}
