package com.lzc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzc.reggie.common.CustomException;
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

    /**
     * 删除套餐的实现类
     *
     * @param ids
     */
    @Override
    public void deleteWithDish(List<Long> ids)
    {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 多个 ids 可以直接用 in 来查询
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int countSetmeal = this.count(queryWrapper);
        if (countSetmeal > 0)
        {
            throw new CustomException("套餐正在售卖中,不能删除");
        }
        // 先删除 setmeal 中的套餐数据
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);


        // 删除关系表中的数据 setmeal_dish 中的数据


    }
}
