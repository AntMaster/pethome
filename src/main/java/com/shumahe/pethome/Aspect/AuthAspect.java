package com.shumahe.pethome.Aspect;

import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class AuthAspect {

    /**
     *
     *
     */
    @Pointcut("execution(public * com.shumahe.pethome.Controller.PublishController.*(..))")
    public void  validateAutoInfo() {



    }


    @Before("validateAutoInfo()")
    public void doBefore() {
       ServletRequestAttributes attributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String openId = request.getParameter("openId");
        if (StringUtils.isEmpty(openId)){
            throw new PetHomeException(ResultEnum.OPENID_EMPTY);
        }
    }

    @After("validateAutoInfo()")
    public void doAfter() {
        log.info("22222");
    }
}
