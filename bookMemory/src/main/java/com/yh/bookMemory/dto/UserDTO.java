package com.yh.bookMemory.dto;

import com.yh.bookMemory.entity.Users;
import lombok.Builder;
import lombok.Data;

@Data   //Getter/Setter, toString(), equals(), hashCode() 자동 생성
@Builder(toBuilder = true)
public class UserDTO {

    private Long userKey;

    private String userEmail;

    private String userName;

    private String refreshToken;

    @Builder.Default
    private Double sortKey = 0.0; // 랜덤 문장 선정할 때 order by 할 난수

    @Builder.Default
    private Integer sendCnt = 0;    // 랜덤 문장 받은 개수

    public Users toEntity() {
        return Users.builder()
                .userKey(userKey)
                .userEmail(userEmail)
                .userName(userName)
                .refreshToken(refreshToken)
                .sortKey(sortKey)
                .sendCnt(sendCnt)
                .build();
    }

}
