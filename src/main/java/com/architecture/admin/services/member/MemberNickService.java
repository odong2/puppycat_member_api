package com.architecture.admin.services.member;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.member.MemberNickDao;
import com.architecture.admin.models.daosub.member.MemberNickDaoSub;
import com.architecture.admin.models.dto.member.MemberNickDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.wordcheck.MemberWordCheckService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;


@RequiredArgsConstructor
@Service
@Transactional
public class MemberNickService extends BaseService {

    private final MemberNickDao memberNickDao;
    private final MemberNickDaoSub memberNickDaoSub;
    private final MemberWordCheckService memberWordcheckService;
    @Value("${word.check.member.nick.type}")
    private int nickWordChk;  // 회원 닉네임 금칙어 타입
    @Value("${nick.text.min}")
    private int nickTextMin;  // 회원 닉네임 최소 글자수
    @Value("${nick.text.max}")
    private int nickTextMax;  // 회원 닉네임 최대 글자수


    /*****************************************************
     *  Modules
     ****************************************************/
    /**
     * 닉네임 사용여부 체크
     *
     * @param memberNickDto (nick)
     * @return true
     */
    @SneakyThrows
    public Boolean checkNick(MemberNickDto memberNickDto) {

        // 닉네임 검증
        String nick = memberNickDto.getNick();

        if (nick == null || nick.equals("")) {
            throw new CustomException(CustomError.NICK_EMPTY);
        }

        //닉네임 길이 검사 
        if (nick.length() < nickTextMin || nick.length() > nickTextMax) {
            throw new CustomException(CustomError.NICK_LENGTH_ERROR);
        }

        // 숫자/한글/영어/언더바만 입력 가능
        if (!Pattern.matches("^[0-9a-zA-Zㄱ-ㅎ가-힣_]*$", nick)) {
            throw new CustomException(CustomError.NICK_STRING_ERROR);
        }

        // 외계어 검사
        String nickCheck = new String(nick.getBytes("euc-kr"), "euc-kr");
        if (!nick.equals(nickCheck)) {
            // 사용할 수 없는 문자가 포함되어 있습니다.
            throw new CustomException(CustomError.NICK_STRING_ERROR);
        }

        // 닉네임 중복체크
        Boolean bDupleNick = checkDupleNick(memberNickDto);
        if (Boolean.TRUE.equals(bDupleNick)) {
            throw new CustomException(CustomError.NICK_DUPLE);
        }

        // 닉네임 금칙어 체크
        memberWordcheckService.memberWordCheck(nick, nickWordChk);

        return true;
    }

    public Integer modifyNick(MemberNickDto memberNickDto) {

        if (memberNickDto.getNick() == null || memberNickDto.getNick().equals("")) {
            throw new CustomException(CustomError.NICK_EMPTY);
        }

        // 닉네임 공백 제거
        memberNickDto.setNick(memberNickDto.getNick().trim());

        // 닉네임 사용 가능 여부 체크
        Boolean bIsCheckNick = checkNick(memberNickDto);

        // 체크 실패시
        if (Boolean.FALSE.equals(bIsCheckNick)) {
            throw new CustomException(CustomError.NICK_CHECK_FAIL);
        }

        // 닉네임 등록
        Integer iResult = nickUpdate(memberNickDto);

        if (iResult > 0) {
            // 닉네임 로그 등록
            insertLog(memberNickDto);
        }

        return iResult;
    }

    /**
     * 회원 중복 닉네임 검색
     *
     * @param memberNickDto (nick)
     * @return count
     */
    public Boolean checkDupleNick(MemberNickDto memberNickDto) {
        Integer iCount = memberNickDaoSub.getCountByNick(memberNickDto);

        return iCount > 0;
    }

    /**
     * 조회 할 회원 UUID
     *
     * @param memberUuid
     * @return
     */
    public String getNickByUuid(String memberUuid) {
        // member uuid 검증
        if (memberUuid == null || memberUuid.equals("")) {
            // 회원 UUID가 존재하지 않습니다.
            throw new CustomException(CustomError.MEMBER_UUID_EMPTY);
        }

        return memberNickDaoSub.getNickByUuid(memberUuid);
    }
    /*****************************************************
     *  SubFunction - Insert
     ****************************************************/
    /**
     * 회원 닉네임 로그 등록
     *
     * @param memberNickDto (memberUuid/nick/regDate)
     */
    public void insertLog(MemberNickDto memberNickDto) {
        memberNickDto.setRegDate(dateLibrary.getDatetime());
        memberNickDao.insertLog(memberNickDto);
    }

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/
    /**
     * 회원 닉네임 수정
     *
     * @param memberNickDto (memberUuid/nick/regDate)
     * @return InsertedIdx
     */
    public Integer nickUpdate(MemberNickDto memberNickDto) {
        memberNickDto.setModiDate(dateLibrary.getDatetime());

        return memberNickDao.nickUpdate(memberNickDto);
    }
}
