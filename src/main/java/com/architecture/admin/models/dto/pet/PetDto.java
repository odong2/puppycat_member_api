package com.architecture.admin.models.dto.pet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PetDto {

    /**
     * puppycat_pet
     */
    private Long idx;           // pet.idx
    private String uuid;        // 고유아이디
    private String petUuid;     // 고유아이디
    private String name;        // 이름
    private Long breedIdx;      // pet_breed.idx
    private String regDate;     // 등록일(UTC)
    private String regDateTz;   // 등록일 타임존
    private String modiDate;    // 수정일(UTC)
    private String modiDateTz;  // 수정일 타임존
    private String memberUuid;     // 회원 uuid

    /**
     * puppycat_pet_info
     */
    private Long number;     // 등록번호
    private Integer gender;  // 성별 (1:male, 2:female, 3:neutering(수컷중성화), 4:spaying(암컷중성화)
    private Integer size;    // 크키 (1:small, 2:medium, 3:large)
    private Float weight;  // 몸무게
    private Integer age;    // 연령 (1:puppy, 2:adult, 3:senior)
    private String birth;    // 생년월일

    /**
     * puppycat_pet_type
     */
    private String typeName;  // 반려동물 종류

    /**
     * puppycat_pet_profile_img
     */
    private String imgUuid;             // 이미지 고유 아이디
    private String imgUrl;              // 이미지 url (도메인 제외)
    private String uploadName;          // 업로드 파일명
    private String uploadPath;          // 업로드 경로
    private Integer imgWidth;           // 이미지 가로 사이즈
    private Integer imgHeight;          // 이미지 세로 사이즈
    private Integer imgSort;            // 이미지 정렬 순서
    private Integer resetState;         // 프로필 이미지 초기화 상태 값
    private List<MultipartFile> uploadFile;    // 업로드 이미지

    /**
     * puppycat_pet_profile_intro
     */
    private String intro;   // 프로필 소개글

    /**
     * puppycat_pet_breed
     */
    private Integer typeIdx;  // pet_type.idx
    private String breedName; // 반려동물 품종
    private Long sort;        // 정렬 순서

    /**
     * puppycat_pet_health_type
     */
    private Long healthIdx;             // pet_health_type.idx
    private List<Long> healthIdxList;   // 건강질환 idx 리스트
    private String healthName;          // 건강질환 이름
    private List<HealthDto> healthList; // 건강질환 List
    private String healthEtc;           // 건강질환 기타

    /**
     * puppycat_pet_allergy_type
     */
    private Long allergyIdx;                // pet_allergy_type.idx
    private List<Long> allergyIdxList;      // 알러지 idx 리스트
    private String allergyName;             // 알러지 이름
    private List<AllergyDto> allergyList;   // 알러지 List
    private String allergyEtc;              // 알러지 기타

    /**
     * puppycat_pet_info_personality
     */
    private Long personalityIdx;                  // pet_personality_type.idx
    private List<Long> personalityIdxList;        // 성격 idx 리스트
    private String personalityName;               // 성격 이름
    private List<PersonalityDto> personalityList; // 성격 List
    private String personalityEtc;                // 성격 기타

    /**
     * etc
     */
    private String sizeText;      // 사이즈 텍스트
    private String genderText;    // 성별 텍스트
    private String ageText;       // 연령 텍스트

    // sql
    private Long insertedIdx;

}
