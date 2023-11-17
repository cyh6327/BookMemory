package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.UserDTO;
import com.yh.bookMemory.entity.Users;

import java.util.HashMap;
import java.util.Map;

public interface UserService {

    default Users userDtoToEntitiy(UserDTO dto) {
        Users usersEntity = Users.builder()
                .userKey(dto.getUserKey())
                .userEmail(dto.getUserEmail())
                .userName(dto.getUserName())
                .refreshToken(dto.getRefreshToken())
                .build();

        return usersEntity;
    }

    default UserDTO userEntityToDto(Users entity) {
        UserDTO userDTO = UserDTO.builder()
                .userKey(entity.getUserKey())
                .userEmail(entity.getUserEmail())
                .userName(entity.getUserName())
                .refreshToken(entity.getRefreshToken())
                .build();

        return userDTO;
    }

    Users getUserInfoByUserEmail(String userEmail);
    Users getUserInfoByUserKey(Long userKey);

    Users createUser(HashMap<String, String> map);
    Users updateRefreshToken(String refreshToken);

}
