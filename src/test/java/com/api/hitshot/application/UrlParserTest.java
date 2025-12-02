package com.api.hitshot.application;

import com.api.hitshot.infra.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlParserTest {

    UrlParser urlParser;

    @BeforeEach
    void setUp() {
        urlParser = new UrlParser();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://www.naver.com",
            "https://naver.com",
            "https://www.naver.com/hello/iam/test",
            "https://www.naver.com/hello/iam/test?query=params"})
    void 도메인_추출_테스트(String domain) {
        String result = urlParser.makeValidFormat(domain);
        assertEquals("https://naver.com", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://lcw3176.github.io",
            "https://lcw3176.github.io/hello/iam/test",
            "https://lcw3176.github.io/hello/iam/test?query=params"})
    void 서브_도메인_추출_테스트(String domain) {
        String result = urlParser.makeValidFormat(domain);
        assertEquals("https://lcw3176.github.io", result);
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "http://example.com",
            "http://www.example.com",
            "http://example.com/path",
            "http://www.example.com/path?query=test"})
    void HTTP_프로토콜_도메인_추출_테스트(String domain) {
        String result = urlParser.makeValidFormat(domain);
        assertEquals("http://example.com", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ftp://files.example.com",
            "ftp://www.files.example.com/downloads"})
    void FTP_프로토콜_도메인_추출_테스트(String domain) {
        String result = urlParser.makeValidFormat(domain);
        assertEquals("ftp://files.example.com", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://api.google.com",
            "https://www.api.google.com",
            "https://api.google.com/v1/endpoint"})
    void 다양한_도메인_추출_테스트(String domain) {
        String result = urlParser.makeValidFormat(domain);
        assertEquals("https://api.google.com", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://test-site.co.kr",
            "https://www.test-site.co.kr",
            "https://test-site.co.kr/path/to/resource"})
    void 하이픈_포함_도메인_추출_테스트(String domain) {
        String result = urlParser.makeValidFormat(domain);
        assertEquals("https://test-site.co.kr", result);
    }

    @Test
    void WWW_없는_도메인_추출_테스트() {
        String result = urlParser.makeValidFormat("https://github.com/user/repo");
        assertEquals("https://github.com", result);
    }

    @Test
    void 복잡한_경로_포함_도메인_추출_테스트() {
        String url = "https://www.example.com/path/to/resource/deep/nested?query=value&param=test#fragment";
        String result = urlParser.makeValidFormat(url);
        assertEquals("https://example.com", result);
    }

    @Test
    void 다중_쿼리_파라미터_포함_도메인_추출_테스트() {
        String url = "https://www.example.com/search?q=test&page=1&limit=10";
        String result = urlParser.makeValidFormat(url);
        assertEquals("https://example.com", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-url",
            "not a url",
            "www.example.com",
            "example.com",
            "://example.com",
            "https://",
            "https://.",
            ""})
    void 잘못된_URL_형식_예외_테스트(String invalidUrl) {
        assertThrows(ApiException.class, () -> {
            urlParser.makeValidFormat(invalidUrl);
        });
    }

    @Test
    void NULL_URL_예외_테스트() {
        assertThrows(Exception.class, () -> {
            urlParser.makeValidFormat(null);
        });
    }

    @Test
    void 프로토콜_없는_URL_예외_테스트() {
        assertThrows(ApiException.class, () -> {
            urlParser.makeValidFormat("www.example.com");
        });
    }

    @Test
    void 빈_문자열_예외_테스트() {
        assertThrows(ApiException.class, () -> {
            urlParser.makeValidFormat("");
        });
    }

    @Test
    void 공백만_있는_URL_예외_테스트() {
        assertThrows(ApiException.class, () -> {
            urlParser.makeValidFormat("   ");
        });
    }
}