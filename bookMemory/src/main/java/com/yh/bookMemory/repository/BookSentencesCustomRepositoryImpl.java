package com.yh.bookMemory.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yh.bookMemory.entity.BookSentences;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookSentencesCustomRepositoryImpl implements BookSentencesCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public BookSentencesCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<BookSentences> findByBookInfoBookId(Long bookId) {
        return null;
    }

    @Override
    public List<BookSentences> pickRandomSentences(double sortKey, long sendCnt, int limit) {
        return null;

        // Math.random()으로 생성한 값을 NumberTemplate으로 변환
        //NumberTemplate<Double> numberTemplate = Expressions.numberTemplate(Double.class, "{0}", sortKey);


//        return jpaQueryFactory.selectFrom(bookSentences)
//                .orderBy(numberTemplate.desc())
//                .limit(limit) //몇개를 뽑아올건지
//                .fetch();
    }
}
