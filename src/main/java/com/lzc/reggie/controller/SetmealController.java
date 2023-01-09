package com.lzc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzc.reggie.common.R;
import com.lzc.reggie.dto.SetmealDto;
import com.lzc.reggie.entity.Category;
import com.lzc.reggie.entity.Setmeal;
import com.lzc.reggie.entity.SetmealDish;
import com.lzc.reggie.service.CategoryService;
import com.lzc.reggie.service.SetmealDishService;
import com.lzc.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController
{
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody SetmealDto setmealDto)
    {
        log.info("套餐的信息内容:{}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name)
    {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();


        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 查询条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        // 拷贝数据
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) ->
        {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null)
            {
                setmealDto.setCategoryName(category.getName());

            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids)
    {
        setmealService.deleteWithDish(ids);
        return R.success("删除成功!");
    }

    /**
     * 使得套餐发生停售
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> stopShopping(@RequestParam List<Long> ids)
    {
        log.info(ids.toString());
        // 查询所有与对应的ids 相匹配的 Setmeal 然后 使得所有状态是1 的查询出来
        // 查询出来的值全部 状态全部设置为0
        List<Setmeal> list = setmealService.listByIds(ids);
        log.info("查询出来的数据大小是{}", list.size());
        for (int i = 0; i < list.size(); i++)
        {
            list.get(i).setStatus(0);
            System.out.println(list.get(i).toString());
        }
        // 更新数据
        setmealService.updateBatchById(list);
        return R.success("套餐已全部停售");
    }

    /**
     * 将所选的商品进行发售
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> startShopping(@RequestParam List<Long> ids)
    {
        log.info(ids.toString());
        List<Setmeal> list = setmealService.listByIds(ids);
        for (int i = 0; i < list.size(); i++)
        {
            list.get(i).setStatus(1);
        }
        setmealService.updateBatchById(list);
        return R.success("套餐已全部出售");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal)
    {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }
}
