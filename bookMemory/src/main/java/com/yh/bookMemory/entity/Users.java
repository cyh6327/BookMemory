package com.yh.bookMemory.entity;

import com.yh.bookMemory.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userKey;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String userName;

    @Column(length = 1000)
    private String refreshToken;

    @Column
    @Builder.Default
    private Double sortKey = 0.0; // 랜덤 문장 선정할 때 order by 할 난수

    @Column
    @Builder.Default
    private Integer sendCnt = 0;    // 랜덤 문장 받은 개수

    public UserDTO toDTO() {
        return UserDTO.builder()
                .userKey(userKey)
                .userEmail(userEmail)
                .userName(userName)
                .refreshToken(refreshToken)
                .sortKey(sortKey)
                .sendCnt(sendCnt)
                .build();
    }

}
