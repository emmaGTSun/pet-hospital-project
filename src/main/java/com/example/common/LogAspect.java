package com.example.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.entity.User;
import com.example.entity.Log;
import com.example.service.LogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 处理切面的“监控”
 */
@Component
@Aspect
public class LogAspect {

    @Resource
    private LogService logService;

    @Around("@annotation(autoLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, AutoLog autoLog) throws Throwable {

        // Operation content, have already defined value() in the annotation,
        // then just write the corresponding operation content on the interface where the cut-in is needed //
        String name = autoLog.value();
        // Operation time (current time)
        String time = DateUtil.now();
        //Operator
        String username = "";
        User user = JwtTokenUtils.getCurrentUser();
        if (ObjectUtil.isNotNull(user)) {
            username = user.getName();
        }

        // Operator IP
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getRemoteAddr();

        // Execute the specific interface
        Result result = (Result) joinPoint.proceed();

        Object data = result.getData();
        if (data instanceof User) {
            User user1 = (User) data;
            username = user1.getName();
        }

        // Then write a log record into the log table
        Log log = new Log(null, name, time, username, ip);
        logService.add(log);

        return result;
    }
}