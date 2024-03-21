package com.architecture.admin.models.daosub.search;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SearchDaoSub {

    /**
     * 검색어가 포함된 닉네임을 가진 회원 UUID리스트
     * 
     * @param searchNick
     * @return
     */
    List<String> getSearchNickUuid(String searchNick);

    /**
     * 글자가 온전히 똑같은 닉네임 UUID리스트
     *
     * @param searchNick
     * @return
     */
    String getSameNickUuid(String searchNick);
}
