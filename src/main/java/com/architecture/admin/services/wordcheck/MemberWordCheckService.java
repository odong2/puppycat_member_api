package com.architecture.admin.services.wordcheck;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.daosub.wordcheck.MemberWordCheckDaoSub;
import com.architecture.admin.models.dto.wordcheck.MemberWordCheckDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;


/*****************************************************
 * 회원 금칙어
 ****************************************************/
@Service
@RequiredArgsConstructor
@Transactional
public class MemberWordCheckService extends BaseService {

    private final MemberWordCheckDaoSub memberWordcheckDaoSub;
    @Value("${word.check.member.nick.type}")
    private int nickWordChk;  // 회원 닉네임 금칙어 타입
    @Value("${word.check.member.intro.type}")
    private int introWordChk;  // 회원 인트로 금칙어 타입

    /*****************************************************
     *  Modules
     ****************************************************/

    /**
     * 회원 금칙어 체크
     *
     * @param word 검사 할 단어
     */
    public String memberWordCheck(String word, int type) {

        // validate
        if (ObjectUtils.isEmpty(word)) {
            throw new CustomException(CustomError.WORD_EMPTY);
        }
        if (ObjectUtils.isEmpty(type)) {
            throw new CustomException(CustomError.WORD_TYPE_EMPTY);
        }

        // 금칙어 리스트
        List<MemberWordCheckDto> list = getList(type);
        for (MemberWordCheckDto str : list) {
            String noWord = str.getWord();
            // 금칙어가 포함되어있으면
            if (word.contains(noWord)) {

                if (type == nickWordChk) { // type : 닉네임
                    // 사용할 수 없는 문자가 포함되어 있습니다.
                    throw new CustomException(CustomError.NICK_STRING_ERROR);
                } else if (type == introWordChk) { // type : 소개글
                    String changeWord = str.getChangeWord();
                    // 금칙어가 포함되어있으면 치환
                    word = word.replace(noWord, changeWord);
                }

            }
        }
        return word;
    }

    /**
     * 회원 금칙어 체크
     * [금칙어 리스트 외부에서 조회하여 파라미터로 넘김] -> 금칙어 리스트 한번만 조회하기 위해
     *
     * @param word        검사 내용
     * @param badWordList 금칙어 리스트
     * @return
     */
    public String memberWordCheck(String word, List<MemberWordCheckDto> badWordList) {
        if (!ObjectUtils.isEmpty(word) && !ObjectUtils.isEmpty(badWordList)) {
            for (MemberWordCheckDto str : badWordList) {
                String noWord = str.getWord();
                String changeWord = str.getChangeWord();
                // 금칙어가 포함되어있으면 치환
                word = word.replace(noWord, changeWord);
            }
        }
        return word;
    }

    /*****************************************************
     *  SubFunction - select
     ****************************************************/
    /**
     * 금칙어 목록
     *
     * @return 금칙어 리스트
     */
    public List<MemberWordCheckDto> getList(int type) {

        return memberWordcheckDaoSub.getList(type);
    }

    /**
     * 회원 인트로 금칙어 리스트
     *
     * @return
     */
    public List<MemberWordCheckDto> getIntroBadWordList() {
        return getList(introWordChk);
    }

}
