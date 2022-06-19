//package com.tony.controller;
//
//import com.tony.client.impl.JedisClusterClient;
//import com.tony.config.RedisConfig;
//import com.tony.config.RedisProperties;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @Title: RedisController
// * @ProjectName springbootdemo
// * @Description: TODO
// * @author YangPeng
// * @date 2019/4/3-17:55
// */
//@RestController
//public class TestController {
//    @Autowired
//    private RedisProperties redisProperties;
//
//    @Autowired
//    private RedisConfig redisConfig;
//
//    @Autowired
//    private JedisClusterClient jedisClusterClient;
//
//    @RequestMapping(value = "/getRedisValue")
//    public String getRedisValue(){
//        System.out.println(redisProperties.toString());
//        System.out.println(redisConfig.getJedisCluster().getClusterNodes());
//        System.out.println(jedisClusterClient.get("yp"));
//        jedisClusterClient.set("12","12");
//        System.out.println(jedisClusterClient.get("12"));
//        return jedisClusterClient.get("12");
//    }
//}
