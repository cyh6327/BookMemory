package com.example.ex2.repository;

import com.example.ex2.entity.repository.MemoRepository;
import jakarta.transaction.Transactional;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import com.example.ex2.entity.Memo;

@SpringBootTest
public class MemoRepositoryTests {
    @Autowired
    MemoRepository memoRepository;

    public MemoRepositoryTests() {
    }

    @Test
    public void testClass() {
        System.out.println(this.memoRepository.getClass().getName());
    }

    @Test
    public void testInsertDummies() {
        IntStream.rangeClosed(1, 100).forEach((i) -> {
            Memo memo = Memo.builder().memoText("Sample..." + i).build();
            this.memoRepository.save(memo);
        });
    }

    @Test
    public void testSelect() {
        Long mno = 100L;
        Optional<Memo> result = this.memoRepository.findById(mno);
        System.out.println("======================================");
        if (result.isPresent()) {
            Memo memo = (Memo)result.get();
            System.out.println(memo);
        }

    }

    @Transactional
    @Test
    public void testSelect2() {
        Long mno = 100L;
        Memo memo = (Memo)this.memoRepository.getOne(mno);
        System.out.println("======================================");
        System.out.println(memo);
    }

    @Test
    public void testUpdate() {
        Memo memo = Memo.builder().mno(100L).memoText("Update Text").build();
        System.out.println(this.memoRepository.save(memo));
    }

    @Test
    public void testDelete() {
        Long mno = 100L;
        this.memoRepository.deleteById(mno);
    }

    @Test
    public void testPageDefault() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Memo> result = this.memoRepository.findAll(pageable);
        System.out.println(result);
        System.out.println("----------------------------------");
        System.out.println("Total Pages: " + result.getTotalPages());
        System.out.println("Total Count: " + result.getTotalElements());
        System.out.println("Page Number : " + result.getNumber());
        System.out.println("Page Size : " + result.getSize());
        System.out.println("has next page? : " + result.hasNext());
        System.out.println("first page? : " + result.isFirst());
        System.out.println("----------------------------------");
        Iterator var3 = result.getContent().iterator();

        while(var3.hasNext()) {
            Memo memo = (Memo)var3.next();
            System.out.println(memo);
        }

    }

    @Test
    public void testSort() {
        Sort sort1 = Sort.by(new String[]{"mno"}).descending();
        Sort sort2 = Sort.by(new String[]{"memoText"}).ascending();
        Sort sortAll = sort1.and(sort2);
        Pageable pageable = PageRequest.of(0, 10, sortAll);
        Page<Memo> result = this.memoRepository.findAll(pageable);
        result.get().forEach((memo) -> {
            System.out.println(memo);
        });
    }

    @Test
    public void testQueryMethods() {
        List<Memo> list = this.memoRepository.findByMnoBetweenOrderByMnoDesc(70L, 80L);
        Iterator var2 = list.iterator();

        while(var2.hasNext()) {
            Memo memo = (Memo)var2.next();
            System.out.println(memo);
        }

    }

    @Test
    public void testQueryMethodWithPageable() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(new String[]{"mno"}).descending());
        Page<Memo> result = this.memoRepository.findByMnoBetween(10L, 50L, pageable);
        result.get().forEach((memo) -> {
            System.out.println(memo);
        });
    }

    @Commit
    @Transactional
    @Test
    public void testDeleteQueryMethods() {
        this.memoRepository.deleteMemoByMnoLessThan(10L);
    }
}
