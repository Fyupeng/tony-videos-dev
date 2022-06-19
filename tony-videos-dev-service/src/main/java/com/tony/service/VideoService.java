package com.tony.service;

import com.tony.pojo.Comments;
import com.tony.pojo.Videos;
import com.tony.utils.PagedResult;

import java.util.List;

public interface VideoService {

    /**
     * @Description: 保存视频
     */
    public String saveVideo(Videos video);

    /**
     * @Description: 修改视频封面
     * @param videoId
     * @param coverPath
     * @return
     */
    public void updateVideo(String videoId, String coverPath);

    /**
     * @Description: 分页查询视频列表
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize);

    /**
     * @Description: 获取热搜词列表
     * @return
     */
    public List<String> getHotwords();

    /**
     * @Description: 用户留言
     * @param comment
     */
    public void saveComment(Comments comment);

    /**
     * @Description: 留言分页
     */
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize);

    /**
     * @Description: 用户喜欢/点赞视频
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    public void userLikeVideo(String userId, String videoId, String videoCreaterId);

    /**
     * @Description: 用户不喜欢/点赞视频
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId);

    /**
     * @Description: 查询我喜欢/收藏的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyLikeVideo(String userId, Integer page, Integer pageSize);

    /**
     * @Description: 查询我关注的人的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyFollowVideo(String userId, Integer page, Integer pageSize);
}
