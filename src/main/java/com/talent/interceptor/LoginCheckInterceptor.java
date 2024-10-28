package com.talent.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
     //目标资源方法执行前执行。 返回true：放行    返回false：不放行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 登录请求放行
        if ("/login/oauth".equals(request.getRequestURI())){
            return true;
        }

        //1,先获取请求头
        String token = request.getHeader("Authorization");
        response.setContentType("application/json;charset = UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        //2,判断请求头是否存在
        if (token == null || "".equals(token)){
            //请求头不存在或者请求头为空
            log.info("token不存在");
            String result = mapper.writeValueAsString("NOT_LOGIN");
            response.getWriter().write(result);
            return false;
        }
        return true;
    }
    
    //==========下面与登录无关,不用写==============
 
    //目标资源方法执行后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        System.out.println("postHandle ... ");
    }
 
    //视图渲染完毕后执行，最后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("afterCompletion .... ");
    }
}