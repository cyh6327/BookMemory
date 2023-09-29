package com.example.ex2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "tbl_memo"
)
public class Memo {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long mno;
    @Column(
            length = 200,
            nullable = false
    )
    private String memoText;

    public static MemoBuilder builder() {
        return new MemoBuilder();
    }

    public String toString() {
        Long var10000 = this.getMno();
        return "Memo(mno=" + var10000 + ", memoText=" + this.getMemoText() + ")";
    }

    public Long getMno() {
        return this.mno;
    }

    public String getMemoText() {
        return this.memoText;
    }

    public void setMno(final Long mno) {
        this.mno = mno;
    }

    public void setMemoText(final String memoText) {
        this.memoText = memoText;
    }

    public Memo(final Long mno, final String memoText) {
        this.mno = mno;
        this.memoText = memoText;
    }

    public Memo() {
    }

    public static class MemoBuilder {
        private Long mno;
        private String memoText;

        MemoBuilder() {
        }

        public MemoBuilder mno(final Long mno) {
            this.mno = mno;
            return this;
        }

        public MemoBuilder memoText(final String memoText) {
            this.memoText = memoText;
            return this;
        }

        public Memo build() {
            return new Memo(this.mno, this.memoText);
        }

        public String toString() {
            return "Memo.MemoBuilder(mno=" + this.mno + ", memoText=" + this.memoText + ")";
        }
    }
}
