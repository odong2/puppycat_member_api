package com.architecture.admin.services.member;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.member.MemberDao;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberInfoDto;
import com.architecture.admin.models.dto.member.OutMemberDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.wordcheck.MemberWordCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional
public class MemberService extends BaseService {

    private final MemberDao memberDao;
    private final MemberWordCheckService memberWordCheckService;

    @Value("${recoverable.days}")
    private int recoverableDays; // 탈퇴시 데이터 복구 가능한 일수
    @Value("${word.check.member.intro.type}")
    private int introWordChk;  // 회원 인트로 금칙어 타입
    @Value("${cloud.aws.s3.img.url}")
    private String imgDomain;  // 이미지 도메인

    /*****************************************************
     *  Modules
     ****************************************************/
    /**
     * 탈퇴회원 복구
     *
     * @param memberDto simpleId
     */
    public void restore(MemberDto memberDto) throws ParseException {

        // simpleId 유효성 검사
        if (ObjectUtils.isEmpty(memberDto.getSimpleId())) {
            throw new CustomException(CustomError.LOGIN_SIMPLE_ID_ERROR); // 소셜 고유아이디 값이 없습니다.
        }

        // [puppycat_member_simple] 테이블에서 simpleId 조회하여 memberUuid 조회
        String memberUuid = checkSimpleId(memberDto);

        // simpleId 유효하지 않다면 에러 발생
        if (ObjectUtils.isEmpty(memberUuid)) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY); // 회원 UUID가 유효하지 않습니다.
        }

        // memberUuid set
        memberDto.setMemberUuid(memberUuid);

        // 복구 가능한지 검증 (탈퇴신청일이 7일 이내 & 탈퇴 대기 상태)
        Boolean bRecovery = checkOutDate(memberDto);

        // 복구 가능
        if (Boolean.TRUE.equals(bRecovery)) {

            // 탈퇴회원 복구 & 회원 상태 변경
            updateState(memberDto);

            // 탈퇴 테이블 상태값 변경 (3:복구)
            updateOutState(memberDto);

        }
        // 복구 불가
        else {
            throw new CustomException(CustomError.RESTORE_ERROR); // 복구 불가능한 계정입니다.
        }

    }


    /*****************************************************
     *  SubFunction - Select
     ****************************************************/
    /**
     * 정상 회원 리스트 체크
     *
     * @param uuidList
     * @return
     */
    public boolean checkMemberUuidList(List<String> uuidList) {
        boolean result = true;

        if (ObjectUtils.isEmpty(uuidList)) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY); // 회원 uuid가 비었습니다.
        }

        MemberDto memberDto = new MemberDto();

        for (String uuid : uuidList) {
            memberDto.setMemberUuid(uuid);
            Boolean isExist = getCountByUuid(memberDto); // 정상 회원이면 true

            if (isExist == false) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 정상 회원인지 체크
     *
     * @param uuid : 회원 uuid
     */
    public void memberUuidValidation(String uuid) {

        if (ObjectUtils.isEmpty(uuid)) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY); // 회원 UUID가 비었습니다.
        }

        // 조회용 dto
        MemberDto memberDto = MemberDto.builder()
                .uuid(uuid).build();

        int count = memberDaoSub.getCountByUuid(memberDto);

        if (count < 1) {
            throw new CustomException(CustomError.MEMBER_UUID_ERROR); // 회원 UUID가 유효하지 않습니다.
        }
    }

    /**
     * 회원 유효성 검사 by puppycat_member.idx
     *
     * @param memberDto idx
     * @return true : 유효
     */
    public Boolean getCountByUuid(MemberDto memberDto) {
        int iCount = memberDaoSub.getCountByUuid(memberDto.getMemberUuid());

        return iCount > 0;
    }

    /**
     * 회원IDX 회원 정보 가져오기
     *
     * @param memberUuid
     * @return memberUuid , nick, img
     */
    public MemberDto getMemberRegdate(String memberUuid) {
        // member idx 검증
        if (memberUuid == null || memberUuid.equals("")) {
            // 회원 IDX가 존재하지 않습니다.
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY);
        }

        return memberDaoSub.getMemberRegdate(memberUuid);
    }

    /**
     * 회원 탈퇴 복구 가능 여부 검사
     *
     * @param memberDto memberUuid
     * @return 가능 여부 true/false
     */
    public Boolean checkOutDate(MemberDto memberDto) throws ParseException {

        // 탈퇴 회원 정보 가져오기
        OutMemberDto outMember = memberDao.getOutDate(memberDto);

        // 탈퇴 대기 상태인지 체크
        if (outMember.getState() == 3) {
            throw new CustomException(CustomError.ALREADY_RESTORE_ERROR); // 이미 복구된 계정입니다.
        }
        if (outMember.getState() != 2) {
            throw new CustomException(CustomError.RESTORE_ERROR); // 복구 불가능한 계정입니다.
        }

        // 날짜 비교 위해 String -> Date 변환
        Date outDate = new SimpleDateFormat("yyyy-MM-dd").parse(outMember.getOutRegDate()); // 탈퇴 신청일
        Date today = new SimpleDateFormat("yyyy-MM-dd").parse(dateLibrary.getDatetime());   // 오늘

        // 두 날짜 차이 계산
        long diffDays = (today.getTime() - outDate.getTime()) / (24 * 60 * 60 * 1000);

        return diffDays <= recoverableDays;
    }

    /**
     * memberUuid 조회 by simpleId
     *
     * @param memberDto simpleId
     * @return memberUuid
     */
    public String checkSimpleId(MemberDto memberDto) {

        // 탈퇴 회원 simpleId 로 memberUuid 조회
        return memberDaoSub.getMemberUuidBySimpleId(memberDto);
    }

    /**
     * 회원 검색 by SearchDto
     *
     * @param searchWord 검색어
     * @return
     */
    public List<String> getMemberUuidBySearch(String searchWord) {

        if (ObjectUtils.isEmpty(searchWord)) {
            throw new CustomException(CustomError.SEARCH_WORD_EMPTY);
        }

        // 회원 조회
        return memberDaoSub.getMemberUuidBySearch(searchWord);
    }

    /**
     * 회원 Info 리스트 가져오기 by memberUuidList
     *
     * @param uuidList memberUuidList
     * @return List<MemberInfoDto>
     */
    public List<MemberInfoDto> getMemberInfoByUuidList(List<String> uuidList) {

        if (ObjectUtils.isEmpty(uuidList)) {
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY);
        }

        MemberDto memberDto = new MemberDto();
        memberDto.setImgDomain(imgDomain);
        memberDto.setUuidList(uuidList);

        // 회원 정보 조회
        List<MemberInfoDto> memberInfoList = memberDaoSub.getMemberInfoByUuidList(memberDto);

        // 소개글 금칙어 치환
        for (MemberInfoDto dto : memberInfoList) {
            if (!ObjectUtils.isEmpty(dto.getIntro())) {
                String intro = memberWordCheckService.memberWordCheck(dto.getIntro(), introWordChk);
                dto.setIntro(intro);
            }
        }

        return memberInfoList;
    }

    /**
     * 회원 닉네임 조회 by memberUuid
     *
     * @param uuid memberUuid
     * @return nick
     */
    public String getMemberNickByUuid(String uuid) {

        // 회원 닉네임 정보 조회
        return memberDaoSub.getMemberNickByUuid(uuid);
    }

    /**
     * 회원 가입일 조회 by memberUuid
     *
     * @param uuid memberUuid
     * @return nick
     */
    public String getMemberJoinDate(String uuid) {

        // 회원 닉네임 정보 조회
        return memberDaoSub.getMemberJoinDate(uuid);
    }

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/
    /**
     * 회원 상태 업데이트 [is_del 1 -> 0]
     *
     * @param memberDto memberUuid
     */
    public void updateState(MemberDto memberDto) {
        memberDao.updateState(memberDto);
    }

    /**
     * 탈퇴 테이블 상태 업데이트 [state 2 -> 3]
     *
     * @param memberDto memberUuid
     */
    public void updateOutState(MemberDto memberDto) {
        memberDao.updateOutState(memberDto);
    }

    /*****************************************************
     *  SubFunction - ETC
     ****************************************************/
}
