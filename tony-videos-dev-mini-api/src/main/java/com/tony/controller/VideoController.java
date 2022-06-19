package com.tony.controller;

import com.tony.pojo.Bgm;
import com.tony.pojo.Comments;
import com.tony.pojo.Videos;
import com.tony.service.BgmService;
import com.tony.service.VideoService;
import com.tony.utils.FetchVideoCover;
import com.tony.utils.IMoocJSONResult;
import com.tony.utils.MergeVideoMp3;
import com.tony.utils.PagedResult;
import com.tony.enums.VideoStatusEnum;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@Api(value = "视频相关业务的接口", tags = "视频相关业务的controller")
@RequestMapping(value = "/video")
public class VideoController extends BasicController{

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "上传视频", notes = "上传视频的接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                dataType = "String", paramType = "form"),
        @ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false,
                dataType = "String", paramType = "form"),
        @ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true,
                dataType = "String", paramType = "form"),
        @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true,
                dataType = "String", paramType = "form"),
        @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true,
                dataType = "String", paramType = "form"),
        @ApiImplicitParam(name = "desc", value = "视频描述", required = false,
                dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    //@RequestParam(value = "前端返回的参数名")
    public IMoocJSONResult upload(@RequestParam(value = "userId") String userId,
                                  @RequestParam(value = "bgmId")String bgmId,
                                  @RequestParam(value = "videoSeconds")double videoSeconds,
                                  @RequestParam(value = "videoWidth")int videoWidth,
                                  @RequestParam(value = "videoHeight")int videoHeight,
                                  @RequestParam(value = "desc")String desc,
                                  @ApiParam(value = "短视频",required = true)
                                  MultipartFile file) throws Exception{

        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }
        //文件保存的命名空间
        //String fileSpace = "D:/imooc_videos_dev";
        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        //文件上传的最终保存位置
        String finalVideoPath = "";

        try {
            if(file != null){

                String fileName = file.getOriginalFilename();
                //ab.mp4
                String fileNamePrefix = fileName.split("\\.")[0];//点需要用两个斜杆来转义

                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";

                    File outFile = new File(finalVideoPath);
                    //创建用户文件夹
                    if (outFile.getParentFile() != null && !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                        System.out.println(outFile);
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            }else {
                return IMoocJSONResult.errorMsg("上传出错....");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错....");
        }finally {
            if(fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        //后端接收前端bgm参数合并视频
        if(StringUtils.isNotBlank(bgmId)){
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String songPath = bgm.getPath();


            String arrPath[] = songPath.split("\\\\");
            String finalSongPath = "";
            //3.1 处理url的斜杠以及编码
            for(int i = 0; i < arrPath.length; i++){
                if(StringUtils.isNotBlank(arrPath[i])){
                    finalSongPath += "/" + arrPath[i];
                }
            }

            String mp3InputPath = FILE_SPACE + finalSongPath;

            MergeVideoMp3 tool = new MergeVideoMp3(FFMPPEG_EXE);
            String videoInputPath = finalVideoPath;

            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            //合成的视频保存到本地数据库的相对路径
            //合成后的视频不与第一个视频路径相同，应生成一个新的视频，不能覆盖
            uploadPathDB = "/" + userId + "/video" +   "/" + videoOutputName;
            //合成视频的最终路径
            finalVideoPath = FILE_SPACE + uploadPathDB;
            tool.convertor(videoInputPath,mp3InputPath, videoSeconds, finalVideoPath);

        }
        //System.out.println("uploadPathDB= " + uploadPathDB);
        //System.out.println("finalVideoPath= " + finalVideoPath);
        //System.out.println(FILE_SPACE + coverPathDB);

        //对视频进行截图
        FetchVideoCover videoInfo = new FetchVideoCover(FFMPPEG_EXE);
        videoInfo.getCover(finalVideoPath, FILE_SPACE + coverPathDB);

        //保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setCoverPath(coverPathDB);
        video.setVideoSeconds((float) videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());

        String videoId = videoService.saveVideo(video);

        return IMoocJSONResult.ok(videoId);
    }


    @ApiOperation(value = "上传封面", notes = "上传封面的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoId", value = "视频主键id", required = true,
                    dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
    //@RequestParam(value = "前端返回的参数名")
    public IMoocJSONResult uploadCover( @RequestParam(value = "userId") String userId,
            @RequestParam(value = "videoId") String videoId,
            @ApiParam(value = "视频封面",required = true)
            MultipartFile file)throws Exception {

        if(StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id和视频id主键不能为空....");
        }
        //文件保存的命名空间
        //String fileSpace = "D:/imooc_videos_dev";
        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        //文件上传的最终保存位置
        String finalCoverPath = "";

        try {
            if(file != null){

                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    finalCoverPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalCoverPath);
                    //创建用户文件夹
                    if (outFile.getParentFile() != null && !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            }else {
                return IMoocJSONResult.errorMsg("上传出错....");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错....");
        }finally {
            if(fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }



        videoService.updateVideo(videoId, uploadPathDB);


        return IMoocJSONResult.ok(videoId);
    }

    /**
     * @Description: 分页和搜索视频列表
     * @param video
     * @param isSaveRecord: 1 - 需要保存； 0 - 不需要保存，或者为空的时候
     * @param page
     * @return
     */
    @PostMapping(value = "/showAll")
    public IMoocJSONResult showAll(@RequestBody Videos video, Integer isSaveRecord,
                                   Integer page, Integer pageSize ) {
        //前端不传该参时会初始化
        if(page == null){
            page = 1;
        }
        //前端不传该参时会初始化
        if(pageSize == null){
            pageSize = PAGE_SIZE;
        }

        PagedResult result = videoService.getAllVideos(video, isSaveRecord, page, PAGE_SIZE);
        return IMoocJSONResult.ok(result);
    }

    /**
     * @Description: 我收藏/点赞过的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/showMyLike")
    public IMoocJSONResult showMyLike(String userId, Integer page, Integer pageSize){

        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }

        if(pageSize == null){
            pageSize = 6;
        }

        PagedResult videosList = videoService.queryMyLikeVideo(userId, page, pageSize);

        return IMoocJSONResult.ok(videosList);

    }

    /**
     * @Description: 我关注的人发布的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/showMyFollow")
    public IMoocJSONResult showMyFollow(String userId, Integer page, Integer pageSize){
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.ok();
        }

        if(page == null){
            page = 1;
        }

        PagedResult videosList = videoService.queryMyFollowVideo(userId, page, pageSize);

        return IMoocJSONResult.ok(videosList);
    }



    /**
     * Description: 获取热搜词关键字列表
     * @return
     */
    @PostMapping(value = "/hot")
    public IMoocJSONResult hot(){
        List<String> hotwords = videoService.getHotwords();
        return IMoocJSONResult.ok(hotwords);
    }

    @PostMapping(value = "/userLike")
    public IMoocJSONResult userLike(String userId, String videoId, String videoCreaterId){
        videoService.userLikeVideo(userId, videoId, videoCreaterId);
        return IMoocJSONResult.ok();
    }

    @PostMapping(value = "/userUnlike")
    public IMoocJSONResult userUnlike(String userId, String videoId, String videoCreaterId){
        videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
        return IMoocJSONResult.ok();
    }

    @PostMapping(value = "/saveComment")
    public IMoocJSONResult saveComment(@RequestBody Comments comment, String fatherCommentId, String toUserId){
        comment.setFatherCommentId(fatherCommentId);
        comment.setToUserId(toUserId);

        videoService.saveComment(comment);
        return IMoocJSONResult.ok();
    }

    @PostMapping(value = "/getVideoComments")
    public IMoocJSONResult getVideoComments(String videoId, Integer page, Integer pageSize) {
        if(StringUtils.isBlank(videoId)){
            return IMoocJSONResult.ok();
        }

        //分页查询视频列表，时间顺序倒序排序
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }

        PagedResult list = videoService.getAllComments(videoId, page, pageSize);


        return IMoocJSONResult.ok(list);
    }

}
