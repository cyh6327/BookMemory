package com.yh.bookMemory.repository;

import com.yh.bookMemory.RandomSentenceSub;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class BookSentencesCustomRepositoryTest {

    //@PersistenceContext
    @Autowired
    private EntityManager entityManager;

    @Test
    public void findAllBooks() {
        List<RandomSentenceSub> list = entityManager.createQuery("SELECT r FROM RandomSentenceSub r", RandomSentenceSub.class)
                .getResultList();

        assertNotNull(list);
    }
}
