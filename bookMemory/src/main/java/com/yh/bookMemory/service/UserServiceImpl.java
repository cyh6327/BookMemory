package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.UserDTO;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.jwt.CreateJwt;
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
    public Users getUserInfoByUserEmail(String userEmail) {
        log.info("getUserInfoByUserEmail...........................");
        Users user = userRepository.getReferenceByUserEmail(userEmail);

        return user;
    }

    @Override
    public Users getUserInfoByUserKey(Long userKey) {
        log.info("getUserInfoByUserKey...........................");
        Users user = userRepository.getReferenceByUserKey(userKey);

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
    public Users updateRefreshToken(String refreshToken, Long userKey) {
        log.info("updateRefreshToken..........................."+refreshToken);

        //Long userKey = 1L;
        Users user = getUserInfoByUserKey(userKey);

        // refresh token이 만료된 경우 null로 넘겨주기 때문에 새로운 refresh token 생성
        if(refreshToken == null) {
            String accessToken = CreateJwt.createAccessToken(user);
            refreshToken = CreateJwt.createRefreshToken(user, accessToken);

            log.info("accessToken................................."+accessToken);
            log.info("refreshToken................................."+refreshToken);
        }

        UserDTO userDto = userEntityToDto(user);
        userDto.setRefreshToken(refreshToken);

        user = userDtoToEntitiy(userDto);

        Users result = userRepository.saveAndFlush(user);

        return result;
    }

}
