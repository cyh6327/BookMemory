package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.UserDTO;
import com.yh.bookMemory.entity.Users;

import java.util.HashMap;
import java.util.Map;

public interface UserService {

    default Users userDtoToEntitiy(UserDTO dto) {
        Users usersEntity = Users.builder()
                .userEmail(dto.getUserEmail())
                .userName(dto.getUserName())
                .refreshToken(dto.getRefreshToken())
                .build();

        return usersEntity;
    }

    default UserDTO userEntityToDto(Users entity) {
        UserDTO userDTO = UserDTO.builder()
                .userEmail(entity.getUserEmail())
                .userName(entity.getUserName())
                .refreshToken(entity.getRefreshToken())
                .build();

        return userDTO;
    }

    Users getUserInfo(String email);
    Users createUser(HashMap<String, String> map);
    Users updateUser(HashMap<String, String> map);

}
