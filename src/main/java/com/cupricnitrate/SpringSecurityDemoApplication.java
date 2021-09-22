package com.cupricnitrate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 硝酸铜
 * @date 2021/9/22
 */
@MapperScan("com.cupricnitrate.mapper")
@SpringBootApplication
public class SpringSecurityDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityDemoApplication.class, args);
    }
}
