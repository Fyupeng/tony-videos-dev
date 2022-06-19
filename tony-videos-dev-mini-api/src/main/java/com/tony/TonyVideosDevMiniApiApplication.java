package com.tony;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.tony.mapper")
@ComponentScan(basePackages = {"com.tony", "org.n3r.idworker"})
public class TonyVideosDevMiniApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TonyVideosDevMiniApiApplication.class, args);
    }

}
