package com.architecture.admin.services.pet;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.S3Library;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.pet.PetDao;
import com.architecture.admin.models.daosub.pet.PetDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.pet.*;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.wordcheck.PetWordCheckService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Transactional
public class PetService extends BaseService {

    private static final String REGISTER = "register";
    private static final String MODIFY = "modify";
    private final PetDao petDao;
    private final PetDaoSub petDaoSub;
    private final S3Library s3Library;
    private final PetWordCheckService petWordcheckService;
    @Value("${pet.name.max}")
    private int nameTextMax;            // 이름 최대 글자수
    @Value("${pet.intro.text.max}")
    private int introTextMax;           // 소개글 최대 글자수
    @Value("${pet.personality.code.max}")
    private Long personalityCodeMax;    // 성격 직접입력 코드
    @Value("${pet.health.code.max}")
    private Long healthCodeMax;         // 건강질환 직접입력 코드
    @Value("${pet.allergy.code.max}")
    private Long allergyCodeMax;        // 알러지 직접입력 코드
    @Value("${pet.personality.text.max}")
    private int personalityTextMax;     // 성격 직접입력 최대 글자수
    @Value("${pet.health.text.max}")
    private int healthTextMax;          // 건강질환 직접입력 최대 글자수
    @Value("${pet.allergy.text.max}")
    private int allergyTextMax;         // 알러지 직접입력 최대 글자수
    @Value("${pet.weight.max}")
    private int weightMax;              // 몸무게 최대값
    @Value("${pet.size.max}")
    private int sizeMax;                // 크기 최대값
    @Value("${pet.gender.max}")
    private int genderMax;              // 성별 최대값
    @Value("${pet.number.max}")
    private int numberMax;              // 동물등록번호 최대 자릿수
    @Value("${pet.breed.max}")
    private int breedIdxMax;            // 품종 idx 최대값
    @Value("${pet.age.max}")
    private int ageMax;                 // 연령 최대값
    @Value("${word.check.pet.name.type}")
    private int nameWordChk;            // 반려동물 이름 금칙어 타입
    @Value("${use.pet.register}")
    private boolean usePetRegister;    // 반려동물 등록 true/false
    @Value("${use.pet.modify}")
    private boolean usePetModify;      // 반려동물 수정 true/false
    @Value("${use.pet.delete}")
    private boolean usePetDelete;      // 반려동물 삭제 true/false

    /*****************************************************
     *  SELECT
     ****************************************************/
    /**
     * 강아지/고양이 품종 리스트
     *
     * @param searchDto memberUuid type
     * @return List<BreedDto>
     */
    public List<BreedDto> getBreedList(SearchDto searchDto) {

        // 회원 유효성 검사
        chkMemberUuid(searchDto.getMemberUuid());

        List<BreedDto> breedList = new ArrayList<>();

        // 품종 카운트
        int totalCnt = petDaoSub.getBreedTotalCnt(searchDto);

        if (totalCnt > 0) {
            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);
            // 품종 리스트
            breedList = petDaoSub.getBreedList(searchDto);
        }

