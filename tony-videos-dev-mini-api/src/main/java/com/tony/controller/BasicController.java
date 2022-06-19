package com.tony.controller;

import com.tony.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class BasicController {

    @Autowired
    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";
    ////文件保存的命名空间
    //public static final String FILE_SPACE = "D:/tony_videos_dev";
    ////ffmpeg所在目录
    //public static final String FFMpPEG_EXE = "D:\\study\\software\\ffmpeg\\bin\\ffmpeg.exe";
    ////每页分页的记录数
    //public static final Integer PAGE_SIZE = 5;

    //文件保存的命名空间
    public static final String FILE_SPACE =
            System.getProperties().getProperty("user.home") + File.separator + "webapps" + File.separator + "tony_video_dev_data";
    //ffmpeg所在目录
    public static final String FFMPPEG_EXE = "ffmpeg";//需要预先设置好ffmpeg的环境变量
    //每页分页的记录数
    public static final Integer PAGE_SIZE = 5;



}
