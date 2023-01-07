package com.lzc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzc.reggie.dto.DishDto;
import com.lzc.reggie.entity.Dish;
import com.lzc.reggie.entity.DishFlavor;
import com.lzc.reggie.mapper.DishMapper;
import com.lzc.reggie.service.DishFlavorService;
import com.lzc.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
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
    @Transactional
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

    /**
     * 修改菜品
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto)
    {
        // 更新 dish 表基本信息
        this.updateById(dishDto);
        // 清空当前菜品对应的口味数据, dish_flavor的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // 添加当前菜品对应的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) ->
        {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据 id 来查询信息的实现类
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id)
    {
        // 查询当前菜品对应的信息,从 dish 开始查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        // 查询当前菜品对应的口味信息,
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }
}
