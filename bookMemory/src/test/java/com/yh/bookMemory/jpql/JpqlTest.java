package com.yh.bookMemory.jpql;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yh.bookMemory.BookMemoryApplication;
import com.yh.bookMemory.entity.BookSentences;
import com.yh.bookMemory.entity.Users;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yh.bookMemory.entity.QBookSentences.bookSentences;
import static com.yh.bookMemory.entity.QUsers.users;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ContextConfiguration(classes = BookMemoryApplication.class)
@Log4j2
public class JpqlTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    public void searchQuerydsl() {
        Users findUser = queryFactory
                .select(users)
                .from(users)
                .where(users.userName.eq("최연희"))
                .fetchOne();

        assertNotNull(findUser);
    }

    // SELECT * FROM (SELECT *, ROW_NUMBER() OVER(ORDER BY :sortKey) AS ROWNUM FROM BOOK_SENTENCES) WHERE ROWNUM > :sendCnt LIMIT :limit
    @DisplayName("SELECT 랜덤 문장 5개")
    @Test
    public void selectRandomSentences() {
        Double random = Math.random();
        NumberTemplate<Double> numberTemplate = Expressions.numberTemplate(Double.class, "{0}", random);

        List<BookSentences> result = queryFactory
                .selectFrom(bookSentences)
                .orderBy(numberTemplate.desc())
                .limit(5)
                .fetch();

        log.info(result);
        assertEquals(5, result.size());
    }
}
