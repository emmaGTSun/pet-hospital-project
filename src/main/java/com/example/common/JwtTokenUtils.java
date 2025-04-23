package com.example.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.entity.User;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtTokenUtils {

    private static UserService staticUserService;
    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtils.class);

    @Resource
    private UserService userService;

    @PostConstruct
    public void setUserService() {
        staticUserService = userService;
    }

    /**
     * Generate a token
     */
    public static String genToken(String userId, String password) {
        return JWT.create().withAudience(userId) // Save the userId into the token as a payload.
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) // Token will expire in 2 hours
                .sign(Algorithm.HMAC256(password)); // Using 'password' as the token secret.
    }

    /**
     * Get information of the currently logged-in
     */
    public static User getCurrentUser() {
        String token = null;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            token = request.getHeader("token");
            if (StrUtil.isBlank(token)) {
                token = request.getParameter("token");
            }
            if (StrUtil.isBlank(token)) {
                log.error("Failed to retrieve the current login token， token: {}", token);
                return null;
            }
            // 解析token，获取用户的id
            String userId = JWT.decode(token).getAudience().get(0);
            return staticUserService.findById(Integer.valueOf(userId));
        } catch (Exception e) {
            log.error("Failed to retrieve current user information., token={}", token,  e);
            return null;
        }
    }
}