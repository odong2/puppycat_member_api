package com.architecture.admin.models.daosub.push;

import com.architecture.admin.models.dto.push.PushDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PushDaoSub {

    /**
     * 토큰 리스트 가져오기
     *
     * @param pushDto receiverIdx typeIdx
     * @return 토큰 리스트
     */
    List<String> getPushTokenList(PushDto pushDto);

}
