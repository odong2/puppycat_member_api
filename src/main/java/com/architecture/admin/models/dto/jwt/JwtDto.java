package com.architecture.admin.models.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtDto {
    public JwtDto jwtDto;
    List<JwtDto> jwtDtoList;
    List<Integer> restrainList;
    private String memberUuid;
    private String appKey;
    private String grantType;
    private String secretKeyType;
    private String accessToken;
    private String refreshToken;
    private String accessKey;
    private String refreshKey;
    private Long refreshTokenId;
    private String keyEmail;
    private String regDate; // 등록일
    private long validTime = 10 * 60 * 1000L; // 유효시간 도 인자값 가능 10 * 60 * 1000L : 10분
    private long validTimeRefresh = 24 * 60 * 60 * 30 * 1000L; // 유효시간 도 인자값 가능 24 * 60 * 60 * 30 * 1000L : 30일
    //JWT 토큰 쿠키 유효시간
    private int cookieTime = 60 * 60 * 24 * 30;
    private Long refreshTokenExpireTime; // 리플레쉬 토큰 종료 시간
}