package com.tony.service;

import com.tony.pojo.UsersInfo;
import com.tony.pojo.UsersReport;

public interface UserService {
    /**
     * @Description: 判断用户名是否存在
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * @Description: 保存用户（注册用户）
     * @param user
     */
    public void saveuser(UsersInfo user);

    /**
     * @Description: 用户登录，根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    public UsersInfo queryUserForLogin(String username, String password);

    /**
     * @Description: 用户修改信息
     * @param user
     */
    public void updateUserInfo(UsersInfo user);

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    public UsersInfo queryUserInfo(String userId);

    /**
     * @Description: 查询用户是否喜欢点赞视频
     * @param userId
     * @param videoId
     * @return
     */
    public boolean isUserLikeVideo(String userId, String videoId);


    /**
     * @Description: 增加用户和粉丝的关系
     * @param userId
     * @param fanId
     */
    public void saveUserFanRelation(String userId, String fanId);

    /**
     * @Desrription: 删除用户和粉丝的关系
     * @param userId
     * @param fanId
     */
    public void deleteUserFanRelation(String userId, String fanId);

    /**
     * 查询用户是否关注该视频发布者
     * @param userId
     * @param fanId
     * @return
     */
    public boolean queryIfFollow(String userId, String fanId);

    public void reportUser(UsersReport usersReport);
}
