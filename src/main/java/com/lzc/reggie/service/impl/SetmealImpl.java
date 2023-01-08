package com.lzc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzc.reggie.dto.SetmealDto;
import com.lzc.reggie.entity.Setmeal;
import com.lzc.reggie.entity.SetmealDish;
import com.lzc.reggie.mapper.SetmealMapper;
import com.lzc.reggie.service.SetmealDishService;
import com.lzc.reggie.service.SetmealService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService
{
    @Autowired
    private SetmealDishService setmealDishService;


    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto)
    {
        // 保存套餐的基本信息, 操作setmeal, 执行insert 语句
        this.save(setmealDto);

        // 保存套餐和菜品的关联信息, 操作 setmeal_dish 来执行insert 关系
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish item : setmealDishes)
        {
            item.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);


    }
}
