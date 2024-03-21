package com.architecture.admin.models.dao.pet;

import com.architecture.admin.models.dto.pet.AllergyDto;
import com.architecture.admin.models.dto.pet.HealthDto;
import com.architecture.admin.models.dto.pet.PersonalityDto;
import com.architecture.admin.models.dto.pet.PetDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface PetDao {

    /*****************************************************
     * Insert
     ****************************************************/
    /**
     * 반려동물 등록
     * [pet]
     *
     * @param petDto uuid, name, type, breedIdx, regDate
     * @return Integer
     */
    Integer insertPet(PetDto petDto);

    /**
     * 회원-반려동물 매핑 등록
     * [sns_member_pet]
     *
     * @param petDto memberIdx, insertedIdx, regDate
     * @return Integer
     */
    Integer insertMemberPet(PetDto petDto);

    /**
     * 반려동물 정보 이미지 등록
     * [pet_info]
     *
     * @param petDto insertedIdx, number, gender, size, weight, birth, personalityIdx, regDate
     * @return Integer
     */
    Integer insertPetInfo(PetDto petDto);

    /**
     * 성격 기타 등록
     *
     * @param petDto insertedIdx, personalityEtc, regDate
     * @return Integer
     */
    Integer insertPetPersonalityEtc(PetDto petDto);

    /**
     * 건강질환 기타 등록
     *
     * @param petDto insertedIdx, personalityEtc, regDate
     * @return Integer
     */
    Integer insertPetHealthEtc(PetDto petDto);

    /**
     * 알러지 기타 등록
     *
     * @param petDto insertedIdx, personalityEtc, regDate
     * @return Integer
     */
    Integer insertPetAllergyEtc(PetDto petDto);

    /**
     * 프로필 이미지 초기화
     *
     * @param petDto insertedIdx, imgUuid, regDate
     * @return Integer
     */
    Integer insertPetProfileImgInit(PetDto petDto);

    /**
     * 프로필 이미지 등록
     *
     * @param uploadResponse uuid, petIdx, url, uploadName, uploadPath, imgWidth, imgHeight, sort, regDate
     * @return Integer
     */
    Integer insertPetProfileImg(HashMap<String, Object> uploadResponse);

    /**
     * 프로필 이미지 로그 등록
     *
     * @param uploadResponse uuid, petIdx, url, uploadName, uploadPath, imgWidth, imgHeight, sort, regDate
     * @return Integer
     */
    Integer insertPetProfileImgLog(HashMap<String, Object> uploadResponse);

    /**
     * 프로필 소개글 등록
     *
     * @param petDto
     * @return Integer
     */
    Integer insertPetProfileIntro(PetDto petDto);

    /**
     * 프로필 이미지 로그 등록
     *
     * @param petDto
     * @return Integer
     */
    Integer insertPetProfileIntroLog(PetDto petDto);

    /**
     * 알러지 등록
     *
     * @param list uuid allergyIdx regDate
     * @return Integer
     */
    Integer insertPetAllergy(List<AllergyDto> list);

    /**
     * 건강 질환 등록
     *
     * @param list uuid healthIdx regDate
     * @return Integer
     */
    Integer insertPetHealth(List<HealthDto> list);

    /**
     * 성격 등록
     *
     * @param list uuid personalityIdx regDate
     * @return Integer
     */
    Integer insertPetPersonality(List<PersonalityDto> list);

    /*****************************************************
     * Update
     ****************************************************/

    /**
     * 반려동물 수정
     *
     * @param petDto
     * @return
     */
    int updatePet(PetDto petDto);

    /**
     * 반려동물 기본 정보 수정
     *
     * @param petDto
     * @return
     */
    int updatePetInfo(PetDto petDto);

    /**
     * 성격 정보 수정
     *
     * @param petDto
     * @return
     */
    int updatePetPersonalityInfo(PetDto petDto);

    /**
     * 건강 정보 수정
     *
     * @param petDto
     * @return
     */
    int updatePetHealthInfo(PetDto petDto);

    /**
     * 알러지 정보 수정
     *
     * @param petDto
     * @return
     */
    int updatePetAllergyInfo(PetDto petDto);

    /**
     * 프로필 이미지 수정
     *
     * @param uploadResponse petUuid, url, uploadName, uploadPath, imgWidth, imgHeight, sort, regDate
     * @return
     */
    int updatePetProfileImg(Map<String, Object> uploadResponse);

    /**
     * 프로필 소개글 수정
     *
     * @param petDto petUuid intro
     * @return
     */
    int updatePetProfileIntro(PetDto petDto);

    /*****************************************************
     * delete
     ****************************************************/
    /**
     * 기타 성격 삭제
     *
     * @param petUuid
     * @return
     */
    int deletePetPersonalityEtc(String petUuid);

    /**
     * 기타 건강질환 삭제
     *
     * @param petUuid
     * @return
     */
    int deletePetHealthEtc(String petUuid);

    /**
     * 기타 알러지 삭제
     *
     * @param petUuid
     * @return
     */
    int deletePetAllergyEtc(String petUuid);

    /**
     * 성격 삭제
     *
     * @param petDto idx, personalityIdx
     * @return
     */
    int deletePetPersonality(PetDto petDto);

    /**
     * 알러지 삭제
     *
     * @param petDto idx, allergyIdx
     * @return
     */
    int deletePetAllergy(PetDto petDto);

    /**
     * 건강 질환 삭제
     *
     * @param petDto idx, healthIdx
     * @return
     */
    int deletePetHealth(PetDto petDto);

    /**
     * 회원 소유 펫 삭제
     *
     * @param petDto : idx, memberIdx
     * @return
     */
    int deletePet(PetDto petDto);
}
