package com.architecture.admin.models.dto.member.profile;

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
public class MemberImageDto {
    /**
     * puppycat_member_profile_img
     */
    private Long idx;              // 고유번호
    private String uuid;           // 고유아이디
    private String memberUuid;       // 회원번호
    private String url;            // 이미지 url
    private String uploadPath;     // 이미지 경로
    private String uploadName;     // 이미지 파일명
    private Integer sort;          // 정렬순서
    private Integer isDel;         // 삭제여부
    private Integer state;         // 상태값
    private String stateText;      // 상태값 문자변환
    private String stateBg;        // 상태 bg 색상
    private String regDate;        // 등록일
    private String regDateTz;      // 등록일 타임존

    /**
     * puppycat_member
     **/
    private String id;             // 회원 id
    private String nick;           // 회원 닉네임

    private List<MultipartFile> uploadFile;    // 프로필 업로드 이미지

    // sql
    private Integer insertedId;
    private Integer affectedRow;


}
