package com.lzc.reggie.dto;

import com.lzc.reggie.entity.Setmeal;
import com.lzc.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal
{

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
