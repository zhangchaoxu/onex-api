package com.nb6868.onex.api;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import com.nb6868.onex.common.pojo.Const;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 加解密 测试
 * see https://hutool.cn/docs/#/crypto/%E6%A6%82%E8%BF%B0
 *
 * @author Charles zhangchaoxu@gmail.com
 */
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class CryptoTest {

    @Test
    @DisplayName("二维码")
    public void aesTest() {
        String test1 = "{\"username\":\"1\",\"password\":\"1\",\"uuid\":\"\",\"captcha\":\"\",\"type\":10}";
        String key = "1234567890adbcde";
        byte[] encrypt = SecureUtil.aes(key.getBytes()).encrypt(test1);
        String base64 = Base64.encode(encrypt);
        System.out.println("数据：" + test1);
        System.out.println("加密：" + base64);
        System.out.println("解密：" + SecureUtil.aes(key.getBytes()).decryptStr(Base64.decode(base64)));
        System.out.println("解密：" + SecureUtil.aes(key.getBytes()).decryptStr("5DBdTVg9BL03B6FS%2FgfZudwh9kAGNfgWJ1TPmCg2qU3CS9K9ho1Hp63eG4Wq8H0sxRIPlgh8reDGDqeCfrCXWdABqzrS3L7PgaE2FKdGDjgWm5iiJwkePOeWJZAVZ6CtzEgf9ctdbh4GQzo7cpCdk7MAedq1KHSmvC1V3IVv278OSzu5mPBaORwov5stpDWOjETQ5Mix%2BU8ldcZvEsx%2BgA%3D%3D"));
    }

    @Test
    @DisplayName("二维码")
    public void aesDecide() {
        String test1 = "5DBdTVg9BL03B6FS%2FgfZudwh9kAGNfgWJ1TPmCg2qU3CS9K9ho1Hp63eG4Wq8H0sxRIPlgh8reDGDqeCfrCXWdABqzrS3L7PgaE2FKdGDjgWm5iiJwkePOeWJZAVZ6CtzEgf9ctdbh4GQzo7cpCdk7MAedq1KHSmvC1V3IVv278OSzu5mPBaORwov5stpDWOjETQ5Mix%2BU8ldcZvEsx%2BgA%3D%3D";
        String test2 = URLUtil.decode(test1);
        log.error("test2={}", test2);
        log.error("test3={}", SecureUtil.aes(Const.AES_KEY.getBytes()).decryptStr(test2));
    }

}
