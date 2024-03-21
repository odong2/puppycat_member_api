package com.architecture.admin.services.push;

import com.architecture.admin.models.daosub.push.PushDaoSub;
import com.architecture.admin.models.dto.push.PushDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/*****************************************************
 * 푸시 공통 모델러
 ****************************************************/
@Service
@RequiredArgsConstructor
public class PushService extends BaseService {
    private final PushDaoSub pushDaoSub;

    /*****************************************************
     *  Modules
     ****************************************************/

    /*****************************************************
     *  SubFunction - select
     ****************************************************/
    /**
     * 토큰 리스트 가져오기
     *
     * @param pushDto receiverIdx typeIdx
     * @return 토큰 리스트
     */
    public List<String> getPushTokenList(PushDto pushDto) {
        return pushDaoSub.getPushTokenList(pushDto);
    }


    /*****************************************************
     *  SubFunction - insert
     ****************************************************/

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/
    /*****************************************************
     *  SubFunction - Delete
     ****************************************************/
}
