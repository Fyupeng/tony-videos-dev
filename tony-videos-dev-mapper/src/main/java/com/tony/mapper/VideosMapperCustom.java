package com.tony.mapper;

import com.tony.pojo.Videos;
import com.tony.pojo.vo.VideosVO;
import com.tony.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {

    public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc, @Param("userId")String userId);

    public void addVideoLikeCount(String videoId);

    public void reduceVideoLikeCount(String videoId);


    public List<VideosVO> queryMyLikeVideos(@Param("userId") String userId);

    public List<VideosVO> queryMyFollowVideos(@Param("userId") String userId);
}