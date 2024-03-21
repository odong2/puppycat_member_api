package com.architecture.admin.models.daosub.jwt;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;


@Mapper
@Repository
public interface JwtDaoSub {

    /**
     * jwt refresh 토큰 유무 체크
     *
     * @param tokenMap
     * @return
     */
    Integer checkRefreshToken(HashMap tokenMap);

    /**
     * jwt refresh 토큰 유무 체크
     *
     * @param tokenMap
     * @return
     */
    Integer checkRefreshTokenByToken(HashMap tokenMap);
}


