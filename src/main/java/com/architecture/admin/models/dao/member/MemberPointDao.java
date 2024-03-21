package com.architecture.admin.models.dao.member;

import com.architecture.admin.models.dto.member.MemberPointDto;
import com.architecture.admin.models.dto.member.MemberPointSaveDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MemberPointDao {

    /****************************************
     * SELECT
     ****************************************/

    /**
     * 회원 보유 포인트 조회
     *
     * @param memberUuid : 회원 uuid
     * @return : point, save_point, use_point, expire_point
     */
    MemberPointDto getMemberPoint(String memberUuid);

    /****************************************
     * INSERT
     ****************************************/

    /**
     * member_point 등록
     *
     * @param memberPointDto : memberUuid, point, expiredate, regdate
     */
    void insertMemberPointSave(MemberPointDto memberPointDto);

    /**
     * member_point_log 적립 등록
     *
     * @param insertDto :memberUuid, point, expiredate, regdate, insertedIdx(member_point_save idx), productOrderId
     */
    void insertMemberPointLog(MemberPointDto insertDto);

    /****************************************
     * UPDATE
     ****************************************/

    /**
     * 회원 보유 포인트 업데이트
     *
     * @param memberPointDto : memberUuid, point
     * @return
     */
    int updateMemberPoint(MemberPointDto memberPointDto);

    /**
     * member_point_save 한개 로우에서 rest_point 조회
     *
     * @param memberPointDto
     * @return
     */
    MemberPointSaveDto getMemberRestPointFromSave(MemberPointDto memberPointDto);

    /**
     * member_point_save rest_point 업데이트
     *
     * @param pointLogDto : restPoint, idx
     */
    int updateMemberRestPointToSave(MemberPointDto pointLogDto);
}
