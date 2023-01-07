package com.lzc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzc.reggie.dto.DishDto;
import com.lzc.reggie.entity.Dish;
import com.lzc.reggie.entity.DishFlavor;
import com.lzc.reggie.mapper.DishMapper;
import com.lzc.reggie.service.DishFlavorService;
import com.lzc.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService
{
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品,然后保存对应的口味数据 的实现类
     *
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto)
    {
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        // stream 流的形式进行保存id
        flavors = flavors.stream().map((item) ->
        {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }
}
