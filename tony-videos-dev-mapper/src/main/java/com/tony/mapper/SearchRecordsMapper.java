package com.tony.mapper;

import com.tony.pojo.SearchRecords;
import com.tony.utils.MyMapper;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

    public List<String> getHotwords();
}