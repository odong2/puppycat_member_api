package com.architecture.admin.services.member;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.member.OutMemberDao;
import com.architecture.admin.models.daosub.member.OutMemberDaoSub;
import com.architecture.admin.models.dto.member.OutMemberDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class OutMemberService extends BaseService {

    private final MemberService memberService;
    private final OutMemberDao outMemberDao;
    private final OutMemberDaoSub outMemberDaoSub;

    @Value("${out.code.max}")
    private Integer codeMax;  // 탈퇴 사유 코드 MAX

    /*****************************************************
     *  Modules
     ****************************************************/
    /**
     * 회원 탈퇴
     *
     * @param outMemberDto uuid, code, reason
     */
    @Transactional
    public void outMember(OutMemberDto outMemberDto) {

        // 유효성 검사
        validate(outMemberDto);

        // 회원 정보 가져오기
        OutMemberDto oTargetInfo = getInfoForOut(outMemberDto);

        // 탈퇴 정보 세팅 (탈퇴코드, 탈퇴일)
        oTargetInfo.setCode(outMemberDto.getCode());
        oTargetInfo.setOutRegDate(dateLibrary.getDatetime());

        // 회원 정보 상태값 변경 [puppycat_member]
        updateIsDel(oTargetInfo);

        // 탈퇴 등록 [puppycat_member_out]
        insertOutMember(oTargetInfo);

        // 직접입력시 탈퇴사유 등록 [puppycat_member_out_reason]
        if (Objects.equals(outMemberDto.getCode(), codeMax)) {
            // 탈퇴 정보 세팅 (상세사유)
            oTargetInfo.setReason(outMemberDto.getReason());
            insertOutReason(oTargetInfo);
        }

        // 간편가입인 경우
        if (oTargetInfo.getIsSimple() == 1) {
            // 간편가입 탈퇴 등록 [puppycat_member_simple_out]
            insertOutSimpleMember(oTargetInfo);
        }

        // jwt 토큰 제거
        deleteJwtToken(outMemberDto);

        // fcm 토큰 제거
        deleteFcmToken(outMemberDto);

    }

    /*****************************************************
     *  SubFunction - Select
     ****************************************************/
    /**
     * 회원 정보 가져오기
     *
     * @param outMemberDto uuid
     * @return 회원 정보
     */
    public OutMemberDto getInfoForOut(OutMemberDto outMemberDto) {
        return outMemberDaoSub.getInfoForOut(outMemberDto);
    }

    /**
     * 탈퇴사유 list 가져오기
     *
     * @return 탈퇴사유 list
     */
    public List<OutMemberDto> getOutCodeList() {
        return outMemberDaoSub.getOutCodeList();
    }

    /*****************************************************
     *  SubFunction - Insert
     ****************************************************/
    /**
     * 탈퇴 등록 [puppycat_member_out]
     *
     * @param outMemberDto 회원정보, code, outRegDate
     */
    public void insertOutMember(OutMemberDto outMemberDto) {
        Integer iResult = outMemberDao.insertOutMember(outMemberDto);
        if (iResult < 1) {
            throw new CustomException(CustomError.MEMBER_OUT_FAIL); // 탈퇴 실패하였습니다.
        }
    }

    /**
     * 직접입력시 회원 탈퇴사유 등록 [puppycat_member_out_reason]
     *
     * @param outMemberDto insertedIdx, reason
     */
    public void insertOutReason(OutMemberDto outMemberDto) {
        outMemberDao.insertOutReason(outMemberDto);
    }

    /**
     * 간편가입 탈퇴 등록 [puppycat_member_simple_out]
     *
     * @param outMemberDto memberUuid, simpleId, simpleType, authToken
     */
    public void insertOutSimpleMember(OutMemberDto outMemberDto) {
        outMemberDao.insertOutSimpleMember(outMemberDto);
    }


    /*****************************************************
     *  SubFunction - Update
     ****************************************************/
    /**
     * 회원 정보 상태값 변경 [puppycat_member]
     *
     * @param outMemberDto uuid
     */
    public void updateIsDel(OutMemberDto outMemberDto) {
        Integer iResult = outMemberDao.updateIsDel(outMemberDto);
        if (iResult < 1) {
            throw new CustomException(CustomError.MEMBER_OUT_FAIL); // 탈퇴 실패하였습니다.
        }
    }

    /*****************************************************
     *  SubFunction - Delete
     ****************************************************/
    /**
     * fcm 토큰 제거
     *
     * @param outMemberDto uuid
     */
    public void deleteFcmToken(OutMemberDto outMemberDto) {
        outMemberDao.deleteFcmToken(outMemberDto);
    }

    /**
     * jwt 토큰 제거
     *
     * @param outMemberDto uuid
     */
    public void deleteJwtToken(OutMemberDto outMemberDto) {
        outMemberDao.deleteJwtToken(outMemberDto);
    }

    /*****************************************************
     *  Validate
     ****************************************************/
    /**
     * 유효성 검사
     *
     * @param outMemberDto uuid, code, reason
     */
    public void validate(OutMemberDto outMemberDto) {

        // uuid
        memberService.memberUuidValidation(outMemberDto.getUuid());

        // code
        if (outMemberDto.getCode() == null || outMemberDto.getCode() == 0) {
            throw new CustomException(CustomError.MEMBER_OUT_CODE_EMPTY); // 탈퇴사유를 선택해주세요.
        }
        if (outMemberDto.getCode() < 1 || outMemberDto.getCode() > codeMax) {
            throw new CustomException(CustomError.MEMBER_OUT_CODE_INVALID); // 유효하지 않은 탈퇴사유입니다.
        }

        // reason
        if (Objects.equals(outMemberDto.getCode(), codeMax) && (ObjectUtils.isEmpty(outMemberDto.getReason()) || outMemberDto.getReason().trim().equals(""))) {
            throw new CustomException(CustomError.MEMBER_OUT_REASON_EMPTY); // 탈퇴 상세사유를 입력해주세요.
        }
    }

}
