package com.yh.bookMemory.controller;

import com.yh.bookMemory.dto.BookInfoDTO;
import com.yh.bookMemory.dto.PageRequestDTO;
import com.yh.bookMemory.dto.PageResultDTO;
import com.yh.bookMemory.entity.BookInfo;
import com.yh.bookMemory.service.BookService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;

@Controller
@Log4j2
public class MainController {
    @Autowired
    BookService bookService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) throws IOException {
        log.info("dashboard.......");

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
        PageResultDTO<BookInfoDTO, BookInfo> resultDTO = bookService.getAllBookList(pageRequestDTO);

        List<BookInfoDTO> bookList = resultDTO.getDtoList();

        model.addAttribute("list", bookList);

        return "/layout/index";
    }

}
