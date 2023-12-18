package com.yh.bookMemory;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Entity
@Subselect(
        "SELECT *, ROW_NUMBER() OVER(ORDER BY SORT_KEY) AS ROWNUM FROM BOOK_SENTENCES"
)
@Immutable
@Synchronize("BOOK_SENTENCES")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RandomSentenceSub {

    private double sort_key;
    //SELECT *, ROW_NUMBER() OVER(ORDER BY :sortKey) AS ROWNUM FROM BOOK_SENTENCES

    public RandomSentenceSub(Double sortKey) {
        this.sort_key = sortKey;
    }
}
