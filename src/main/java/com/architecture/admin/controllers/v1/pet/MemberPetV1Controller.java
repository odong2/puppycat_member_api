package com.architecture.admin.controllers.v1.pet;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.pet.PetDto;
import com.architecture.admin.services.pet.MemberPetService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/member")
public class MemberPetV1Controller extends BaseController {

    private static final String SUCCESS_SEARCH_MESSAGE = "lang.common.success.search";
    private final MemberPetService memberPetService;
    @Value("${use.member.pet.list}")
    private boolean usePetList; // 반려 동물 리스트
    @Value("${use.pet.view}")
    private boolean usePetView; // 반려 동물 상세

    /**
     * 반려 동물 리스트
     *
     * @param token      : accessToken
     * @param memberUuid : memberUuid
     * @param searchDto  : page, limit
     * @return
     */
    @GetMapping("/{memberUuid}/pet")
    public ResponseEntity<String> getPetList(@RequestHeader(value = "Authorization", defaultValue = "") String token,
                                             @PathVariable String memberUuid,
                                             @ModelAttribute SearchDto searchDto) {

        // 토큰에서 로그인한 회원 UUID 추출 & 세팅
        searchDto.setLoginMemberUuid(super.getAccessMemberUuid(token));

        // 조회할 회원 uuid 세팅
        searchDto.setMemberUuid(memberUuid);

        List<PetDto> petList;

        if (usePetList) {
            petList = memberPetService.getMemberPetList(searchDto);
        } else {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }

        JSONObject data = new JSONObject();
        data.put("list", petList);
        data.put("params", new JSONObject(searchDto));

        String sMessage = super.langMessage(SUCCESS_SEARCH_MESSAGE); // 검색 성공

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 반려 동물 상세
     *
     * @param token     : accessToken
     * @param petUuid   : petUuid
     * @param searchDto : page, limit
     * @return ResponseEntity
     */
    @GetMapping("/pet/{petUuid}")
    public ResponseEntity<String> getPetView(@RequestHeader(value = "Authorization") String token,
                                             @PathVariable String petUuid,
                                             @ModelAttribute SearchDto searchDto) {

        // 토큰에서 로그인한 회원 UUID 추출 & 세팅
        searchDto.setLoginMemberUuid(super.getAccessMemberUuid(token));

        // 조회할 반려동물 uuid 세팅
        searchDto.setPetUuid(petUuid);

        PetDto petDto;

        if (usePetView) {
            petDto = memberPetService.getPetView(searchDto);
        } else {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }

        JSONObject data = new JSONObject();
        data.put("view", petDto);
        data.put("params", new JSONObject(searchDto));

        String sMessage = super.langMessage(SUCCESS_SEARCH_MESSAGE); // 검색 성공

        return displayJson(true, "1000", sMessage, data);
    }

}
