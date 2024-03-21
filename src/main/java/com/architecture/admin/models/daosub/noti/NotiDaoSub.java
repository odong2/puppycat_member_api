package com.architecture.admin.models.daosub.noti;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.noti.NotiDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface NotiDaoSub {
    /**
     * 전체 카운트
     *
     * @param searchDto
     * @return count
     */
    Long getTotalCount(SearchDto searchDto);

    /**
     * 마지막으로 알림확인한 날짜 가져오기
     *
     * @param notiDto memberUuid
     * @return 날짜
     */
    Integer getEventNotiSetting(NotiDto notiDto);

    /**
     * 마지막으로 알림확인한 날짜 가져오기
     *
     * @param notiDto memberUuid
     * @return 날짜
     */
    String getNotiShowDate(NotiDto notiDto);

    Long getNotiDuple(NotiDto notiDto);

    /**
     * 회원별 신규 공지 알림 리스트
     *
     * @param notiDto memberUuid showDate
     * @return 공지리스트
     */
    List<NotiDto> getNoticeNotiList(NotiDto notiDto);


    List<NotiDto> getNotiList(SearchDto searchDto);
    /**
     * 회원별 신규 알림 카운트
     *
     * @param notiDto memberUuid showDate
     * @return count
     */
    Integer getCountNewNoti(NotiDto notiDto);

    /**
     * 신규 공지 알림 카운트
     *
     * @param notiDto showDate
     * @return count
     */
    Integer getCountNewNoticeNoti(NotiDto notiDto);

    /**
     * 팔로워 uuid 가져오기
     *
     * @param notiDto
     * @return
     */
    List<String> getFollowerMember(NotiDto notiDto);
}
