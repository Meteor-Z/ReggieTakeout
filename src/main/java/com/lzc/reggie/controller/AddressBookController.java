package com.lzc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lzc.reggie.common.BaseContext;
import com.lzc.reggie.common.R;
import com.lzc.reggie.entity.AddressBook;
import com.lzc.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController
{
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增 一个地址簿
     *
     * @param addressBook
     * @return
     */
    @PostMapping()
    public R<AddressBook> save(@RequestBody AddressBook addressBook)
    {
        // 设置一下 User ID 来进行保存数据,辨别是不是自己的信息
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook)
    {
        log.info("addressBook: {}", addressBook);
        // 先将全部的默认值弄成0 ,然后单独设置成 1
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(updateWrapper);

        // 单独将这个设置成一个 默认地址;
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    @GetMapping("/{id}")
    public R get(@PathVariable Long id)
    {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null)
        {
            return R.success(addressBook);
        } else
        {
            return R.error("没有查找到该对象");
        }
    }

    /**
     * 查询默认地址
     *
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault()
    {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (addressBook != null)
        {
            return R.success(addressBook);
        } else
        {
            return R.error("没有查找到该对象");
        }
    }

    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook)
    {
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }
}
