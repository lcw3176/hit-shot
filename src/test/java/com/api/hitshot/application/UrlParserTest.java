package com.api.hitshot.application;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UrlParserTest {

    UrlParser urlParser = new UrlParser();

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
}