package com.architecture.admin.services.follow;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.daosub.follow.FollowDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.wordcheck.MemberWordCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;


@RequiredArgsConstructor
@Service
@Transactional
public class FollowService extends BaseService {
    private final FollowDaoSub followDaoSub;
    private final MemberWordCheckService memberWordCheckService;
    @Value("${word.check.member.intro.type}")
    private int introWordChk;  // 회원 인트로 금칙어 타입
    @Value("${cloud.aws.s3.img.url}")
    private String imgDomain;

    /*****************************************************
     *  Modules
     ****************************************************/

    /*****************************************************
     *  SubFunction - Select
     ****************************************************/
    /**
     * 검색시 팔로우 카운트
     *
     * @param searchDto searchType,searchWord
     * @return cnt
     */
    public Long getFollowSearchCnt(SearchDto searchDto) {

        if (ObjectUtils.isEmpty(searchDto.getMemberUuidList())) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY);
        }

        return followDaoSub.getFollowSearchCnt(searchDto);
    }

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/

    /*****************************************************
     *  SubFunction - ETC
     ****************************************************/

}
