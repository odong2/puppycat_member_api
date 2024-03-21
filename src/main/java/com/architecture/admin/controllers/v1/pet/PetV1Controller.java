package com.architecture.admin.controllers.v1.pet;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.pet.*;
import com.architecture.admin.services.pet.PetService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/pet")
public class PetV1Controller extends BaseController {

    private static final String SUCCESS_SEARCH_MESSAGE = "lang.common.success.search"; // 검색 성공
    private final PetService petService;
    @Value("${use.pet.register}")
    private boolean usePetRegister;   // 반려 동물 등록 스위치
    @Value("${use.pet.modify}")
    private boolean usePetModify;    // 반려 동물 수정 스위치
    @Value("${use.pet.delete}")
    private boolean usePetDelete;    // 반려 동물 삭제 스위치
    @Value("${use.pet.breed.list}")
    private boolean useBreedList;     // 품종 리스트 스위치
    @Value("${use.pet.allergy.list}")
    private boolean useAllergyList;   // 알러지 리스트 스위치
    @Value("${use.pet.health.list}")
    private boolean useHealthList;   // 건강질환 리스트 스위치
    @Value("${use.pet.personality.list}")
    private boolean usePersonalityList; // 성격 리스트 스위치

    /**
     * 반려동물 등록
     *
     * @param token  accessToken
     * @param petDto name, number, type, gender, breedIdx, breedNameEtc
     *               size, weight, age, birth, personalityCode, personalityEtc, uploadFile
     *               allergyIdxList, healthIdxList
     * @return ResponseEntity
     */
    @PostMapping("")
    public ResponseEntity registPet(@RequestHeader(value = "Authorization") String token,
                                    @ModelAttribute PetDto petDto) {

        Integer result;

        // 토큰에서 회원 UUID 추출 & 세팅
        petDto.setMemberUuid(super.getAccessMemberUuid(token));

        if (usePetRegister) {
            // 반려동물 등록
            result = petService.registPet(petDto);
        } else {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }

        // 등록 완료
        String sMessage = super.langMessage("lang.pet.success.regist");
        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 반려 동물 수정
     *
     * @param token     accessToken
     * @param petUuid   petUuid
     * @param petDto    name, number, type, gender, breedIdx, breedNameEtc
     *                  size, weight, age, birth, personalityCode, personalityEtc, uploadFile
     *                  allergyIdxList, healthIdxList, resetState
     * @return
     */
    @PutMapping("/{petUuid}")
    public ResponseEntity modifyPet(@RequestHeader(value = "Authorization") String token,
                                    @PathVariable String petUuid,
                                    @ModelAttribute PetDto petDto) {

        // 토큰에서 회원 UUID 추출 & 세팅
        petDto.setMemberUuid(super.getAccessMemberUuid(token));

        if (usePetModify) {
            // 반려동물 수정
            petDto.setPetUuid(petUuid);
            petService.modifyPet(petDto);
        } else {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }

        // 수정 완료
        String sMessage = super.langMessage("lang.pet.success.modify");

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 반려 동물 삭제
     *
     * @param token   accessToken
     * @param petUuid petUuid
     * @return
     */
    @DeleteMapping("/{petUuid}")
    public ResponseEntity deletePet(@RequestHeader(value = "Authorization") String token,
                                    @PathVariable String petUuid) {

        // 토큰에서 회원 UUID 추출 & 세팅
        String memberUuid = super.getAccessMemberUuid(token);
        PetDto petDto = PetDto.builder()
                .petUuid(petUuid)
                .memberUuid(memberUuid)
                .build();

        if (usePetDelete) {
            // 반려 동물 삭제
            petService.deletePet(petDto);
        } else {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }

        // 삭제 완료
        String sMessage = super.langMessage("lang.pet.success.delete");

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 품종 리스트 & 검색
     *
     * @param token   accessToken
     * @param searchDto searchWord 검색어 type 타입
     * @return
     */
    @GetMapping("/breed")
    public ResponseEntity<String> getPetBreedList(@RequestHeader(value = "Authorization") String token,
                                                  @ModelAttribute SearchDto searchDto) {

        // 토큰에서 회원 UUID 추출 & 세팅
        searchDto.setMemberUuid(super.getAccessMemberUuid(token));

        List<BreedDto> breedList;

        if (useBreedList) {
            breedList = petService.getBreedList(searchDto);
        } else {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }

        JSONObject data = new JSONObject();
        data.put("list", breedList);
        data.put("params", new JSONObject(searchDto));

        String sMessage = super.langMessage(SUCCESS_SEARCH_MESSAGE); // 검색 성공

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 알러지 리스트
     *
     * @param token   accessToken
     * @param searchDto
     * @return
     */
    @GetMapping("/allergy")
    public ResponseEntity<String> getPetAllergyList(@RequestHeader(value = "Authorization") String token,
                                                    @ModelAttribute SearchDto searchDto) {

        // 토큰에서 회원 UUID 추출 & 세팅
        searchDto.setMemberUuid(super.getAccessMemberUuid(token));

        List<AllergyDto> allergyList;

        if (useAllergyList) {
            allergyList = petService.getAllergyList(searchDto);
        } else {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }


        JSONObject data = new JSONObject();
        data.put("list", allergyList);
        data.put("params", new JSONObject(searchDto));

        String sMessage = super.langMessage(SUCCESS_SEARCH_MESSAGE); // 검색 성공

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 건강질환 리스트
     *
     * @param token   accessToken
     * @param searchDto
     * @return
     */
    @GetMapping("/health")
    public ResponseEntity<String> getPetHealthList(@RequestHeader(value = "Authorization") String token,
                                                   @ModelAttribute SearchDto searchDto) {

        // 토큰에서 회원 UUID 추출 & 세팅
        searchDto.setMemberUuid(super.getAccessMemberUuid(token));

        List<HealthDto> healthList;

        if (useHealthList) {
            healthList = petService.getHealthList(searchDto);
        } else {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }


        JSONObject data = new JSONObject();
        data.put("list", healthList);
        data.put("params", new JSONObject(searchDto));

        String sMessage = super.langMessage(SUCCESS_SEARCH_MESSAGE); // 검색 성공

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 성격 리스트
     *
     * @param token   accessToken
     * @param searchDto
     * @return
     */
    @GetMapping("/personality")
    public ResponseEntity<String> getPetPersonalityList(@RequestHeader(value = "Authorization") String token,
                                                        @ModelAttribute SearchDto searchDto) {

        // 토큰에서 회원 UUID 추출 & 세팅
        searchDto.setMemberUuid(super.getAccessMemberUuid(token));

        List<PersonalityDto> personalityList;

        if (usePersonalityList) {
            personalityList = petService.getPersonalityList(searchDto);
        } else {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }

        JSONObject data = new JSONObject();
        data.put("list", personalityList);
        data.put("params", new JSONObject(searchDto));

        String sMessage = super.langMessage(SUCCESS_SEARCH_MESSAGE); // 검색 성공

        return displayJson(true, "1000", sMessage, data);

    }

}
