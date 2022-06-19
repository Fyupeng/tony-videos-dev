package com.tony.mapper;

import com.tony.pojo.UsersInfo;
import com.tony.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

public interface UsersInfoMapper extends MyMapper<UsersInfo> {
    /**
     * @Description: 增加收到喜欢/收藏的累积数量
     * @param userId
     */
    public void addReceiveLikeCount(@Param("userId") String userId);

    /**
     * @Description: 减少收到/喜欢的累积数量
     * @param userId
     */
    public void reduceReceiveLikeCount(@Param("userId") String userId);

    /**
     * @Description: 增加粉丝数量
     * @param userId
     */
    public void addFansCount(@Param("userId") String userId);

    /**
     * @Description: 增加关注数量
     * @param userId
     */
    public void addFollowsCount(@Param("userId") String userId);

    /**
     * @Description: 减少粉丝数量
     * @param userId
     */
    public void reduceFansCount(String userId);

    /**
     * @Description: 减少关注数量
     * @param fanId
     */
    public void reduceFollowsCount(String fanId);
}