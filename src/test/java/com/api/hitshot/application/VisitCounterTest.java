package com.api.hitshot.application;

import com.api.hitshot.application.model.SiteVisitor;
import com.api.hitshot.domain.buckets.BucketCounter;
import com.api.hitshot.domain.dailyScore.DailyScoreService;
import com.api.hitshot.domain.sites.SiteService;
import com.api.hitshot.infra.exception.ApiException;
import com.api.hitshot.infra.exception.status.ErrorCode;
import io.github.bucket4j.Bucket;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitCounterTest {

    @Mock
    private SiteService siteService;

    @Mock
    private BucketCounter bucketCounter;

    @Mock
    private DailyScoreService dailyScoreService;

    @Mock
    private Bucket bucket;

    @InjectMocks
    private VisitCounter visitCounter;

    private ObjectId testSiteId;
    private String testUrl;

    @BeforeEach
    void setUp() {
        testSiteId = new ObjectId();
        testUrl = "https://example.com";
    }

    @Test
    void 전체_방문자수_조회_테스트() {
        // given
        long expectedToday = 50L;
        long expectedTotal = 1000L;

        when(siteService.findIdByUrl(testUrl)).thenReturn(testSiteId);
        when(dailyScoreService.readTodayVisitors(testSiteId)).thenReturn(expectedToday);
        when(siteService.readVisitors(testUrl)).thenReturn(expectedTotal);

        // when
        SiteVisitor result = visitCounter.onlyGetTotalVisitorsCount(testUrl);

        // then
        assertNotNull(result);
        assertEquals(expectedToday, result.getToday());
        assertEquals(expectedTotal, result.getTotal());

        verify(siteService, times(1)).findIdByUrl(testUrl);
        verify(dailyScoreService, times(1)).readTodayVisitors(testSiteId);
        verify(siteService, times(1)).readVisitors(testUrl);
        verify(bucketCounter, never()).getBucketForDomain(anyString());
    }

    @Test
    void 방문자수_증가_및_조회_성공_테스트() {
        // given
        long expectedToday = 51L;
        long expectedTotal = 1001L;

        when(bucketCounter.getBucketForDomain(testUrl)).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);
        when(siteService.findIdByUrl(testUrl)).thenReturn(testSiteId);
        when(dailyScoreService.addTodayVisitors(testSiteId)).thenReturn(expectedToday);
        when(siteService.addVisitors(testUrl)).thenReturn(expectedTotal);

        // when
        SiteVisitor result = visitCounter.increaseAndGetCount(testUrl);

        // then
        assertNotNull(result);
        assertEquals(expectedToday, result.getToday());
        assertEquals(expectedTotal, result.getTotal());

        verify(bucketCounter, times(1)).getBucketForDomain(testUrl);
        verify(bucket, times(1)).tryConsume(1);
        verify(siteService, times(1)).findIdByUrl(testUrl);
        verify(dailyScoreService, times(1)).addTodayVisitors(testSiteId);
        verify(siteService, times(1)).addVisitors(testUrl);
    }

    @Test
    void 방문자수_증가_실패_Rate_Limit_테스트() {
        // given
        when(bucketCounter.getBucketForDomain(testUrl)).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(false);

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> {
            visitCounter.increaseAndGetCount(testUrl);
        });

        assertEquals(ErrorCode.RATE_LIMIT_ACTIVATED, exception.getCode());

        verify(bucketCounter, times(1)).getBucketForDomain(testUrl);
        verify(bucket, times(1)).tryConsume(1);
        verify(siteService, never()).findIdByUrl(anyString());
        verify(dailyScoreService, never()).addTodayVisitors(any());
        verify(siteService, never()).addVisitors(anyString());
    }

    @Test
    void 방문자수_0일_때_조회_테스트() {
        // given
        long expectedToday = 0L;
        long expectedTotal = 0L;

        when(siteService.findIdByUrl(testUrl)).thenReturn(testSiteId);
        when(dailyScoreService.readTodayVisitors(testSiteId)).thenReturn(expectedToday);
        when(siteService.readVisitors(testUrl)).thenReturn(expectedTotal);

        // when
        SiteVisitor result = visitCounter.onlyGetTotalVisitorsCount(testUrl);

        // then
        assertNotNull(result);
        assertEquals(0L, result.getToday());
        assertEquals(0L, result.getTotal());
    }

    @Test
    void 다른_URL_방문자수_증가_테스트() {
        // given
        String anotherUrl = "https://another-site.com";
        ObjectId anotherSiteId = new ObjectId();
        long expectedToday = 1L;
        long expectedTotal = 1L;

        when(bucketCounter.getBucketForDomain(anotherUrl)).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);
        when(siteService.findIdByUrl(anotherUrl)).thenReturn(anotherSiteId);
        when(dailyScoreService.addTodayVisitors(anotherSiteId)).thenReturn(expectedToday);
        when(siteService.addVisitors(anotherUrl)).thenReturn(expectedTotal);

        // when
        SiteVisitor result = visitCounter.increaseAndGetCount(anotherUrl);

        // then
        assertNotNull(result);
        assertEquals(expectedToday, result.getToday());
        assertEquals(expectedTotal, result.getTotal());

        verify(bucketCounter, times(1)).getBucketForDomain(anotherUrl);
    }


    @Test
    void 연속_방문자수_증가_테스트() {
        // given
        when(bucketCounter.getBucketForDomain(testUrl)).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);
        when(siteService.findIdByUrl(testUrl)).thenReturn(testSiteId);
        when(dailyScoreService.addTodayVisitors(testSiteId)).thenReturn(1L, 2L, 3L);
        when(siteService.addVisitors(testUrl)).thenReturn(1L, 2L, 3L);

        // when
        SiteVisitor result1 = visitCounter.increaseAndGetCount(testUrl);
        SiteVisitor result2 = visitCounter.increaseAndGetCount(testUrl);
        SiteVisitor result3 = visitCounter.increaseAndGetCount(testUrl);

        // then
        assertEquals(1L, result1.getToday());
        assertEquals(2L, result2.getToday());
        assertEquals(3L, result3.getToday());

        verify(bucketCounter, times(3)).getBucketForDomain(testUrl);
        verify(siteService, times(3)).addVisitors(testUrl);
    }

    @Test
    void 매우_큰_방문자수_조회_테스트() {
        // given
        long expectedToday = Long.MAX_VALUE - 1;
        long expectedTotal = Long.MAX_VALUE;

        when(siteService.findIdByUrl(testUrl)).thenReturn(testSiteId);
        when(dailyScoreService.readTodayVisitors(testSiteId)).thenReturn(expectedToday);
        when(siteService.readVisitors(testUrl)).thenReturn(expectedTotal);

        // when
        SiteVisitor result = visitCounter.onlyGetTotalVisitorsCount(testUrl);

        // then
        assertEquals(expectedToday, result.getToday());
        assertEquals(expectedTotal, result.getTotal());
    }

    @Test
    void 오늘_방문자만_있는_경우_테스트() {
        // given
        long expectedToday = 10L;
        long expectedTotal = 0L;

        when(siteService.findIdByUrl(testUrl)).thenReturn(testSiteId);
        when(dailyScoreService.readTodayVisitors(testSiteId)).thenReturn(expectedToday);
        when(siteService.readVisitors(testUrl)).thenReturn(expectedTotal);

        // when
        SiteVisitor result = visitCounter.onlyGetTotalVisitorsCount(testUrl);

        // then
        assertEquals(expectedToday, result.getToday());
        assertEquals(expectedTotal, result.getTotal());
    }

    @Test
    void 여러_도메인_동시_조회_테스트() {
        // given
        String url1 = "https://site1.com";
        String url2 = "https://site2.com";
        ObjectId siteId1 = new ObjectId();
        ObjectId siteId2 = new ObjectId();

        when(siteService.findIdByUrl(url1)).thenReturn(siteId1);
        when(siteService.findIdByUrl(url2)).thenReturn(siteId2);
        when(dailyScoreService.readTodayVisitors(siteId1)).thenReturn(100L);
        when(dailyScoreService.readTodayVisitors(siteId2)).thenReturn(200L);
        when(siteService.readVisitors(url1)).thenReturn(1000L);
        when(siteService.readVisitors(url2)).thenReturn(2000L);

        // when
        SiteVisitor result1 = visitCounter.onlyGetTotalVisitorsCount(url1);
        SiteVisitor result2 = visitCounter.onlyGetTotalVisitorsCount(url2);

        // then
        assertEquals(100L, result1.getToday());
        assertEquals(1000L, result1.getTotal());
        assertEquals(200L, result2.getToday());
        assertEquals(2000L, result2.getTotal());
    }

    @Test
    void Rate_Limit_후_재시도_성공_테스트() {
        // given
        when(bucketCounter.getBucketForDomain(testUrl)).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(false).thenReturn(true);
        when(siteService.findIdByUrl(testUrl)).thenReturn(testSiteId);
        when(dailyScoreService.addTodayVisitors(testSiteId)).thenReturn(1L);
        when(siteService.addVisitors(testUrl)).thenReturn(1L);

        // when & then
        assertThrows(ApiException.class, () -> {
            visitCounter.increaseAndGetCount(testUrl);
        });

        SiteVisitor result = visitCounter.increaseAndGetCount(testUrl);
        assertNotNull(result);
        assertEquals(1L, result.getToday());
    }

    @Test
    void 트랜잭션_ReadOnly_동작_확인_테스트() {
        // given
        when(siteService.findIdByUrl(testUrl)).thenReturn(testSiteId);
        when(dailyScoreService.readTodayVisitors(testSiteId)).thenReturn(50L);
        when(siteService.readVisitors(testUrl)).thenReturn(500L);

        // when
        visitCounter.onlyGetTotalVisitorsCount(testUrl);

        // then - 읽기 전용이므로 add 메서드가 호출되지 않아야 함
        verify(dailyScoreService, never()).addTodayVisitors(any());
        verify(siteService, never()).addVisitors(anyString());
    }
}