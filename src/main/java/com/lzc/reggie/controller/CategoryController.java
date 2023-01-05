package com.lzc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzc.reggie.common.R;
import com.lzc.reggie.entity.Category;
import com.lzc.reggie.service.CategoryService;
import com.lzc.reggie.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.awt.*;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController
{

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类，包括菜品分类和套餐分类
     *
     * @param category
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody Category category)
    {
        log.info("新增分类成功!");

        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return 查询是否成功 !
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize)
    {
        log.info("这里在进行分页查询");
        // 分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        // 条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
        // 进行分页查询
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据 id 来删除分类
     *
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(Long ids)
    {
        log.info("删除当前分类：{}", ids);
        categoryService.remove(ids);
        return R.success("删除分类成功");
    }


}
