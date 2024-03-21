package com.architecture.admin.models.daosub.pet;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.pet.AllergyDto;
import com.architecture.admin.models.dto.pet.HealthDto;
import com.architecture.admin.models.dto.pet.PersonalityDto;
import com.architecture.admin.models.dto.pet.PetDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MemberPetDaoSub {

    /**
     * 반려동물 카운트
     *
     * @param searchDto memberUuid
     * @return count
     */
    int getTotalMemberPetCnt(SearchDto searchDto);

    /**
     * 반려동물 리스트
     *
     * @param searchDto memberUuid
     * @return List<PetDto>
     */
    List<PetDto> getMemberPetList(SearchDto searchDto);

    /**
     * 반려동물 상세
     *
     * @param searchDto petUuid loginMemberUuid
     * @return PetDto
     */
    PetDto getPetView(SearchDto searchDto);

    /**
     * 반려동물 성격 리스트
     *
     * @param searchDto petUuid
     * @return List<PersonalityDto>
     */
    List<PersonalityDto> getPetPersonalityInfo(SearchDto searchDto);

    /**
     * 반려동물 건강질환 리스트
     *
     * @param searchDto petUuid
     * @return List<HealthDto>
     */
    List<HealthDto> getPetHealthInfo(SearchDto searchDto);

    /**
     * 반려동물 알러지 리스트
     *
     * @param searchDto petUuid
     * @return List<AllergyDto>
     */
    List<AllergyDto> getPetAllergyInfo(SearchDto searchDto);
}