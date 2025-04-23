package com.example.controller;

import com.example.common.AutoLog;
import com.example.common.CaptureConfig;
import com.example.common.Result;
import com.example.entity.Params;
import com.example.entity.UpdatePasswordRequest;
import com.example.entity.User;
import com.example.service.UserService;
import com.github.pagehelper.PageInfo;
import com.wf.captcha.utils.CaptchaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin // Solving Cross-Domain Issues
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    @PostMapping("/login")
    @AutoLog("Log in to the system")
    public Result login(@RequestBody User user, @RequestParam String key, HttpServletRequest request) {
        // Check if the captcha is correct
        if (!user.getVerCode().toLowerCase().equals(CaptureConfig.CAPTURE_MAP.get(key))) {
            // If not equal, it means the verification has failed
            CaptchaUtil.clear(request);
            return Result.error(" Incorrect verification code");
        }
        User loginUser = userService.login(user);
        CaptureConfig.CAPTURE_MAP.remove(key);
        return Result.success(loginUser);
    }

    @PostMapping("/update-password")
    public Result updatePassword(@RequestBody UpdatePasswordRequest updateRequest) {
        userService.updatePassword(updateRequest.getUserId(), updateRequest.getOldPassword(), updateRequest.getNewPassword());
        return Result.success();
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.add(user);
        return Result.success();
    }

    @PostMapping
    public Result save(@RequestBody User user) {
        if (user.getId() == null) {
            userService.add(user);
        } else {
            userService.update(user);
        }
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
         List<User> list = userService.findAll();
         return Result.success(list);
    }

    @GetMapping("/search")
    public Result findBySearch(Params params) {
        log.info("The interceptor has been allowed through, now officially invoking the internal API to query user information");
        PageInfo<User> info= userService.findBySearch(params);
        return Result.success(info);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        userService.delete(id);
        return Result.success();
    }




}
