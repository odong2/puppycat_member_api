package com.architecture.admin.models.dto;

import com.architecture.admin.libraries.PaginationLibray;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * 공통 페이징, 검색 Dto
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchDto {
    @Value("${cloud.aws.s3.img.url}")
    private String defaultImgDomain;
    // 검색, 페이징
    private String searchType;              // 검색 종류
    private String searchWord;              // 검색어
    private String memberUuid;              // 회원 UUID
    private String petUuid;                 // 펫 UUID
    private List<String> memberUuidList;    // uuidList
    private String loginMemberUuid;         // 로그인 회원 uuid
    private Long memberIdx;                 // 회원 idx
    private Long contentsIdx;               // 콘텐츠 idx
    private Long parentIdx;                 // 부모 댓글 idx
    private Long commentIdx;                // 댓글 idx
    private Long childCommentIdx;           // 자식 댓글 idx
    private Integer type;                   // type
    private Integer selfLike;               // 본인 게시글 좋아요 상태값 [0:안 누름 1:누름]
    private Integer imgLimit;               // 이미지 limit
    private Integer imgOffSet;              // 이미지 OffSet
    private String date;
    private String nowDate;
    private String startDate;               // 시작일
    private String endDate;                 // 종료일
    private Integer period;                 // 기간 (1: 1개월, 12: 1년)
    private String imgDomain;               // 이미지 도메인
    private PaginationLibray pagination;    // 페이징

    // selectBox 검색
    private String conditionFirst;
    private String conditionSecond;

    private String lang; // 언어
    private int page;
    // 시작위치
    private int offset;
    // 리스트 갯수
    @Setter(AccessLevel.PROTECTED)
    private int limit;
    // 한 페이지 리스트 수
    private int recordSize;
    // 최대 표시 페이징 갯수
    private int pageSize;

    // default paging
    public SearchDto() {
        this.page = 1;
        // 시작번호
        this.offset = 0;
        // DB 조회 갯수
        this.limit = 10;
        // 한 페이지 리스트 수
        this.recordSize = this.limit;
        // 최대 표시 페이징 갯수
        this.pageSize = 5;
        //이미지 도메인
        this.imgDomain = defaultImgDomain;
    }

    public int getOffset() {
        return (page - 1) * recordSize;
    }

    /**
     * limit setter 재정의
     *
     * @param limit
     */
    public void setLimit(Integer limit) {
        if (limit == null || limit < 1) {
            this.limit = 10;
        } else if (limit > 1000) { // 최대 max 값 설정
            this.limit = 1000;
            this.recordSize = 1000;
        } else {
            this.limit = limit;
            this.recordSize = limit;
        }
    }

}
