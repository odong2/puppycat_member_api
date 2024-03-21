package com.architecture.admin.models.dao.jwt;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;


@Mapper
@Repository
public interface JwtDao {

    /**
     * jwt refresh 토큰 저장
     *
     * @param tokenMap
     * @return
     */
    Integer insertRefreshToken(HashMap tokenMap);

    /**
     * jwt refresh 토큰 수정
     *
     * @param tokenMap
     * @return
     */
    Integer updateRefreshToken(HashMap tokenMap);

    /**
     * jwt refresh 토큰 삭제
     *
     * @param tokenMap
     * @return
     */
    Integer deleteRefreshToken(HashMap tokenMap);
}


