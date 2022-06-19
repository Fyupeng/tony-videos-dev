package com.tony.client.impl;

import com.tony.client.CuratorClient;
import com.tony.config.ResourceConfig;
import com.tony.service.BgmService;
import com.tony.utils.BGMOperatorTypeEnum;
import com.tony.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class ZKCuratorClient implements CuratorClient {

    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);

    @Autowired
    private BgmService bgmService;

    @Autowired
    private ResourceConfig resourceConfig;

    //public static final String ZOOKEEPER_SERVER = "47.107.63.171:2181";
    //优化：使用resource.properties来依赖注入

    @Override
    public void init(){
        if(client != null){
            return;
        }

        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(  1000, 5);
        //创建zk客户端
        client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin").build();
        //启动客户端
        client.start();

        try {
            //String testNodeData = new String(client.getData().forPath("/bgm/210816FNTPDX97HH"));
            //log.info("测试节点数据为：{}" + testNodeData);
            addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addChildWatch(String nodePath) throws Exception{
        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);

        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
                    log.info("监听到事件CHILD_ADDED");

                    //1. 从数据库查询bgm对象，获取路径path
                    String path = event.getData().getPath();
                    String operatotObjStr = new String(event.getData().getData(), "utf-8");
                    Map<String, Object> map = JsonUtils.jsonToPojo(operatotObjStr, Map.class);

                    //System.out.println("operatorObjStr: " + operatotObjStr);
                    //System.out.println("map: " + map);

                    //从zookeeper获取bgmPath
                    String operatorType = (String) map.get("operaType");
                    String songPath = (String) map.get("path");

                    //String[] arr = path.split("/");
                    //String bgmId = arr[arr.length - 1];

                    //从数据库获取bgmPath
                    //Bgm bgm = bgmService.queryBgmById(bgmId);
                    //if(bgm == null){
                    //    return;
                    //}
                    //bgm所在的相对路径
                    //String songPath = bgm.getPath();

                    //2. 定义保存到本地的bgm路径
   //                 String filePath = "D:/tony_videos_dev" + songPath;
    /*优化*/          String filePath = resourceConfig.getFileSpace() + songPath;
                    //3. 定义下载的路径(播放url)
                    String arrPath[] = songPath.split("\\\\");
                    String finalPath = "";
                    //3.1 处理url的斜杠以及编码
                    for(int i = 0; i < arrPath.length; i++){
                        if(StringUtils.isNotBlank(arrPath[i])){
                            finalPath += "/";
                            finalPath += URLEncoder.encode(arrPath[i], StandardCharsets.UTF_8.toString()) ;
                        }
                    }
     //               String bgmUrl = "http://192.168.56.1:8080/mvc" + finalPath;
    /*优化*/          String bgmUrl = resourceConfig.getBgmServer() + finalPath;
                    if(operatorType.equals(BGMOperatorTypeEnum.ADD.type)){
                        //下载bgm到springboot服务器
                        URL url = new URL(bgmUrl);
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url, file);
                        client.delete().forPath(path);
                        log.info("文件 {} 已同步!",filePath);
                    }else if (operatorType.equals(BGMOperatorTypeEnum.DELETE.type)){
                        File file = new File(filePath);
                        log.info("文件 {} 已删除!",filePath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }

                }
            }
        });

    }
}
