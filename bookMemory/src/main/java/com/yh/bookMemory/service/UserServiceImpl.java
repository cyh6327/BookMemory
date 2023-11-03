package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.UserDTO;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Primary
@Service
@Log4j2
public class UserServiceImpl implements UserService, CommonService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public Users getUserInfo(String email) {
        log.info("getUserInfo...........................");
        Users user = userRepository.getReferenceByUserEmail(email);

        return user;
    }

    @Override
    public Users createUser(HashMap<String, String> map) {
        log.info("createUser..........................."+map);

        UserDTO userDTO = UserDTO.builder()
                .userEmail(map.get("email"))
                .userName(map.get("name"))
                .refreshToken(null)
                .build();

        log.info("userDTO..........................."+userDTO.toString());

        Users usersEntity = userDtoToEntitiy(userDTO);
        Users userInfo = userRepository.saveAndFlush(usersEntity);

        return userInfo;
    }

    @Override
    public Users updateUser(HashMap<String, String> map) {
        log.info("updateUser..........................."+map);

        Users user = getUserInfo(map.get("email"));

        UserDTO userDto = userEntityToDto(user);
        userDto.setRefreshToken(map.get("refreshToken"));

        user = userDtoToEntitiy(userDto);

        Users result = userRepository.saveAndFlush(user);

        return result;
    }
}
