package com.yh.bookMemory.service;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.entity.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

//    @DisplayName("책 생성")
//    @Test
//    void createBook() {
//        //given
//        Long userKey = getUserKeyFromJwt();
//        log.info("userKey....................................."+userKey);
//
//        //TODO: bookInfo 테이블에 user_key를 넣어야해서 일단은 Users entity를 찾아와서 넣어줬는데 조금 더 간단한 방법이 있는지 알아보고 수정
//        //생각해본건 아예 RequestContextHolder에 accesstoken 대신 users entity를 넣는 방법인데 일반적으로 이렇게 하는지는 모르겠네
//        Users user = userService.getUserInfoByUserKey(userKey);
//        dto.setUsers(user);
//
//        BookInfo entity = dtoToEntitiy(dto);
//
//        BookInfo createdBook = bookInfoRepository.save(entity);
//        BookInfoDTO createdBookDTO = entityToDto(createdBook);
//
//        log.info("createdBookDTO....................................."+createdBookDTO);
//
//        return createdBookDTO;
//    }
}