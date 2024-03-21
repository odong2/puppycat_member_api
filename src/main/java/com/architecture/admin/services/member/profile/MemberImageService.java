package com.architecture.admin.services.member.profile;

import com.architecture.admin.libraries.S3Library;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.member.profile.MemberImageDao;
import com.architecture.admin.models.daosub.member.profile.MemberImageDaoSub;
import com.architecture.admin.models.dto.member.profile.MemberImageDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberImageService extends BaseService {

    private final MemberImageDao memberImageDao;
    private final MemberImageDaoSub memberImageDaoSub;
    private final S3Library s3Library;

    /*****************************************************
     *  Modules
     ****************************************************/
    /**
     * 프로필 이미지 등록/수정
     *
     * @param memberImageDto uploadFile, memberUuid
     */
    public void setProfileImage(MemberImageDto memberImageDto) {

        // 업로드할 이미지 유효성
        s3Library.checkUploadFiles(memberImageDto.getUploadFile());

        // 업로드 이미지
        List<MultipartFile> uploadFile = memberImageDto.getUploadFile();

        // s3에 저장될 path
        String s3Path = "member/" + memberImageDto.getMemberUuid();

        // s3 upload (원본)
        List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(uploadFile, s3Path);

        // DB 저장할 정보 세팅
        HashMap<String, Object> fileMap = setFileToDB(memberImageDto, s3Path, uploadResponse);

        // 고유아이디 set
        fileMap.put("uuid", getUuid());

        // 기존 프로필 이미지 존재 여부 체크
        Boolean bImageExist = checkProfileImage(memberImageDto);

        // 이미지 존재 -> update
        if (Boolean.TRUE.equals(bImageExist)) {
            // 이미지 DB 수정
            updateImage(fileMap);
        }
        // 이미지 존재 X -> insert
        else {
            // 이미지 DB 등록
            insertImage(fileMap);
        }

        // 로그 insert
        insertImageLog(fileMap);
    }

    /**
     * 프로필 이미지 초기화
     *
     * @param memberImageDto memberUuid
     */
    public void resetProfileImage(MemberImageDto memberImageDto) {
        // DB 저장할 정보 세팅
        HashMap<String, Object> fileMap = new HashMap<>();
        fileMap.put("memberUuid", memberImageDto.getMemberUuid());
        fileMap.put("fileUrl", "");
        fileMap.put("orgFileName", "");
        fileMap.put("uploadPath", "");
        fileMap.put("imgWidth", 0);
        fileMap.put("imgHeight", 0);
        fileMap.put("regDate", dateLibrary.getDatetime());

        // 고유아이디 set
        fileMap.put("uuid", getUuid());

        // 기존 프로필 이미지 존재 여부 체크
        Boolean bImageExist = checkProfileImage(memberImageDto);

        // 이미지 존재 -> update
        if (Boolean.TRUE.equals(bImageExist)) {
            // 이미지 DB 수정
            updateImage(fileMap);
        }
        // 이미지 존재 X -> insert
        else {
            // 이미지 DB 등록
            insertImage(fileMap);
        }

        // 로그 insert
        insertImageLog(fileMap);
    }

    /**
     * UUID 생성
     */
    public String getUuid() {
        // 프로필 이미지 uuid 세팅 ( locale Language + 랜덤 UUID + Timestamp )
        String localeLang = super.getLocaleLang();
        String setUuid = dateLibrary.getTimestamp();
        String uuid = "profile_img_" + localeLang + UUID.randomUUID().toString().concat(setUuid);
        uuid = uuid.replace("-", "");

        // 고유아이디 중복체크
        Boolean bDupleUuid = checkDupleUuid(uuid);

        // 고유아이디가 중복이면 5번 재시도
        int retry = 0;
        while (Boolean.TRUE.equals(bDupleUuid) && retry < 5) {
            retry++;
            pushAlarm("프로필 이미지 고유아이디 중복 시도::" + retry + "번째");

            // 회원 uuid 세팅 ( locale Language + 랜덤 UUID + Timestamp )
            localeLang = super.getLocaleLang();
            setUuid = dateLibrary.getTimestamp();
            uuid = "profile_img_" + localeLang + UUID.randomUUID().toString().concat(setUuid);
            uuid = uuid.replace("-", "");

            bDupleUuid = checkDupleUuid(uuid);

            if (retry == 5) {
                throw new CustomException(CustomError.PROFILE_IMAGE_UID_DUPLE);
            }
        }

        return uuid;
    }

    /*****************************************************
     *  Select
     ****************************************************/
    /**
     * 회원 프로필 이미지 등록여부 체크
     *
     * @param memberImageDto memberUuid
     * @return count
     */
    public Boolean checkProfileImage(MemberImageDto memberImageDto) {
        Integer iCount = memberImageDaoSub.getCountByImage(memberImageDto);

        return iCount > 0;
    }

    /**
     * 프로필 이미지 고유 아이디 중복 검색
     *
     * @param uuid 고유아이디
     * @return 중복여부 [중복 : true]
     */
    public Boolean checkDupleUuid(String uuid) {
        Integer iCount = memberImageDaoSub.getCountByUuid(uuid);

        return iCount > 0;
    }

    /*****************************************************
     *  Insert
     ****************************************************/
    /**
     * 이미지 등록
     *
     * @param uploadResponse 등록한 파일정보
     */
    public void insertImage(HashMap<String, Object> uploadResponse) {
        Integer iResult = memberImageDao.insertImage(uploadResponse);
        if (iResult < 1) {
            throw new CustomException(CustomError.PROFILE_IMAGE_ERROR); // 프로필 이미지 등록에 실패하였습니다.
        }
    }

    /**
     * 이미지 로그 등록
     *
     * @param uploadResponse 등록한 파일정보
     */
    private void insertImageLog(HashMap<String, Object> uploadResponse) {
        Integer iResult = memberImageDao.insertImageLog(uploadResponse);
        if (iResult < 1) {
            throw new CustomException(CustomError.PROFILE_IMAGE_LOG_ERROR); // 이미지 로그 등록에 실패하였습니다.
        }
    }

    /*****************************************************
     *  Update
     ****************************************************/
    /**
     * 이미지 수정
     *
     * @param uploadResponse 등록한 파일정보
     */
    public void updateImage(HashMap<String, Object> uploadResponse) {
        Integer iResult = memberImageDao.updateImage(uploadResponse);
        if (iResult < 1) {
            throw new CustomException(CustomError.PROFILE_IMAGE_UPDATE_ERROR); // 프로필 이미지 수정에 실패하였습니다.
        }
    }

    /*****************************************************
     *  SubFunction - Etc
     ****************************************************/
    /**
     * DB 저장할 정보 세팅
     *
     * @param memberImageDto memberUuid
     * @param s3Path         S3 경로
     * @param uploadResponse 업로드 이미지
     * @return hmImage
     */
    public HashMap<String, Object> setFileToDB(MemberImageDto memberImageDto, String s3Path, List<HashMap<String, Object>> uploadResponse) {

        HashMap<String, Object> hmImage = uploadResponse.get(0);

        hmImage.put("memberUuid", memberImageDto.getMemberUuid());
        hmImage.put("uploadPath", s3Path);
        hmImage.put("sort", 1);
        hmImage.put("regDate", dateLibrary.getDatetime());

        return hmImage;
    }

}
