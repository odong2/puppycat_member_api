package com.architecture.admin.controllers.v2;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.services.login.JoinService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/join")
public class JoinV2Controller extends BaseController {
    private final JoinService joinService;

    /**
     * 소셜 회원가입
     *
     * @param memberDto simpleId, id, password, passwordConfirm, isSimple, simpleType, partner
     *                  nick, name, phone, gender, birth, ci, di, accessToken, refreshToken, selectPolicy_{idx}
     * @return
     * @throws Exception
     */
    @PostMapping("/social")
    public ResponseEntity socialMemberJoin(@RequestBody MemberDto memberDto) throws Exception {

        // 회원 가입 된 아이디인지 체크
        Boolean bDupleId = joinService.checkDupleId(memberDto);

        // 가입 되어 있으면
        if (Boolean.TRUE.equals(bDupleId)) {
            throw new CustomException(CustomError.ID_DUPLE);
        }

        // 회원가입 처리
        Map hmap = joinService.registV2(memberDto);

        // 성공 했을 경우
        boolean result = true;
        String code = "1000";
        String sErrorMessage = "lang.login.success.regist"; // 가입되었습니다
        String message = super.langMessage(sErrorMessage);

        // set return data
        JSONObject data = new JSONObject();
        data.put("location", "/");
        data.put("isAvailableJoin", hmap.get("isAvailableJoin"));
        data.put("id", hmap.get("id"));
        data.put("simpleType", hmap.get("simpleType"));

        // 이미 가입된 계정이 존재하는 경우
        if ((boolean) hmap.get("isAvailableJoin") == false) {
            result = false; // 가입 실패
            code = "SIJD-3999";
            message = super.langMessage("lang.login.exception.simple.join.duple"); // 이미 가입된 계정이 있습니다.
        }


        return displayJson(result, code, message, data);
    }

    /**
     * TODO : 테스트 종료후 삭제 예정
     * 소셜 회원가입 (테스트 용)
     *
     * @param memberDto simpleId, id, password, passwordConfirm, isSimple, simpleType, partner
     *                  nick, name, phone, gender, birth, ci, di, accessToken, refreshToken, selectPolicy_{idx}
     * @return
     * @throws Exception
     */
    @PostMapping("/social/test")
    public ResponseEntity socialMemberJoinTest(@RequestBody MemberDto memberDto) throws Exception {

        // 회원 가입 된 아이디인지 체크
        Boolean bDupleId = joinService.checkDupleId(memberDto);

        // 가입 되어 있으면
        if (Boolean.TRUE.equals(bDupleId)) {
            throw new CustomException(CustomError.ID_DUPLE);
        }
        // 이름 암호화
        memberDto.setName(securityLibrary.aesEncrypt(memberDto.getName()));
        // 휴대폰번호 암호화
        memberDto.setPhone(securityLibrary.aesEncrypt(memberDto.getPhone()));
        // 성별 암호화
        memberDto.setGender(securityLibrary.aesEncrypt(memberDto.getGender()));
        // 생일 암호화
        memberDto.setBirth(securityLibrary.aesEncrypt(memberDto.getBirth()));
        // ci 암호화
        memberDto.setCi(securityLibrary.aesEncrypt(memberDto.getCi()));
        // di 암호화
        memberDto.setDi(securityLibrary.aesEncrypt(memberDto.getDi()));

        // 회원가입 처리
        Map hmap = joinService.registV2(memberDto);

        // 성공 했을 경우
        boolean result = true;
        String code = "1000";
        String sErrorMessage = "lang.login.success.regist"; // 가입되었습니다
        String message = super.langMessage(sErrorMessage);

        // set return data
        JSONObject data = new JSONObject();
        data.put("location", "/");
        data.put("isAvailableJoin", hmap.get("isAvailableJoin"));
        data.put("id", hmap.get("id"));
        data.put("simpleType", hmap.get("simpleType"));

        // 이미 가입된 계정이 존재하는 경우
        if ((boolean) hmap.get("isAvailableJoin") == false) {
            result = false; // 가입 실패
            code = "SIJD-3999";
            message = super.langMessage("lang.login.exception.simple.join.duple"); // 이미 가입된 계정이 있습니다.
        }


        return displayJson(result, code, message, data);
    }
}

