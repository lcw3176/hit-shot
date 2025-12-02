package com.api.hitshot.application.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SiteVisitorTest {

    @Test
    void SiteVisitor_빌더_생성_테스트() {
        // given
        long total = 1000L;
        long today = 100L;

        // when
        SiteVisitor visitor = SiteVisitor.builder()
                .total(total)
                .today(today)
                .build();

        // then
        assertNotNull(visitor);
        assertEquals(total, visitor.getTotal());
        assertEquals(today, visitor.getToday());
    }

    @Test
    void SiteVisitor_생성자_테스트() {
        // given
        long total = 500L;
        long today = 50L;

        // when
        SiteVisitor visitor = new SiteVisitor(total, today);

        // then
        assertNotNull(visitor);
        assertEquals(total, visitor.getTotal());
        assertEquals(today, visitor.getToday());
    }

    @Test
    void SiteVisitor_기본_생성자_테스트() {
        // when
        SiteVisitor visitor = new SiteVisitor();

        // then
        assertNotNull(visitor);
        assertEquals(0L, visitor.getTotal());
        assertEquals(0L, visitor.getToday());
    }

    @Test
    void SiteVisitor_큰_숫자_테스트() {
        // given
        long total = Long.MAX_VALUE;
        long today = Long.MAX_VALUE - 1;

        // when
        SiteVisitor visitor = SiteVisitor.builder()
                .total(total)
                .today(today)
                .build();

        // then
        assertEquals(total, visitor.getTotal());
        assertEquals(today, visitor.getToday());
    }
}