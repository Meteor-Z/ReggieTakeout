package com.lzc.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.lzc.reggie.common.BaseContext;
import com.lzc.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter
{
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();

//        log.info(requestURI);
        String[] urls = new String[]{"/employee/login", "/employee/logout", "/backend/**", "/front/**",};
        boolean isMatch = check(urls, requestURI);


        if (isMatch)
        {
            filterChain.doFilter(request, response);
            return;
        }
        Object employee = request.getSession().getAttribute("employee");
        if (employee != null)
        {
            // 这里就是登录成功了 然后设置 ThreadLocal 的 Id
            Long empId = (Long) request.getSession().getAttribute("employee");
            log.info("当前的ID,{}", empId.toString());
            BaseContext.setCurrentId(empId); // 设置当前线程的

            filterChain.doFilter(request, response);
            return;
        }
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 检测是否合格，
     * @param urls
     * @param requestURI
     * @return 如果合格，那么就返回 true， 否则就返回 false
     */
    public boolean check(String[] urls, String requestURI)
    {
        for (String url : urls)
        {
            boolean isMatch = PATH_MATCHER.match(url, requestURI);
            if (isMatch)
            {
                return true;
            }
        }
        return false;
    }
}
