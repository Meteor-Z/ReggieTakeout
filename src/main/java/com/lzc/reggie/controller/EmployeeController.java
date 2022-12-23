package com.lzc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzc.reggie.common.R;
import com.lzc.reggie.entity.Employee;
import com.lzc.reggie.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
