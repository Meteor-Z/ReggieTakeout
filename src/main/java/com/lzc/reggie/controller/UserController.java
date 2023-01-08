package com.lzc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzc.reggie.common.R;
import com.lzc.reggie.entity.User;
import com.lzc.reggie.service.UserService;
import com.lzc.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController
{
    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request)
    {
        // 获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone))
        {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码是:{}", code);
            // 调用工具类发送请求 这里我就不写了,呜呜呜
            // 这里通过 session 来保存信息
            request.getSession().setAttribute(phone, code);
            return R.success("手机验证码发送成功");
        }
        // 生成一个随即的四位的验证码
        return R.error("手机验证码发送失败");
    }

    /**
     * 移动端用户登陆
     *
     * @param map
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request)
    {
        log.info(map.toString());
        String phoneNumber = map.get("phone").toString();
        String code = map.get("code").toString();
        String phoneInSession = (String) request.getSession().getAttribute("phone");
        // 这里写 死 了
        //       if (phoneInSession != null && (true || phoneInSession.equals(code))) // 这里直接死了,呜呜
        if (true)
        {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phoneNumber);
            User user = userService.getOne(queryWrapper);
            if (user == null)
            {
                // 是新用户 直接进行注册
                user = new User();
                user.setPhone(phoneNumber);
                user.setStatus(1);
                userService.save(user);
            }
            request.getSession().setAttribute("user",user.getId());
            return R.success(user);


        }

        return R.error("登陆失败");

    }
}

