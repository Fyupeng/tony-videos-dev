package com.tony.mapper;

import com.tony.pojo.Comments;
import com.tony.pojo.vo.CommentsVO;
import com.tony.utils.MyMapper;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<Comments> {

    public List<CommentsVO> queryComments(String videoId);

}