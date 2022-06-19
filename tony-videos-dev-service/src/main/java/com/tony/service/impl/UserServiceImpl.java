package com.tony.service.impl;

import com.tony.mapper.UsersFansMapper;
import com.tony.mapper.UsersInfoMapper;
import com.tony.mapper.UsersLikeVideosMapper;
import com.tony.mapper.UsersReportMapper;
import com.tony.pojo.UsersFans;
import com.tony.pojo.UsersInfo;
import com.tony.pojo.UsersLikeVideos;
import com.tony.pojo.UsersReport;
import com.tony.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.Date;
import java.util.List;

@SuppressWarnings("all")
@Service
public class UserServiceImpl<queryUserInfo> implements UserService {
    @Autowired
    private UsersInfoMapper usersInfoMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        UsersInfo user = new UsersInfo();
        user.setUsername(username);
        UsersInfo result = usersInfoMapper.selectOne(user);

        return result == null ? false : true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveuser(UsersInfo user) {

        String userId = sid.nextShort();

        user.setId(userId);

        usersInfoMapper.insert(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UsersInfo queryUserForLogin(String username, String password){
        Example userExample = new Example(UsersInfo.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);
        UsersInfo result = usersInfoMapper.selectOneByExample(userExample);

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserInfo(UsersInfo user){

        Example userExample = new Example(UsersInfo.class);

        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", user.getId());
        usersInfoMapper.updateByExampleSelective(user, userExample);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UsersInfo queryUserInfo(String userId) {
        Example userExample = new Example(UsersInfo.class);

        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", userId);
        UsersInfo user = usersInfoMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId){


        if(StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)){
            return false;
        }

        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);

        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);

        if(list != null && list.size() > 0){
            return true;
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {

        String relId = sid.nextShort();

        UsersFans userFan = new UsersFans();
        userFan.setId(relId);
        userFan.setUserId(userId);
        userFan.setFanId(fanId);

        usersFansMapper.insert(userFan);

        usersInfoMapper.addFansCount(userId);
        usersInfoMapper.addFollowsCount(fanId);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {

        Example example = new Example(UsersFans.class);
        Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        usersFansMapper.deleteByExample(example);

        usersInfoMapper.reduceFansCount(userId);
        usersInfoMapper.reduceFollowsCount(fanId);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryIfFollow(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        List<UsersFans> list = usersFansMapper.selectByExample(example);

        if(list != null && !list.isEmpty() && list.size() > 0){
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reportUser(UsersReport userReport) {

        String urId = sid.nextShort();
        userReport.setId(urId);
        userReport.setCreateDate(new Date());

        usersReportMapper.insert(userReport);
    }


}