        return breedList;
    }

    /**
     * 알러지 리스트
     *
     * @param searchDto memberUuid
     * @return List<AllergyDto>
     */
    public List<AllergyDto> getAllergyList(SearchDto searchDto) {

        // 회원 유효성 검사
        chkMemberUuid(searchDto.getMemberUuid());

        List<AllergyDto> list = new ArrayList<>();

        // 알러지 카운트
        int totalCnt = petDaoSub.getAllergyTotalCnt(searchDto);

        if (totalCnt > 0) {
            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);
            // 알러지 리스트
            list = petDaoSub.getAllergyList(searchDto);
        }

        return list;
    }

    /**
     * 건강질환 리스트
     *
     * @param searchDto memberUuid
     * @return List<HealthDto>
     */
    public List<HealthDto> getHealthList(SearchDto searchDto) {

        // 회원 유효성 검사
        chkMemberUuid(searchDto.getMemberUuid());

        List<HealthDto> healthList = new ArrayList<>();

        // 건강질환 카운트
        int totalCnt = petDaoSub.getHealthTotalCnt(searchDto);

        if (totalCnt > 0) {
            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);
            // 건강질환 리스트
            healthList = petDaoSub.getHealthList(searchDto);
        }

        return healthList;
    }

    /**
     * 성격 리스트
     *
     * @param searchDto memberUuid
     * @return List<PersonalityDto>
     */
    public List<PersonalityDto> getPersonalityList(SearchDto searchDto) {

        // 회원 유효성 검사
        chkMemberUuid(searchDto.getMemberUuid());

        List<PersonalityDto> personalityList = new ArrayList<>();

        // 성격 카운트
        int totalCnt = petDaoSub.getPersonalityTotalCnt(searchDto);

        if (totalCnt > 0) {
            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);
            // 성격 리스트
            personalityList = petDaoSub.getPersonalityList(searchDto);
        }

        return personalityList;
    }

    /*****************************************************
     *  Modules
     ****************************************************/
    /**
     * 반려동물 등록
     *
     * @param petDto memberUuid, name, number, type, gender, breedIdx,
     *               size, weight, age, birth, intro,
     *               personalityList, healthIdxList, allergyIdxList
     *               personalityEtc, healthEtc, allergyEtc
     * @return Integer
     */
    @Transactional
    public Integer registPet(PetDto petDto) {

        if (!usePetRegister) {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }

        // 회원 유효성 검사
        chkMemberUuid(petDto.getMemberUuid());

        // 유효성 검사
        registValidate(petDto);

        // 반려동물 uuid 세팅 ( locale Language + 랜덤 UUID + Timestamp )
        String localeLang = super.getLocaleLang();
        String setUuid = dateLibrary.getTimestamp();
        String uuid = "pet" + localeLang + UUID.randomUUID().toString().concat(setUuid);
        uuid = uuid.replace("-", "");

        // 고유아이디 중복체크
        Boolean bDupleUuid = checkDupleUuid(uuid);

        // 고유아이디가 중복이면 5번 재시도
        int retry = 0;
        while (Boolean.TRUE.equals(bDupleUuid) && retry < 5) {
            retry++;
            pushAlarm("반려동물 고유아이디 중복 시도::" + retry + "번째");

            // 콘텐츠 uuid 세팅 ( locale Language + 랜덤 UUID + Timestamp )
            localeLang = super.getLocaleLang();
            setUuid = dateLibrary.getTimestamp();
            uuid = "pet" + localeLang + UUID.randomUUID().toString().concat(setUuid);
            uuid = uuid.replace("-", "");

            bDupleUuid = checkDupleUuid(uuid);

            if (retry == 5) {
                throw new CustomException(CustomError.PET_UID_DUPLE); // 이미 존재하는 고유아이디입니다.
            }
        }

        // 고유아이디 set
        petDto.setPetUuid(uuid);

        // 등록일 set
        String datetime = dateLibrary.getDatetime();
        petDto.setRegDate(datetime);

        // 반려동물 등록
        Integer result = insertPet(petDto);

        // 회원 등록
        insertMemberPet(petDto);

        // 반려동물 정보 등록
        insertPetInfo(petDto);

        // 프로필 이미지 등록
        if (!ObjectUtils.isEmpty(petDto.getUploadFile())) {
            registerProfileImg(petDto, REGISTER);
        } else {
            // 프로필 이미지 빈값으로 초기화
            insertPetProfileImgInit(petDto);
        }

        // 프로필 소개글 등록
        registerProfileIntro(petDto, REGISTER);

        // 성격 정보 등록
        List<Long> personalityIdxList = petDto.getPersonalityIdxList();
        if (!ObjectUtils.isEmpty(personalityIdxList)) {
            List<PersonalityDto> personalityDtoList = new ArrayList<>();

            // 성격 idx 하나씩 세팅
            for (Long personalityIdx : personalityIdxList) {
                PersonalityDto personalityDto = PersonalityDto.builder()
                        .petUuid(petDto.getPetUuid())     // uuid
                        .idx(personalityIdx)           // personalityIdx
                        .regDate(datetime)             // 등록일
                        .build();

                personalityDtoList.add(personalityDto);

                // 직접 입력
                if (Objects.equals(personalityIdx, personalityCodeMax)) {
                    // 성격 기타 등록
                    insertPetPersonalityEtc(petDto);
                }
            }

            insertPetPersonality(personalityDtoList);
        }

        // 건강질환 정보 등록
        List<Long> healthIdxList = petDto.getHealthIdxList();
        if (!ObjectUtils.isEmpty(healthIdxList)) {
            List<HealthDto> healthDtoList = new ArrayList<>();

            // 건강질환 idx 하나씩 세팅
            for (Long healthIdx : healthIdxList) {
                HealthDto healthDto = HealthDto.builder()
                        .petUuid(petDto.getPetUuid())     // uuid
                        .idx(healthIdx)                // healthIdx
                        .regDate(datetime)             // 등록일
                        .build();

                healthDtoList.add(healthDto);

                // 직접 입력
                if (Objects.equals(healthIdx, healthCodeMax)) {
                    // 성격 기타 등록
                    insertPetHealthEtc(petDto);
                }
            }

            insertPetHealth(healthDtoList);
        }

        // 알러지 정보 등록
        List<Long> allergyIdxList = petDto.getAllergyIdxList();
        if (!ObjectUtils.isEmpty(allergyIdxList)) {
            List<AllergyDto> allergyDtoList = new ArrayList<>();

            // 알러지 idx 하나씩 세팅
            for (Long allergyIdx : allergyIdxList) {
                AllergyDto healthDto = AllergyDto.builder()
                        .petUuid(petDto.getPetUuid())   // uuid
                        .idx(allergyIdx)             // allergyIdx
                        .regDate(datetime)           // 등록일
                        .build();

                allergyDtoList.add(healthDto);

                // 직접 입력
                if (Objects.equals(allergyIdx, allergyCodeMax)) {
                    // 알러지 기타 등록
                    insertPetAllergyEtc(petDto);
                }
            }

            insertPetAllergy(allergyDtoList);
        }

        return result;
    }

    /**
     * 반려 동물 수정
     *
     * @param petDto petUuid, memberUuid, name, number, type, gender, breedIdx
     *               size, weight, age, birth, uploadFile, resetState
     *               personalityIdxList, allergyIdxList, healthIdxList,
     *               personalityEtc, allergyEtc, healthEtc
     */
    @Transactional
    public void modifyPet(PetDto petDto) {

        if (!usePetModify) {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }

        // 회원 유효성 검사
        chkMemberUuid(petDto.getMemberUuid());

        // 유효성 검사
        modifyValidate(petDto);

        String datetime = dateLibrary.getDatetime();
        petDto.setModiDate(datetime); // 수정일
        petDto.setRegDate(datetime);  // 등록일

        /** 반려 동물 [이름, 품종] 수정 **/
        updatePet(petDto);

        /** 반려 동물 기본 정보 수정 [number, gender, size, weight, age, birth] **/
        updatePetInfo(petDto);

        /** 이미지 수정 **/
        // 프로필 이미지 수정
        if (petDto.getResetState() == 0 && !ObjectUtils.isEmpty(petDto.getUploadFile())) {
            registerProfileImg(petDto, MODIFY);
        } // 기본 이미지 초기화
        else if (petDto.getResetState() == 1 && !ObjectUtils.isEmpty(petDto.getUploadFile())) {
            resetPetProfileImg(petDto.getPetUuid());
        }

        /** 소개글 수정 **/
        // 프로필 소개글 수정
        if (!ObjectUtils.isEmpty(petDto.getIntro())) {
            registerProfileIntro(petDto, MODIFY);
        }

        // insert, delete 용 petDto
        PetDto tmpPetDto = PetDto.builder()
                .petUuid(petDto.getPetUuid())          // petUuid
                .regDate(dateLibrary.getDatetime())    // 등록일
                .build();

        /** 성격 수정 **/
        List<Long> oldPersonalityIdxList = getPersonalityIdxList(petDto.getPetUuid()); // 기존 성격 리스트
        List<Long> tmpPersonalityIdxList = new ArrayList<>(oldPersonalityIdxList);     // 기존 성격 리스트 복사
        List<Long> newPersonalityIdxList = petDto.getPersonalityIdxList();             // 새로운 성격 리스트
        boolean personalityEtcUpdate = false;  // 직접입력 수정 필요 여부

        // 기존 성격 존재 & 새로운 성격 있는 경우
        if (!ObjectUtils.isEmpty(oldPersonalityIdxList) && !ObjectUtils.isEmpty(newPersonalityIdxList)) {
            // delete 할 성격 리스트 set
            for (Long idx : newPersonalityIdxList) {
                oldPersonalityIdxList.removeIf(n -> n.equals(idx));

                // 직접입력 여부
                if (Objects.equals(idx, personalityCodeMax)) {
                    personalityEtcUpdate = true;
                }
            }

            // insert 할 성격 리스트 set [위에서 기존 성격 목록 지우므로 복사한 리스트와 대조]
            for (Long idx : tmpPersonalityIdxList) {
                newPersonalityIdxList.removeIf(n -> n.equals(idx));
            }

            // 삭제할 성격 있으면 삭제
            if (!ObjectUtils.isEmpty(oldPersonalityIdxList)) {
                for (Long personalityIdx : oldPersonalityIdxList) {
                    tmpPetDto.setPersonalityIdx(personalityIdx);
                    deletePetPersonality(tmpPetDto);

                    // 기존 기타 삭제
                    if (Objects.equals(personalityIdx , personalityCodeMax)) {
                        deletePetPersonalityEtc(petDto.getPetUuid());
                    }

                }
            }
        } else if (!ObjectUtils.isEmpty(oldPersonalityIdxList) && ObjectUtils.isEmpty(newPersonalityIdxList)) { // 새로운 성격이 없는 경우
            for (Long personalityIdx : oldPersonalityIdxList) {
                tmpPetDto.setPersonalityIdx(personalityIdx);
                deletePetPersonality(tmpPetDto);
            }
        }

        // 등록할 성격 정보 있으면 등록
        if (!ObjectUtils.isEmpty(newPersonalityIdxList)) {
            List<Long> deletedPetPersonalityList = getDeletedPetPersonalityList(petDto.getPetUuid()); // 삭제된 성격 정보 조회
            // 삭제된 성격 정보 있는 경우 update
            if (!ObjectUtils.isEmpty(deletedPetPersonalityList)) {
                for (Long deletedIdx : deletedPetPersonalityList) {
                    for (int index = 0; index < newPersonalityIdxList.size(); index++) {
                        Long idx = newPersonalityIdxList.get(index);
                        // 수정 [기존에 삭제된 성격 정보]
                        if (idx.equals(deletedIdx)) {
                            tmpPetDto.setPersonalityIdx(idx);
                            updatePetPersonality(tmpPetDto);
                            newPersonalityIdxList.remove(index);
                            break;
                        }
                    }
                }
            }
        }

        // 신규 성격 정보 insert
        if (!ObjectUtils.isEmpty(newPersonalityIdxList)) {

            List<PersonalityDto> personalityDtoList = new ArrayList<>();

            for (Long idx : newPersonalityIdxList) {
                PersonalityDto personalityDto = PersonalityDto.builder()
                        .petUuid(petDto.getPetUuid())       // uuid
                        .idx(idx)                           // personalityIdx
                        .regDate(dateLibrary.getDatetime()) // 등록일
                        .build();

                personalityDtoList.add(personalityDto);
            }

            // 성격 등록
            insertPetPersonality(personalityDtoList);

        }

        // 직접 입력 수정
        if (Boolean.TRUE.equals(personalityEtcUpdate)) {
            // 기존 기타 성격 조회
            String oldPersonalityEtc = getPersonalityEtc(petDto.getPetUuid());

            // 기존 직접 입력 성격 있으면
            if (!ObjectUtils.isEmpty(oldPersonalityEtc)) {
                oldPersonalityEtc = oldPersonalityEtc.trim(); // 공백 제거
                String newEtcName = petDto.getPersonalityEtc().trim(); // 새로 입력된 기타 성격
                // 기존 성격과 같지 않으면
                if (!oldPersonalityEtc.equals(newEtcName)) {
                    deletePetPersonalityEtc(petDto.getPetUuid()); // 기존 기타 성격 삭제
                    insertPetPersonalityEtc(petDto); // 기타 성격 등록
                }
            } else { // 기존 직접 입력 성격 없으면
                insertPetPersonalityEtc(petDto); // 기타 성격 등록
            }
        }

        /** 건강 질환 수정 **/
        List<Long> oldHealthIdxList = getHealthIdxList(petDto.getPetUuid()); // 기존 건강 질환 리스트
        List<Long> tmpHealthIdxList = new ArrayList<>(oldHealthIdxList);     // 기존 건강 질환 리스트 복사
        List<Long> newHealthIdxList = petDto.getHealthIdxList();             // 새로운 건강 질환 리스트
        boolean healthEtcUpdate = false;  // 직접입력 수정 필요 여부

        // 기존 건강 질환 존재 & 새로운 건강 질환 있는 경우
        if (!ObjectUtils.isEmpty(oldHealthIdxList) && !ObjectUtils.isEmpty(newHealthIdxList)) {
            // delete 할 건강 질환 리스트 set
            for (Long idx : newHealthIdxList) {
                oldHealthIdxList.removeIf(n -> n.equals(idx));

                // 직접입력 여부
                if (Objects.equals(idx, healthCodeMax)) {
                    healthEtcUpdate = true;
                }
            }

            // insert 할 건강 질환 리스트 set [위에서 기존 건강 질환 목록 지우므로 복사한 리스트와 대조]
            for (Long idx : tmpHealthIdxList) {
                newHealthIdxList.removeIf(n -> n.equals(idx));
            }

            // 삭제할 건강 질환 있으면 삭제
            if (!ObjectUtils.isEmpty(oldHealthIdxList)) {
                for (Long healthIdx : oldHealthIdxList) {
                    tmpPetDto.setHealthIdx(healthIdx);
                    deletePetHealth(tmpPetDto);

                    // 기존 기타 삭제
                    if (Objects.equals(healthIdx , healthCodeMax)) {
                        deletePetHealthEtc(petDto.getPetUuid());
                    }
                }
            }
        } else if (!ObjectUtils.isEmpty(oldHealthIdxList) && ObjectUtils.isEmpty(newHealthIdxList)) { // 새로운 건강 질환이 없는 경우
            for (Long healthIdx : oldHealthIdxList) {
                tmpPetDto.setHealthIdx(healthIdx);
                deletePetHealth(tmpPetDto);
            }
        }

        // 등록할 건강 질환 정보 있으면 등록
        if (!ObjectUtils.isEmpty(newHealthIdxList)) {
            List<Long> deletedPetHealthList = getDeletedPetHealthList(petDto.getPetUuid()); // 삭제된 건강 정보 조회
            // 삭제된 건강 정보 있는 경우 update
            if (!ObjectUtils.isEmpty(deletedPetHealthList)) {
                for (Long deletedIdx : deletedPetHealthList) {
                    for (int index = 0; index < newHealthIdxList.size(); index++) {
                        Long idx = newHealthIdxList.get(index);
                        // 수정 [기존에 삭제된 건강 정보]
                        if (idx.equals(deletedIdx)) {
                            tmpPetDto.setHealthIdx(idx);
                            modifyPetHealth(tmpPetDto);
                            newHealthIdxList.remove(index);
                            break;
                        }
                    }
                }
            }
        }

        // 신규 건강 질환 정보 insert
        if (!ObjectUtils.isEmpty(newHealthIdxList)) {

            List<HealthDto> healthDtoList = new ArrayList<>();

            for (Long idx : newHealthIdxList) {
                HealthDto healthDto = HealthDto.builder()
                        .petUuid(petDto.getPetUuid())       // uuid
                        .idx(idx)                           // healthIdx
                        .regDate(dateLibrary.getDatetime()) // 등록일
                        .build();

                healthDtoList.add(healthDto);
            }

            // 건강 질환 등록
            insertPetHealth(healthDtoList);

        }

        // 건강 질환 직접 입력 수정
        if (Boolean.TRUE.equals(healthEtcUpdate)) {
            // 기존 기타 건강 질환 조회
            String oldHealthEtc = getHealthEtc(petDto.getPetUuid());

            // 기존 직접 입력 건강 질환 있으면
            if (!ObjectUtils.isEmpty(oldHealthEtc)) {
                oldHealthEtc = oldHealthEtc.trim(); // 공백 제거
                String newEtcName = petDto.getHealthEtc().trim(); // 새로 입력된 기타 건강 질환
                // 기존 건강 질환과 같지 않으면
                if (!oldHealthEtc.equals(newEtcName)) {
                    deletePetHealthEtc(petDto.getPetUuid()); // 기존 기타 건강 질환 삭제
                    insertPetHealthEtc(petDto); // 기타 건강 질환 등록
                }
            } else { // 기존 직접 입력 건강 질환 없으면
                insertPetHealthEtc(petDto); // 기타 건강 질환 등록
            }
        }

        /** 알러지 정보 수정 **/
        List<Long> oldAllergyIdxList = getAllergyIdxList(petDto.getPetUuid()); // 기존 알러지 리스트
        List<Long> tmpAllergyIdxList = new ArrayList<>(oldAllergyIdxList);     // 기존 알러지 리스트 복사
        List<Long> newAllergyIdxList = petDto.getAllergyIdxList();             // 새로운 알리지 리스트
        boolean allergyEtcUpdate = false;  // 직접입력 수정 필요 여부

        // 등록된 알러지 정보 있고, 새로운 알러지 정보 있는 경우
        if (!ObjectUtils.isEmpty(oldAllergyIdxList) && !ObjectUtils.isEmpty(newAllergyIdxList)) {

            // delete 해야할 알러지 리스트 set
            for (Long idx : newAllergyIdxList) {
                oldAllergyIdxList.removeIf(n -> n.equals(idx)); // 삭제할 리스트

                // 직접입력 여부
                if (Objects.equals(idx, allergyCodeMax)) {
                    allergyEtcUpdate = true;
                }
            }

            // insert 해야할 알러지 리스트 set
            for (Long idx : tmpAllergyIdxList) { // [위에서 기존 알러지 목록 지우므로 복사한 리스트와 대조]
                newAllergyIdxList.removeIf(n -> n.equals(idx)); // 새로 등록 할 리스트
            }

            // 삭제할 알러지 있으면 삭제
            if (!ObjectUtils.isEmpty(oldAllergyIdxList)) {
                for (Long allergyIdx : oldAllergyIdxList) {
                    tmpPetDto.setAllergyIdx(allergyIdx);
                    deletePetAllergy(tmpPetDto);

                    // 기존 기타 삭제
                    if (Objects.equals(allergyIdx , allergyCodeMax)) {
                        deletePetAllergyEtc(petDto.getPetUuid());
                    }
                }
            }
        } else if (!ObjectUtils.isEmpty(oldAllergyIdxList) && ObjectUtils.isEmpty(newAllergyIdxList)) { // 새로운 알러지 정보 없는 경우
            for (Long allergyIdx : oldAllergyIdxList) {
                tmpPetDto.setAllergyIdx(allergyIdx);
                deletePetAllergy(tmpPetDto);
            }
        }

        // 등록할 알러지 정보 있으면 등록
        if (!ObjectUtils.isEmpty(newAllergyIdxList)) {
            List<Long> deletedPetAllergyList = getDeletedPetAllergyList(petDto.getPetUuid()); // 삭제된 건강 정보 조회

            if (!ObjectUtils.isEmpty(deletedPetAllergyList)) {
                for (Long deletedIdx : deletedPetAllergyList) {
                    for (int index = 0; index < newAllergyIdxList.size(); index++) {
                        Long idx = newAllergyIdxList.get(index);
                        // 수정 [기존에 삭제된 알러지 정보]
                        if (idx.equals(deletedIdx)) {
                            tmpPetDto.setAllergyIdx(idx);
                            modifyPetAllergy(tmpPetDto);
                            newAllergyIdxList.remove(index);
                            break;
                        }
                    }
                }
            }
        }

        // 신규 알러지 정보 insert
        if (!ObjectUtils.isEmpty(newAllergyIdxList)) {

            List<AllergyDto> allergyDtoList = new ArrayList<>();

            for (Long idx : newAllergyIdxList) {
                AllergyDto allergyDto = AllergyDto.builder()
                        .petUuid(petDto.getPetUuid())          // uuid
                        .idx(idx)                           // allergyIdx
                        .regDate(dateLibrary.getDatetime()) // 등록일
                        .build();

                allergyDtoList.add(allergyDto);
            }

            // 알러지 등록
            insertPetAllergy(allergyDtoList);
        }

        // 알러지 직접 입력 수정
        if (Boolean.TRUE.equals(allergyEtcUpdate)) {

            // 기존 기타 건강 질환 조회
            String oldAllergyEtc = getAllergyEtc(petDto.getPetUuid());

            // 기존 직접 입력 알러지 있으면
            if (!ObjectUtils.isEmpty(oldAllergyEtc)) {
                oldAllergyEtc = oldAllergyEtc.trim(); // 공백 제거
                String newEtcName = petDto.getHealthEtc().trim(); // 새로 입력된 기타 알러지
                // 기존 알러지와 같지 않으면
                if (!oldAllergyEtc.equals(newEtcName)) {
                    deletePetAllergyEtc(petDto.getPetUuid()); // 기존 기타 알러지 삭제
                    insertPetAllergyEtc(petDto); // 기타 알러지 등록
                }
            } else { // 기존 직접 입력 알러지 없으면
                insertPetAllergyEtc(petDto); // 기타 알러지 등록
            }

        }

    }

    /**
     * 반려 동물 삭제
     *
     * @param petDto petUuid memberUuid
     */
    public void deletePet(PetDto petDto) {

        if (!usePetDelete) {
            throw new CustomException(CustomError.SWITCH_FALSE_ERROR); // 이용 불가한 기능입니다.
        }

        // 회원 유효성 검사
        chkMemberUuid(petDto.getMemberUuid());

        // 유효성 검사
        deletePetValidate(petDto);

        // 반려동물 삭제
        int result = petDao.deletePet(petDto);

        if (result < 1) {
            throw new CustomException(CustomError.PET_DELETE_FAIL); // 반려동물 삭제에 실패하였습니다.
        }
    }

    /*****************************************************
     *  SubFunction - Select
     ****************************************************/
    /**
     * 반려동물 고유 아이디 중복 검색
     *
     * @param uuid 고유아이디
     * @return 중복여부 [중복 : true]
     */
    public Boolean checkDupleUuid(String uuid) {
        int iCount = petDaoSub.getCountByUuid(uuid);

        return iCount > 0;
    }

    /**
     * 품종 idx 검색
     *
     * @param idx 품종 idx (pet_breed)
     * @return 중복여부 [중복 : true]
     */
    public Boolean checkBreedIdx(Long idx) {
        Integer iCount = petDaoSub.getCountByBreedIdx(idx);

        return iCount > 0;
    }

    /**
     * 성격 idx 검색
     *
     * @param idx 성격 idx
     * @return 중복여부 [중복 : true]
     */
    public Boolean checkPersonalityIdx(Long idx) {
        Integer iCount = petDaoSub.getCountByPersonalityIdx(idx);

        return iCount > 0;
    }

    /**
     * 건강질환 idx 검색
     *
     * @param idx 건강질환 idx
     * @return 중복여부 [중복 : true]
     */
    public Boolean checkHealthIdx(Long idx) {
        Integer iCount = petDaoSub.getCountByHealthIdx(idx);

        return iCount > 0;
    }

    /**
     * 알러지 idx 검색
     *
     * @param idx 알러지 idx
     * @return 중복여부 [중복 : true]
     */
    public Boolean checkAllergyIdx(Long idx) {
        Integer iCount = petDaoSub.getCountByAllergyIdx(idx);

        return iCount > 0;
    }

    /**
     * 반려동물 프로필 이미지 고유 아이디 중복 검색
     *
     * @param uuid 고유아이디
     * @return 중복여부 [중복 : true]
     */
    public Boolean checkDupleImgUuid(String uuid) {
        Integer iCount = petDaoSub.getCountByImgUuid(uuid);

        return iCount > 0;
    }

    /**
     * 나의 반려동물인지 여부 조회
     *
     * @param petDto petUuid memberUuid
     * @return
     */
    public Boolean checkMyPet(PetDto petDto) {
        int iCount = petDaoSub.getMemberPetCnt(petDto);

        return iCount > 0;
    }

    /**
     * 기존 성격 idx 리스트 조회
     *
     * @param petUuid
     * @return
     */
    private List<Long> getPersonalityIdxList(String petUuid) {
        return petDaoSub.getPersonalityIdxList(petUuid);
    }

    /**
     * 기존 건강 질환 idx 리스트 조회
     *
     * @param petUuid
     * @return
     */
    private List<Long> getHealthIdxList(String petUuid) {
        return petDaoSub.getHealthIdxList(petUuid);
    }

    /**
     * 기존 알러지 idx 리스트 조회
     *
     * @param petUuid
     * @return
     */
    private List<Long> getAllergyIdxList(String petUuid) {
        return petDaoSub.getAllergyIdxList(petUuid);
    }

    /**
     * 삭제된 성격 type_idx 조회
     *
     * @param petUuid
     * @return
     */
    private List<Long> getDeletedPetPersonalityList(String petUuid) {
        return petDaoSub.getDeletedPetPersonalityList(petUuid);
    }

    /**
     * 삭제된 건강 type_idx 조회
     *
     * @param petUuid
     * @return
     */
    private List<Long> getDeletedPetHealthList(String petUuid) {
        return petDaoSub.getDeletedPetHealthList(petUuid);
    }

    /**
     * 삭제된 알러지 type_idx 조회
     *
     * @param petUuid
     * @return
     */
    private List<Long> getDeletedPetAllergyList(String petUuid) {
        return petDaoSub.getDeletedPetAllergyList(petUuid);
    }

    /**
     * 기타 성격 조회
     *
     * @param petUuid
     * @return
     */
    private String getPersonalityEtc(String petUuid) {
        return petDaoSub.getPersonalityEtc(petUuid);
    }

    /**
     * 기타 건강 질환 조회
     *
     * @param petUuid
     * @return
     */
    private String getHealthEtc(String petUuid) {
        return petDaoSub.getHealthEtc(petUuid);
    }

    /**
     * 기타 알러지 조회
     *
     * @param petUuid
     * @return
     */
    private String getAllergyEtc(String petUuid) {
        return petDaoSub.getAllergyEtc(petUuid);
    }

    /*****************************************************
     *  SubFunction - Insert
     ****************************************************/
    /**
     * 반려동물 등록
     *
     * @param petDto uuid, name, type, breedIdx, regDate
     */
    public Integer insertPet(PetDto petDto) {
        Integer iResult = petDao.insertPet(petDto);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_REGISTER_FAIL); // 반려동물 등록에 실패하였습니다.
        }
        return iResult;
    }

    /**
     * 회원 매핑 등록
     *
     * @param petDto memberIdx, insertedIdx, regDate
     */
    public void insertMemberPet(PetDto petDto) {
        Integer iResult = petDao.insertMemberPet(petDto);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_MEMBER_REGISTER_FAIL); // 반려동물 매핑 등록에 실패하였습니다.
        }
    }

    /**
     * 반려동물 정보 등록
     *
     * @param petDto insertedIdx, number, gender, size, weight, birth, personalityIdx, regDate
     */
    public void insertPetInfo(PetDto petDto) {
        Integer iResult = petDao.insertPetInfo(petDto);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_INFO_REGISTER_FAIL); // 반려동물 정보 등록에 실패하였습니다.
        }
    }

    /**
     * 성격 기타 등록
     *
     * @param petDto uuid, personalityEtc, regDate
     */
    public void insertPetPersonalityEtc(PetDto petDto) {

        Integer iResult = petDao.insertPetPersonalityEtc(petDto);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_PERSONALITY_ETC_REGISTER_FAIL); // 성격 기타 등록에 실패하였습니다.
        }
    }

    /**
     * 건강질환 기타 등록
     *
     * @param petDto uuid, personalityEtc, regDate
     */
    public void insertPetHealthEtc(PetDto petDto) {

        Integer iResult = petDao.insertPetHealthEtc(petDto);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_HEALTH_ETC_REGISTER_FAIL); // 성격 기타 등록에 실패하였습니다.
        }
    }

    /**
     * 알러지 기타 등록
     *
     * @param petDto uuid, personalityEtc, regDate
     */
    public void insertPetAllergyEtc(PetDto petDto) {

        Integer iResult = petDao.insertPetAllergyEtc(petDto);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_ALLERGY_ETC_REGISTER_FAIL); // 성격 기타 등록에 실패하였습니다.
        }
    }

    /**
     * 프로필 이미지 초기화
     *
     * @param petDto insertedIdx, regDate
     */
    public void insertPetProfileImgInit(PetDto petDto) {

        // 이미지 고유아이디 세팅
        petDto.setImgUuid(getUuid());
        Integer iResult = petDao.insertPetProfileImgInit(petDto);

        if (iResult < 1) {
            throw new CustomException(CustomError.PET_PROFILE_IMG_REGISTER_FAIL); // 프로필 이미지 등록 실패하였습니다.
        }

    }

    /**
     * 프로필 이미지 등록
     *
     * @param uploadResponse uuid, petUuid, fileUrl, uploadName, uploadPath, imgWidth, imgHeight, sort, regDate
     */
    public void insertPetProfileImg(HashMap<String, Object> uploadResponse) {
        Integer iResult = petDao.insertPetProfileImg(uploadResponse);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_PROFILE_IMG_REGISTER_FAIL); // 프로필 이미지 등록 실패하였습니다.
        }
    }

    /**
     * 프로필 이미지 로그 등록
     *
     * @param uploadResponse uuid, petUuid, fileUrl, uploadName, uploadPath, imgWidth, imgHeight, sort, regDate
     */
    public void insertPetProfileImgLog(HashMap<String, Object> uploadResponse) {
        Integer iResult = petDao.insertPetProfileImgLog(uploadResponse);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_PROFILE_IMG_LOG_REGISTER_FAIL); // 프로필 이미지 로그 등록 실패하였습니다.
        }
    }

    /**
     * 프로필 소개글 등록
     *
     * @param petDto petUuid intro
     */
    public void insertPetProfileIntro(PetDto petDto) {
        Integer iResult = petDao.insertPetProfileIntro(petDto);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_PROFILE_INTRO_REGISTER_FAIL); // 프로필 소개글 등록 실패하였습니다.
        }
    }

    /**
     * 프로필 소개글 로그 등록
     *
     * @param petDto petUuid intro
     */
    public void insertPetProfileIntroLog(PetDto petDto) {
        Integer iResult = petDao.insertPetProfileIntroLog(petDto);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_PROFILE_INTRO_LOG_REGISTER_FAIL); // 프로필 소개글 로그 등록 실패하였습니다.
        }
    }

    /**
     * 성격 등록
     *
     * @param personalityList uuid, personalityIdx, regDate
     */
    public void insertPetPersonality(List<PersonalityDto> personalityList) {
        Integer iResult = petDao.insertPetPersonality(personalityList);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_PERSONALITY_REGISTER_FAIL); // 성격 등록에 실패하였습니다.
        }
    }

    /**
     * 건강질환 등록
     *
     * @param healthList uuid, healthIdx, regDate
     */
    public void insertPetHealth(List<HealthDto> healthList) {
        Integer iResult = petDao.insertPetHealth(healthList);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_HEALTH_REGISTER_FAIL); // 건강질환 등록에 실패하였습니다.
        }
    }

    /**
     * 알러지 등록
     *
     * @param allergyList uuid, allergyIdx, regDate
     */
    public void insertPetAllergy(List<AllergyDto> allergyList) {
        Integer iResult = petDao.insertPetAllergy(allergyList);
        if (iResult < 1) {
            throw new CustomException(CustomError.PET_ALLERGY_REGISTER_FAIL); // 알러지 등록에 실패하였습니다.
        }
    }

    /**
     * 반려동물 프로필 이미지 수정
     *
     * @param uploadResponse : uuid, petIdx, url, uploadName, uploadPath, imgWidth, imgHeight, sort, regDate
     */
    private void updatePetProfileImg(Map<String, Object> uploadResponse) {
        int result = petDao.updatePetProfileImg(uploadResponse);

        if (result < 1) {
            throw new CustomException(CustomError.PET_PROFILE_IMG_UPDATE_FAIL); // 반려동물 프로필 이미지 수정에 실패하였습니다.
        }
    }

    /**
     * 반려동물 프로필 소개글 수정
     *
     * @param petDto petUuid intro
     */
    private void updatePetProfileIntro(PetDto petDto) {
        int result = petDao.updatePetProfileIntro(petDto);

        if (result < 1) {
            throw new CustomException(CustomError.PET_PROFILE_INTRO_UPDATE_FAIL); // 반려동물 프로필 소개글 수정에 실패하였습니다.
        }
    }

    /**
     * 이미지 등록
     *
     * @param petDto : petUuid, uploadFile
     * @param type   : 등록[REGISTER], 수정[MODIFY]
     */
    private void registerProfileImg(PetDto petDto, String type) {
        // 업로드할 이미지 유효성
        s3Library.checkUploadFiles(petDto.getUploadFile());

        // 업로드 이미지
        List<MultipartFile> uploadFile = petDto.getUploadFile();

        // s3에 저장될 path
        String s3Path = "pet/" + petDto.getPetUuid();

        // s3 upload (원본)
        List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(uploadFile, s3Path);

        // DB 저장할 정보 세팅
        HashMap<String, Object> fileMap = setFileToDB(petDto.getPetUuid(), s3Path, uploadResponse);

        // 고유아이디 set
        fileMap.put("imgUuid", getUuid());

        // 등록
        if (type.equals(REGISTER)) {
            // 프로필 이미지 등록
            insertPetProfileImg(fileMap);
        } // 수정
        else if (type.equals(MODIFY)) {
            updatePetProfileImg(fileMap);
        }
        // 프로필 이미지 로그 등록
        insertPetProfileImgLog(fileMap);
    }

    /**
     * 소개글 등록
     *
     * @param petDto : petUuid, intro
     * @param type   : 등록[REGISTER], 수정[MODIFY]
     */
    private void registerProfileIntro(PetDto petDto, String type) {

        // 등록
        if (type.equals(REGISTER)) {
            // 등록 시 빈값이면 초기화
            if (ObjectUtils.isEmpty(petDto.getIntro())) {
                petDto.setIntro("");
            }
            // 프로필 소개글 등록
            insertPetProfileIntro(petDto);
        } // 수정
        else if (type.equals(MODIFY)) {
            updatePetProfileIntro(petDto);
        }
        // 프로필 소개글 로그 등록
        insertPetProfileIntroLog(petDto);
    }

    /*****************************************************
     *  SubFunction - Update
     ****************************************************/
    /**
     * 반려동물 수정
     *
     * @param petDto name, breedIdx
     */
    private void updatePet(PetDto petDto) {
        int iResult = petDao.updatePet(petDto);

        if (iResult < 1) {
            throw new CustomException(CustomError.PET_UPDATE_FAIL); // 반려동물 수정에 실패하였습니다.
        }

    }

    /**
     * 반려동물 정보 수정
     *
     * @param petDto insertedIdx, number, gender, size, weight, birth, personalityIdx, regDate
     */
    private void updatePetInfo(PetDto petDto) {
        int iResult = petDao.updatePetInfo(petDto);

        if (iResult < 1) {
            throw new CustomException(CustomError.PET_UPDATE_FAIL); // 반려동물 수정에 실패하였습니다.
        }
    }

    /**
     * 반려동물 성격 정보 수정
     *
     * @param petDto
     */
    private void updatePetPersonality(PetDto petDto) {
        int result = petDao.updatePetPersonalityInfo(petDto);

        if (result < 1) {
            throw new CustomException(CustomError.PET_UPDATE_FAIL);
        }
    }

    /**
     * 반려동물 건강 정보 수정
     *
     * @param petDto
     */
    private void modifyPetHealth(PetDto petDto) {
        int result = petDao.updatePetHealthInfo(petDto);

        if (result < 1) {
            throw new CustomException(CustomError.PET_UPDATE_FAIL);
        }
    }

    /**
     * 반려동물 알러지 정보 수정
     *
     * @param petDto
     */
    private void modifyPetAllergy(PetDto petDto) {
        int result = petDao.updatePetAllergyInfo(petDto);

        if (result < 1) {
            throw new CustomException(CustomError.PET_UPDATE_FAIL);
        }
    }

    /**
     * 프로필 이미지 초기화 [pet_profile_img, pet_profile_img_log]
     *
     * @param petUuid
     */
    private void resetPetProfileImg(String petUuid) {

        // DB 저장할 정보 세팅
        HashMap<String, Object> fileMap = new HashMap<>();
        fileMap.put("petUuid", petUuid);
        fileMap.put("fileUrl", "");
        fileMap.put("orgFileName", "");
        fileMap.put("uploadPath", "");
        fileMap.put("imgWidth", 0);
        fileMap.put("imgHeight", 0);
        fileMap.put("regDate", dateLibrary.getDatetime());
        fileMap.put("imgUuid", getUuid());

        // 이미지 update [등록할 때 기본 이미지 등록 됨]
        updatePetProfileImg(fileMap);
        // 로그 insert
        insertPetProfileImgLog(fileMap);
    }

    /*****************************************************
     *  SubFunction - Delete
     ****************************************************/
    /**
     * 성격 삭제
     *
     * @param petDto idx, personalityIdx
     */
    private void deletePetPersonality(PetDto petDto) {
        int iResult = petDao.deletePetPersonality(petDto);

        if (iResult < 1) {
            throw new CustomException(CustomError.PET_PERSONALITY_DELETE_FAIL); // 성격 삭제에 실패하였습니다.
        }
    }

    /**
     * 건강 질환 삭제
     *
     * @param petDto idx, healthIdx
     */
    private void deletePetHealth(PetDto petDto) {
        int iResult = petDao.deletePetHealth(petDto);

        if (iResult < 1) {
            throw new CustomException(CustomError.PET_HEALTH_DELETE_FAIL); // 건강 질환 삭제에 실패하였습니다.
        }
    }

    /**
     * 알러지 삭제
     *
     * @param petDto idx, healthIdx
     */
    private void deletePetAllergy(PetDto petDto) {
        int iResult = petDao.deletePetAllergy(petDto);

        if (iResult < 1) {
            throw new CustomException(CustomError.PET_ALLERGY_DELETE_FAIL); // 알러지 삭제에 실패하였습니다.
        }
    }

    /**
     * 기타 성격 삭제
     *
     * @param petUuid
     */
    private void deletePetPersonalityEtc(String petUuid) {
        int result = petDao.deletePetPersonalityEtc(petUuid);

        if (result < 1) {
            throw new CustomException(CustomError.PET_PERSONALITY_UPDATE_FAIL); // 성격 기타 수정에 실패하였습니다.
        }
    }

    /**
     * 기타 건강질환 삭제
     *
     * @param petUuid
     */
    private void deletePetHealthEtc(String petUuid) {
        int result = petDao.deletePetHealthEtc(petUuid);

        if (result < 1) {
            throw new CustomException(CustomError.PET_HEALTH_UPDATE_FAIL); // 성격 기타 수정에 실패하였습니다.
        }
    }

    /**
     * 기타 알러지 삭제
     *
     * @param petUuid
     */
    private void deletePetAllergyEtc(String petUuid) {
        int result = petDao.deletePetAllergyEtc(petUuid);

        if (result < 1) {
            throw new CustomException(CustomError.PET_ALLERGY_UPDATE_FAIL); // 성격 기타 수정에 실패하였습니다.
        }
    }

    /*****************************************************
     *  SubFunction - etc
     ****************************************************/
    /**
     * 반려동물 프로필 이미지 DB 저장할 정보 세팅
     *
     * @param petUuid        pet.uuid
     * @param s3Path         S3 경로
     * @param uploadResponse 업로드 이미지
     * @return hmImage
     */
    public HashMap<String, Object> setFileToDB(String petUuid, String s3Path, List<HashMap<String, Object>> uploadResponse) {

        HashMap<String, Object> hmImage = uploadResponse.get(0);

        hmImage.put("petUuid", petUuid);
        hmImage.put("uploadPath", s3Path);
        hmImage.put("sort", 1);
        hmImage.put("regDate", dateLibrary.getDatetime());

        return hmImage;
    }

    /**
     * 반려동물 프로필 이미지 UUID 생성
     */
    public String getUuid() {
        // 반려동물 프로필 이미지 uuid 세팅 ( locale Language + 랜덤 UUID + Timestamp )
        String localeLang = super.getLocaleLang();
        String setUuid = dateLibrary.getTimestamp();
        String uuid = "pet_profile_img_" + localeLang + UUID.randomUUID().toString().concat(setUuid);
        uuid = uuid.replace("-", "");

        // 고유아이디 중복체크
        Boolean bDupleUuid = checkDupleImgUuid(uuid);

        // 고유아이디가 중복이면 5번 재시도
        int retry = 0;
        while (Boolean.TRUE.equals(bDupleUuid) && retry < 5) {
            retry++;
            pushAlarm("프로필 이미지 고유아이디 중복 시도::" + retry + "번째");

            // 반려동물 프로필 이미지 uuid 세팅 ( locale Language + 랜덤 UUID + Timestamp )
            localeLang = super.getLocaleLang();
            setUuid = dateLibrary.getTimestamp();
            uuid = "pet_profile_img_" + localeLang + UUID.randomUUID().toString().concat(setUuid);
            uuid = uuid.replace("-", "");

            bDupleUuid = checkDupleImgUuid(uuid);

            if (retry == 5) {
                throw new CustomException(CustomError.PET_UID_DUPLE);
            }
        }

        return uuid;
    }

    /*****************************************************************
     * Validation
     *****************************************************************/
    /**
     * 반려동물 등록 유효성 검사
     *
     * @param petDto name, number, gender, breedIdx, breedNameEtc, size,
     *               weight, birth, personalityCode, personalityEtc,
     *               healthIdxList, allergyIdxList
     */
    @SneakyThrows
    private void registValidate(PetDto petDto) {

        /** name */
        if (ObjectUtils.isEmpty(petDto.getName())) { // 이름 null
            throw new CustomException(CustomError.PET_NAME_NULL);
        }
        // 최대 글자수 초과
        if (petDto.getName().length() > nameTextMax) {
            throw new CustomException(CustomError.PET_NAME_TEXT_LIMIT_OVER);
        }
        // 숫자/한글/영어/언더바만 입력 가능
        if (!Pattern.matches("^[0-9a-zA-Zㄱ-ㅎ가-힣_]*$", petDto.getName())) {
            throw new CustomException(CustomError.PET_NAME_STRING_ERROR);
        }
        // 외계어 검사
        String nameCheck = new String(petDto.getName().getBytes("euc-kr"), "euc-kr");
        if (!petDto.getName().equals(nameCheck)) {
            throw new CustomException(CustomError.PET_NAME_STRING_ERROR);
        }
        // 이름 금칙어 체크
        petWordcheckService.petWordCheck(petDto.getName(), nameWordChk);

        /** number */
        if (petDto.getNumber() != null) {
            if (petDto.getNumber().toString().length() > numberMax) { // 최대 숫자 자리 초과
                throw new CustomException(CustomError.PET_NUMBER_LIMIT_OVER);
            }
        }

        /** breed **/
        if (petDto.getBreedIdx() == null) { // 품종 null
            throw new CustomException(CustomError.PET_BREED_IDX_NULL);
        }
        if (petDto.getBreedIdx() < 1L || petDto.getBreedIdx() > breedIdxMax) { // 선택 가능 idx 초과
            throw new CustomException(CustomError.PET_BREED_IDX_ERROR);
        }
        // 품종 idx DB 체크
        Boolean bValidBreedIdx = checkBreedIdx(petDto.getBreedIdx());
        if (Boolean.FALSE.equals(bValidBreedIdx)) { // 유효하지 않은 품종 idx
            throw new CustomException(CustomError.PET_BREED_IDX_ERROR);
        }

        /** gender **/
        if (ObjectUtils.isEmpty(petDto.getGender())) { // 성별 null
            throw new CustomException(CustomError.PET_GENDER_NULL);
        }
        if (petDto.getGender() < 1 || petDto.getGender() > genderMax) { // 입력 가능 숫자 초과
            throw new CustomException(CustomError.PET_GENDER_ERROR);
        }

        /** size **/
        if (petDto.getSize() == null) { // 크키 null
            throw new CustomException(CustomError.PET_SIZE_NULL);
        }
        if (petDto.getSize() < 1 || petDto.getSize() > sizeMax) { // 입력 가능 숫자 초과
            throw new CustomException(CustomError.PET_SIZE_ERROR);
        }

        /** weight **/
        if (petDto.getWeight() == null) { // 몸무게 null
            throw new CustomException(CustomError.PET_WEIGHT_NULL);
        }
        if (petDto.getWeight() < 0 || petDto.getWeight() > weightMax) { // 입력 가능 수 초과
            throw new CustomException(CustomError.PET_WEIGHT_ERROR);
        }

        /** age **/
        if (petDto.getAge() == null) { // 연령 null
            throw new CustomException(CustomError.PET_AGE_NULL);
        }
        if (petDto.getAge() < 1 || petDto.getAge() > ageMax) {
            throw new CustomException(CustomError.PET_AGE_ERROR); // 선택 가능 수 초과
        }

        /** birth **/
        if (ObjectUtils.isEmpty(petDto.getBirth())) { // 생년월일 null
            throw new CustomException(CustomError.PET_BIRTH_NULL);
        }
        // yyyymmdd 형식만 입력 가능
        if (!Pattern.matches("[0-9]{4}[0-9]{2}[0-9]{2}", petDto.getBirth())) {
            throw new CustomException(CustomError.PET_BIRTH_STRING_ERROR);
        }

        /** personality **/
        List<Long> personalityIdxList = petDto.getPersonalityIdxList();
        if (ObjectUtils.isEmpty(personalityIdxList)) { // 성격 미선택
            throw new CustomException(CustomError.PET_PERSONALITY_IDX_NULL);
        }
        for (Long idx : personalityIdxList) {
            // 성격 idx DB 체크
            Boolean bValidPersonalityIdx = checkPersonalityIdx(idx);
            if (Boolean.FALSE.equals(bValidPersonalityIdx)) { // 유효하지 않은 성격 idx
                throw new CustomException(CustomError.PET_PERSONALITY_IDX_ERROR);
            }
            // 직접입력
            if (Objects.equals(idx, personalityCodeMax)) {
                if (ObjectUtils.isEmpty(petDto.getPersonalityEtc())) {
                    throw new CustomException(CustomError.PET_PERSONALITY_ETC_NULL);
                }
                if (petDto.getPersonalityEtc().length() > personalityTextMax) { // 최대 입력 가능 글자수 초과
                    throw new CustomException(CustomError.PET_PERSONALITY_TEXT_LIMIT_OVER);
                }
            }
        }

        /** health **/
        List<Long> healthIdxList = petDto.getHealthIdxList();
        if (ObjectUtils.isEmpty(healthIdxList)) { // 건강질환 미선택
            throw new CustomException(CustomError.PET_HEALTH_IDX_NULL);
        }
        for (Long idx : healthIdxList) {
            // 건강질환 idx DB 체크
            Boolean bValidHealthIdx = checkHealthIdx(idx);
            if (Boolean.FALSE.equals(bValidHealthIdx)) { // 유효하지 않은 건강질환 idx
                throw new CustomException(CustomError.PET_HEALTH_IDX_ERROR);
            }
            // 직접입력
            if (Objects.equals(idx, healthCodeMax)) {
                if (ObjectUtils.isEmpty(petDto.getHealthEtc())) {
                    throw new CustomException(CustomError.PET_HEALTH_ETC_NULL);
                }
                if (petDto.getHealthEtc().length() > healthTextMax) { // 최대 입력 가능 글자수 초과
                    throw new CustomException(CustomError.PET_HEALTH_TEXT_LIMIT_OVER);
                }
            }
        }

        /** allergy **/
        List<Long> allergyIdxList = petDto.getAllergyIdxList();
        if (ObjectUtils.isEmpty(allergyIdxList)) { // 알러지 미선택
            throw new CustomException(CustomError.PET_ALLERGY_IDX_NULL);
        }
        for (Long idx : allergyIdxList) {
            // 알러지 idx DB 체크
            Boolean bValidAllergyIdx = checkAllergyIdx(idx);
            if (Boolean.FALSE.equals(bValidAllergyIdx)) { // 유효하지 않은 알러지 idx
                throw new CustomException(CustomError.PET_ALLERGY_IDX_ERROR);
            }
            // 직접입력
            if (Objects.equals(idx, allergyCodeMax)) {
                if (ObjectUtils.isEmpty(petDto.getHealthEtc())) {
                    throw new CustomException(CustomError.PET_ALLERGY_ETC_NULL);
                }
                if (petDto.getHealthEtc().length() > allergyTextMax) { // 최대 입력 가능 글자수 초과
                    throw new CustomException(CustomError.PET_ALLERGY_TEXT_LIMIT_OVER);
                }
            }
        }

        /** intro **/
        if (!ObjectUtils.isEmpty(petDto.getIntro())) {
            if (petDto.getIntro().length() > introTextMax) {
                throw new CustomException(CustomError.PET_INTRO_TEXT_LIMIT_OVER);
            }
        }

    }

    /**
     * 수정 유효성 검사
     *
     * @param petDto : petUuid, memberUuid, name, number, gender, breedIdx, size,
     *               weight, birth, personalityList, healthIdxList, allergyIdxList
     *               personalityEtc, healthEtc, allergyEtc
     */
    private void modifyValidate(PetDto petDto) {

        if (Boolean.FALSE.equals(checkDupleUuid(petDto.getPetUuid()))) {
            throw new CustomException(CustomError.PET_UUID_ERROR); // 존재하지 않는 반려동물입니다.
        }

        // 회원 소유 반려동물인지 조회
        Boolean isMyPet = checkMyPet(petDto);

        if (Boolean.FALSE.equals(isMyPet)) { // 회원의 pet이 아닌 경우
            throw new CustomException(CustomError.PET_NOT_MEMBER_OWN_PET); // 회원 소유의 반려동물이 아닙니다.
        }

        // 등록 유효성 공통
        registValidate(petDto);
    }

    /**
     * 삭제 유효성 검사
     *
     * @param petDto petUuid memberUuid
     */
    private void deletePetValidate(PetDto petDto) {

        // 반려동물 고유아이디 검증
        if (Boolean.FALSE.equals(checkDupleUuid(petDto.getPetUuid()))) {
            throw new CustomException(CustomError.PET_UUID_ERROR); // 존재하지 않는 반려동물입니다.
        }

        // 회원 소유 반려동물인지 조회
        Boolean isMyPet = checkMyPet(petDto);
        
        if (Boolean.FALSE.equals(isMyPet)) { // 회원의 반려동물이 아닌 경우
            throw new CustomException(CustomError.PET_NOT_MEMBER_OWN_PET); // 회원 소유의 반려동물이 아닙니다.
        }
    }
}
