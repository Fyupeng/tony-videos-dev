package com.tony.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tony.mapper.*;
import com.tony.pojo.Comments;
import com.tony.pojo.SearchRecords;
import com.tony.pojo.UsersLikeVideos;
import com.tony.pojo.Videos;
import com.tony.pojo.vo.CommentsVO;
import com.tony.pojo.vo.VideosVO;
import com.tony.service.VideoService;
import com.tony.mapper.*;
import com.tony.utils.PagedResult;
import com.tony.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@SuppressWarnings("all")
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private UsersInfoMapper usersInfoMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos video) {
        String id = sid.nextShort();
        video.setId(id);
        videosMapper.insertSelective(video);

        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String coverPath) {

        Videos video = new Videos();
        video.setId(videoId);
        video.setCoverPath(coverPath);

        videosMapper.updateByPrimaryKeySelective(video);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult  getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {
        //搜索热搜词接口
        String desc = video.getVideoDesc();
        //搜索作品接口
        String userId = video.getUserId();
        //是否开启热搜保存
        if(isSaveRecord != null && isSaveRecord == 1){
            SearchRecords record = new SearchRecords();
            String recordId = sid.nextShort();
            //保存热搜
            record.setId(recordId);
            record.setContent(desc);

            searchRecordsMapper.insert(record);
        }

        //相当于sql查询的拦截器，会在sql末尾加上 limit page(当前页), pageSize(每页条数)
        PageHelper.startPage(page, pageSize);

        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc, userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();

        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotwords() {
        List<String> hotwords = searchRecordsMapper.getHotwords();
        return hotwords;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comment){
        String id = sid.nextShort();
        comment.setId(id);
        comment.setCreateTime(new Date());
        commentsMapper.insert(comment);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);
        //list返回的是当前page页的数据
        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);

        for(CommentsVO c : list){
            //使用工具转化时间格式为字符串格式（几分钟前、几小时前....）
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }

        PageInfo<CommentsVO> pageList = new PageInfo<>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setRecords(pageList.getTotal());

        return grid;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreaterId){
        String likeId = sid.nextShort();
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        usersLikeVideosMapper.insert(ulv);

        videosMapperCustom.addVideoLikeCount(videoId);

        usersInfoMapper.addReceiveLikeCount(videoCreaterId);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {

            Example example = new Example(UsersLikeVideos.class);
            Example.Criteria criteria = example.createCriteria();

            criteria.andEqualTo("userId", userId);
            criteria.andEqualTo("videoId", videoId);

            usersLikeVideosMapper.deleteByExample(example);

            videosMapperCustom.reduceVideoLikeCount(videoId);

            usersInfoMapper.reduceReceiveLikeCount(videoCreaterId);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyLikeVideo(String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyFollowVideo(String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }


}
