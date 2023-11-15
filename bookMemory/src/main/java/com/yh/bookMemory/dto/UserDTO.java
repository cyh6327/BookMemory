package com.yh.bookMemory.dto;

import lombok.Builder;
import lombok.Data;

@Data   //Getter/Setter, toString(), equals(), hashCode() 자동 생성
@Builder(toBuilder = true)
public class UserDTO {

    private Long userKey;

    private String userEmail;

    private String userName;

    private String refreshToken;

}
