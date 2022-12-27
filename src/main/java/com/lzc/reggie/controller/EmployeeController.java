package com.lzc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzc.reggie.common.R;
import com.lzc.reggie.entity.Employee;
import com.lzc.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController
{
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee)
    {
     /*
        1. 将页面提交的password进行md5进行加密
        2. 根据用户提交的用户名username查询数据库
        3. 如果没有查询到则返回登录失败结果
        4. 密码对比，如果不一致，如果不一致则返回登录失败
        5. 查看员工状态，如果是已禁用状态，则返回员工已禁用的结果
        6. 登陆成功，将员工id存入Session并且返回登录成功结果
      */
        // 密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 根据用户名来查找数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);// 通过数据库来查询到数据 这应该是mybatis-plus的内容把？

        // 查询不到结果
        if (emp == null)
        {
            return R.error("登录失败");
        }

        // 密码的比对
        if (!emp.getPassword().equals(password))
        {
            return R.error("登录失败!");
        }

        // 查看员工状态
        if (emp.getStatus() == 0)
        {
            return R.error("账号已禁用");

        }

        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工登出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request)
    {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功!");
    }

    /**
     * 新增员工
     *
     * @param employee
     * @return
     */
    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee)
    {
        log.info(employee.toString());
        // 设置初始化密码。并且先进行md5进行加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 将当前的创建时间和更新时间加上
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        // 设置当前更新人和创建人
        Long userId = (Long) request.getSession().getAttribute("employee");

//        employee.setCreateUser(userId);
//        employee.setUpdateUser(userId);
        employeeService.save(employee);
        return R.success("创建成功");
    }

    /**
     * 进行分页查询 默认 page = 1 pageSize = 10
     * 如果传入 name 就是要进行查询
     *
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name)
    {

        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name); // 看是否查询到了

        // 基于MyBatisPlus 的分页插件进行查询

        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize); // 一页请求的数据

        // 构造查询条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo, queryWrapper); // 这样就直接处理好了
        // 返回查询

        return R.success(pageInfo);
    }

    /**
     * 根据 id 来修改信息 这个 id 有可能出现错误,
     *
     * @param employee
     * @return
     */
    @PutMapping()
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee)
    {
        // 查看是否传入了
        log.info(employee.toString());

        employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");

//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("员工修改成功");
    }

    /**
     * 根据 id 来查询员工信息 然后进行修改和删除 修改的上传在上面已经开发完了
     *
     * @param id
     * @return
     */
    @GetMapping({"/{id}"})
    public R<Employee> getById(@PathVariable Long id)
    {
        log.info("根据 id 来查询信息");
        Employee employee = employeeService.getById(id);
        // 有可能两个人一起，导致不能查询到，所以会导致寄了
        if (employee != null)
        {
            return R.success(employee);
        }else
        {
            return R.error("没有查询到相关信息");
        }
    }
}
