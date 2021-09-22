package com.nb6868.onex.api.modules.sys.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.api.modules.uc.service.CaptchaService;
import com.nb6868.onex.common.pojo.Result;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * 图形验证码
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/sys/captcha")
@AccessControl("/sys/captcha/**")
@Validated
@Api(tags = "验证码")
public class CaptchaController {

    @Autowired
    CaptchaService captchaService;

    /**
     * 验证码机制是将验证码的内容和对应的uuid的对应关系存入缓存,然后验证的时候从缓存中去匹配
     * uuid不应该由前端生成,否则容易伪造和被攻击
     * 包含uuid和图片信息
     */
    @GetMapping("base64")
    @ApiOperation(value = "图形验证码(base64)")
   public Result<?> base64(@RequestParam(required = false, defaultValue = "150", name = "图片宽度") int width, @RequestParam(required = false, defaultValue = "50", name = "图片高度") int height) {
        String uuid = IdUtil.randomUUID();
        // 随机arithmetic/spec
        Captcha captcha = captchaService.createCaptcha(uuid, width, height, RandomUtil.randomEle(new String[]{"arithmetic", "spec"}));
        // 将uuid和图片base64返回给前端
        return new Result<>().success(Dict.create().set("uuid", uuid).set("image", captcha.toBase64()));
    }

    @Deprecated
    @GetMapping("stream")
    @ApiOperation(value = "图形验证码(数据流)", produces = "application/octet-stream")
    public void stream(HttpServletResponse response,
                       @RequestParam(required = false, defaultValue = "150", name = "图片宽度") int width,
                       @RequestParam(required = false, defaultValue = "50", name = "图片高度") int height,
                       @NotNull(message = "uuid不能为空") @RequestParam(name = "UUID") String uuid) {
        // 随机arithmetic/spec
        Captcha captcha = captchaService.createCaptcha(uuid, width, height, RandomUtil.randomEle(new String[]{"arithmetic", "spec"}));

        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        try {
            ServletOutputStream out = response.getOutputStream();
            captcha.out(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
