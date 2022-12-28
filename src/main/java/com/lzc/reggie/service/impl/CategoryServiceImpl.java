package com.lzc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzc.reggie.entity.Category;
import com.lzc.reggie.entity.Dish;
import com.lzc.reggie.entity.Setmeal;
import com.lzc.reggie.mapper.CategoryMapper;
import com.lzc.reggie.service.CategoryService;
import com.lzc.reggie.service.DishService;
import com.lzc.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService
{
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据 ids 来删除分类，删除之前需要进行判断
     * @param ids
     */
    @Override
    public void remove(Long ids)
    {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类 id 来查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if (dishCount > 0)
        {
           // 抛出一个异常，因为有相关关联
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount > 0)
        {
            // 有相关关联。要抛出去一个异常
        }
        // 现在就可以直接删除了

        removeById(ids);
    }
}