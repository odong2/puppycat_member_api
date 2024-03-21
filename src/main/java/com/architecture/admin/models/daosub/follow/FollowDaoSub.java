package com.architecture.admin.models.daosub.follow;

import com.architecture.admin.models.dto.SearchDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface FollowDaoSub {


    /**
     * 검색시 팔로우/팔로워 CNT
     *
     * @param searchDto
     * @return
     */
    Long getFollowSearchCnt(SearchDto searchDto);

}
