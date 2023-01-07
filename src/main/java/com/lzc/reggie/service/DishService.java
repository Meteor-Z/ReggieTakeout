package com.lzc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzc.reggie.dto.DishDto;
import com.lzc.reggie.entity.Dish;

public interface DishService extends IService<Dish>
{
    /**
     * 新增菜品的时候,同时插入菜品对应的口味数据
     * @param dishDto
     */
    public void saveWithFlavor(DishDto dishDto);
    public void updateWithFlavor(DishDto dishDto);
    /**
     * 根据菜品 id 来查询菜品信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id);


}


