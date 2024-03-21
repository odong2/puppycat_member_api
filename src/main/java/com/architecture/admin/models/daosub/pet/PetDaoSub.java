package com.architecture.admin.models.daosub.pet;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.pet.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PetDaoSub {

    /*****************************************************
     * Select
     ****************************************************/
    /**
     * 반려동물 고유아이디 중복체크 (pet)
     *
     * @param uuid 고유아이디
     * @return count
     */
    int getCountByUuid(String uuid);

    /**
     * 품종 idx 유효성 검사 (pet_breed)
     *
     * @param breedIdx 품종 idx
     * @return count
     */
    Integer getCountByBreedIdx(Long breedIdx);

    /**
     * 성격 idx 유효성 검사
     *
     * @param personalityIdx 성격 idx
     * @return count
     */
    Integer getCountByPersonalityIdx(Long personalityIdx);

    /**
     * 건강질환 idx 유효성 검사
     *
     * @param healthIdx 건강질환 idx
     * @return count
     */
    Integer getCountByHealthIdx(Long healthIdx);

    /**
     * 알러지 idx 유효성 검사
     *
     * @param allergyIdx 알러지 idx
     * @return count
     */
    Integer getCountByAllergyIdx(Long allergyIdx);

    /**
     * 반려동물 프로필 이미지 고유아이디 중복체크
     *
     * @param uuid 고유아이디 (pet_profile_img)
     * @return count
     */
    Integer getCountByImgUuid(String uuid);

    /**
     * 품종 카운트
     *
     * @param searchDto
     * @return
     */
    int getBreedTotalCnt(SearchDto searchDto);

    /**
     * 품종 리스트
     *
     * @param searchDto
     * @return
     */
    List<BreedDto> getBreedList(SearchDto searchDto);

    /**
     * 알러지 카운트
     *
     * @param searchDto
     * @return
     */
    int getAllergyTotalCnt(SearchDto searchDto);

    /**
     * 알러지 리스트
     *
     * @param searchDto
     * @return
     */
    List<AllergyDto> getAllergyList(SearchDto searchDto);

    /**
     * 건강질환 카운트
     *
     * @param searchDto
     * @return
     */
    int getHealthTotalCnt(SearchDto searchDto);

    /**
     * 건강질환 리스트
     *
     * @param searchDto
     * @return
     */
    List<HealthDto> getHealthList(SearchDto searchDto);

    /**
     * 성격 전체 카운트
     *
     * @param searchDto
     * @return
     */
    int getPersonalityTotalCnt(SearchDto searchDto);

    /**
     * 성격 리스트
     *
     * @param searchDto
     * @return
     */
    List<PersonalityDto> getPersonalityList(SearchDto searchDto);

    /**
     * 회원이 등록한 펫인지 조회
     *
     * @param petDto
     * @return
     */
    int getMemberPetCnt(PetDto petDto);

    /**
     * 기존 성격 idx 조회
     *
     * @param petUuid
     * @return
     */
    List<Long> getPersonalityIdxList(String petUuid);

    /**
     * 기존 건강 질환 idx 조회
     *
     * @param petUuid
     * @return
     */
    List<Long> getHealthIdxList(String petUuid);

    /**
     * 기존 알러지 idx 조회
     *
     * @param petUuid
     * @return
     */
    List<Long> getAllergyIdxList(String petUuid);

    /**
     * 기타 성격 조회
     *
     * @param petUuid
     * @return
     */
    String getPersonalityEtc(String petUuid);

    /**
     * 기타 건강질환 조회
     *
     * @param petUuid
     * @return
     */
    String getHealthEtc(String petUuid);

    /**
     * 기타 알러지 조회
     *
     * @param petUuid
     * @return
     */
    String getAllergyEtc(String petUuid);

    /**
     * 삭제된 성격 type_idx 조회
     *
     * @param petUuid
     * @return
     */
    List<Long> getDeletedPetPersonalityList(String petUuid);

    /**
     * 삭제된 건강 type_idx 조회
     *
     * @param petUuid
     * @return
     */
    List<Long> getDeletedPetHealthList(String petUuid);

    /**
     * 삭제된 알러지 type_idx 조회
     *
     * @param petUuid
     * @return
     */
    List<Long> getDeletedPetAllergyList(String petUuid);

}
