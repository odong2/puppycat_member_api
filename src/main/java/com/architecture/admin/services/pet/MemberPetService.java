package com.architecture.admin.services.pet;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.daosub.pet.MemberPetDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.pet.AllergyDto;
import com.architecture.admin.models.dto.pet.HealthDto;
import com.architecture.admin.models.dto.pet.PersonalityDto;
import com.architecture.admin.models.dto.pet.PetDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPetService extends BaseService {

    private final MemberPetDaoSub memberPetDaoSub;
    private final PetService petService;
    @Value("${pet.size.small}")
    private int sizeSmall;          // 작음
    @Value("${pet.size.medium}")
    private int sizeMedium;         // 중간
    @Value("${pet.size.large}")
    private int sizeLarge;          // 큼
    @Value("${pet.age.puppy}")
    private int agePuppy;          // 퍼피
    @Value("${pet.age.adult}")
    private int ageAdult;         // 어덜트
    @Value("${pet.age.senior}")
    private int ageSenior;          // 시니어
    @Value("${pet.gender.male}")
    private int genderMale;         // 수컷
    @Value("${pet.gender.female}")
    private int genderFemale;       // 암컷
    @Value("${pet.gender.neutering}")
    private int genderNeutering;    // 수컷 중성화
    @Value("${pet.gender.spaying}")
    private int genderSpaying;      // 암컷 중성화
    @Value("${cloud.aws.s3.img.url}")
    private String imgDomain;

    /**********************************************
     * SELECT
     **********************************************/
    /**
     * 반려 동물 리스트
     *
     * @param searchDto : loginMemberUuid(로그인 회원 uuid), memberUuid(회원 uuid)
     * @return List<PetDto>
     */
    public List<PetDto> getMemberPetList(SearchDto searchDto) {

        // 로그인 한 경우 회원 유효성 검사
        if (!ObjectUtils.isEmpty(searchDto.getLoginMemberUuid())) {
            chkMemberUuid(searchDto.getLoginMemberUuid());
        }

        // 회원 유효성 검사
        chkMemberUuid(searchDto.getMemberUuid());

        // 리턴할 리스트 생성
        List<PetDto> petList = new ArrayList<>();

        // 이미지 도메인 세팅
        searchDto.setImgDomain(imgDomain);

        // 회원 반려 동물 카운트
        int totalCnt = memberPetDaoSub.getTotalMemberPetCnt(searchDto);

        if (totalCnt > 0) {
            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);
            petList = memberPetDaoSub.getMemberPetList(searchDto);

            // 텍스트 변환
            stateText(petList);
        }

        return petList;
    }

    /**
     * 반려 동물 상세
     *
     * @param searchDto : loginMemberUuid(로그인 회원 uuid), petUuid(반려동물 uuid)
     * @return PetDto
     */
    public PetDto getPetView(SearchDto searchDto) {

        // data
        String loginMemberUuid = searchDto.getLoginMemberUuid();
        String petUuid = searchDto.getPetUuid();

        // 회원 uuid 유효성 검사
        chkMemberUuid(searchDto.getLoginMemberUuid());

        // 반려동물 uuid 유효성 검사
        Boolean bCheckPet = petService.checkDupleUuid(searchDto.getPetUuid());
        if (Boolean.FALSE.equals(bCheckPet)) { // 유효하지 않은 반려동물 uuid
            throw new CustomException(CustomError.PET_UUID_ERROR);
        }

        // 나의 반려동물인지 검사
        PetDto petDto = PetDto.builder()
                .petUuid(petUuid)
                .memberUuid(loginMemberUuid)
                .build();
        Boolean bCheckMyPet = petService.checkMyPet(petDto);
        if (Boolean.FALSE.equals(bCheckMyPet)) { // 나의 반려동물이 아님
            throw new CustomException(CustomError.PET_NOT_MY_PET);
        }

        // 이미지 도메인 세팅
        searchDto.setImgDomain(imgDomain);

        // 반려동물 상세
        PetDto petView = memberPetDaoSub.getPetView(searchDto);

        // 반려동물 성격 정보 set
        List<PersonalityDto> personalityList = memberPetDaoSub.getPetPersonalityInfo(searchDto);
        petView.setPersonalityList(personalityList);

        // 반려동물 건강질환 정보 set
        List<HealthDto> healthList = memberPetDaoSub.getPetHealthInfo(searchDto);
        petView.setHealthList(healthList);

        // 반려동물 알러지 정보 set
        List<AllergyDto> allergyList = memberPetDaoSub.getPetAllergyInfo(searchDto);
        petView.setAllergyList(allergyList);

        // 텍스트 변환
        stateText(petView);

        return petView;
    }

    /*****************************************************
     *  SubFunction - Select
     ****************************************************/

    /*****************************************************
     *  SubFunction - Insert
     ****************************************************/

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/

    /*****************************************************
     *  SubFunction - Delete
     ****************************************************/

    /*****************************************************
     *  ETC
     ****************************************************/

    /**
     * 텍스트 변환 리스트
     *
     * @param petList petDto
     */
    private void stateText(List<PetDto> petList) {
        for (PetDto petDto : petList) {
            stateText(petDto);
        }
    }

    /**
     * 텍스트 변환
     *
     * @param petDto size, age, gender
     */
    public void stateText(PetDto petDto) {

        /** size **/
        if (petDto.getSize() != null) {
            if (petDto.getSize() == sizeSmall) {         // 작음
                petDto.setSizeText(super.langMessage("lang.pet.size.small"));
            } else if (petDto.getSize() == sizeMedium) { // 중간
                petDto.setSizeText(super.langMessage("lang.pet.size.medium"));
            } else if (petDto.getSize() == sizeLarge) {  // 큼
                petDto.setSizeText(super.langMessage("lang.pet.size.large"));
            }
        }

        /** age **/
        if (petDto.getAge() != null) {
            if (petDto.getAge() == agePuppy) {         // 퍼피
                petDto.setAgeText(super.langMessage("lang.pet.age.puppy"));
            } else if (petDto.getAge() == ageAdult) { // 어덜트
                petDto.setAgeText(super.langMessage("lang.pet.age.adult"));
            } else if (petDto.getAge() == ageSenior) {  // 시니어
                petDto.setAgeText(super.langMessage("lang.pet.age.senior"));
            }
        }

        /** gender **/
        if (petDto.getGender() != null) {
            if (petDto.getGender() == genderMale) {    // 수컷
                petDto.setGenderText(super.langMessage("lang.pet.gender.male"));
            } else if (petDto.getGender() == genderFemale) { // 암컷
                petDto.setGenderText(super.langMessage("lang.pet.gender.female"));
            } else if (petDto.getGender() == genderNeutering) { //수컷 중성화
                petDto.setGenderText(super.langMessage("lang.pet.gender.neutering"));
            } else if (petDto.getGender() == genderSpaying) { // 암컷 중성화
                petDto.setGenderText(super.langMessage("lang.pet.gender.spaying"));
            }
        }

        // 앞단에 쓰지 않는 타입 값 null 처리
        petDto.setGender(null);
        petDto.setSize(null);
        petDto.setAge(null);
    }

}
