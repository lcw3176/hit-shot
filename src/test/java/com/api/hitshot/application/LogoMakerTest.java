package com.api.hitshot.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class LogoMakerTest {

    private LogoMaker logoMaker;

    @BeforeEach
    void setUp() {
        logoMaker = new LogoMaker();
    }

    @Test
    void 오늘과_전체_방문자수_SVG_로고_생성_테스트() {
        // given
        long today = 100;
        long total = 1000;
        String color = "#4c1";

        // when
        String result = logoMaker.makeTodaySvgLogo(today, total, color);

        // then
        assertNotNull(result);
        assertTrue(result.contains("<svg"));
        assertTrue(result.contains("hit-shot"));
        assertTrue(result.contains("100"));
        assertTrue(result.contains("1,000"));
        assertTrue(result.contains(color));
        assertTrue(result.contains("</svg>"));
    }

    @Test
    void 전체_방문자수만_포함된_SVG_로고_생성_테스트() {
        // given
        long total = 5000;
        String color = "#007ec6";

        // when
        String result = logoMaker.makeOnlyTotalSvgLogo(total, color);

        // then
        assertNotNull(result);
        assertTrue(result.contains("<svg"));
        assertTrue(result.contains("hit-shot"));
        assertTrue(result.contains("5,000"));
        assertTrue(result.contains(color));
        assertTrue(result.contains("</svg>"));
    }

    @Test
    void 큰_숫자_포맷팅_테스트() {
        // given
        long today = 123456;
        long total = 9876543;
        String color = "#4c1";

        // when
        String result = logoMaker.makeTodaySvgLogo(today, total, color);

        // then
        assertNotNull(result);
        assertTrue(result.contains("123,456"));
        assertTrue(result.contains("9,876,543"));
    }

    @Test
    void 작은_숫자_로고_생성_테스트() {
        // given
        long total = 1;
        String color = "#4c1";

        // when
        String result = logoMaker.makeOnlyTotalSvgLogo(total, color);

        // then
        assertNotNull(result);
        assertTrue(result.contains("1"));
    }

    @Test
    void SVG_너비_계산_테스트() {
        // given
        long today = 100;
        long total = 1000;
        String color = "#4c1";

        // when
        String result = logoMaker.makeTodaySvgLogo(today, total, color);

        // then
        assertTrue(result.contains("width="));
        assertTrue(result.contains("height=\"20\""));
    }

    @Test
    void 방문자_0_로고_생성_테스트() {
        // given
        long today = 0;
        long total = 0;
        String color = "#4c1";

        // when
        String result = logoMaker.makeTodaySvgLogo(today, total, color);

        // then
        assertNotNull(result);
        assertTrue(result.contains("0"));
        assertTrue(result.contains("<svg"));
        assertTrue(result.contains("</svg>"));
    }

    @ParameterizedTest
    @CsvSource({
            "#4c1, #4c1",
            "#007ec6, #007ec6",
            "#ff0000, #ff0000",
            "#00ff00, #00ff00",
            "blue, blue",
            "red, red"
    })
    void 다양한_색상_로고_생성_테스트(String inputColor, String expectedColor) {
        // given
        long total = 100;

        // when
        String result = logoMaker.makeOnlyTotalSvgLogo(total, inputColor);

        // then
        assertTrue(result.contains(expectedColor));
    }

    @Test
    void 매우_큰_숫자_로고_생성_테스트() {
        // given
        long total = 999999999L;
        String color = "#4c1";

        // when
        String result = logoMaker.makeOnlyTotalSvgLogo(total, color);

        // then
        assertNotNull(result);
        assertTrue(result.contains("999,999,999"));
    }

    @Test
    void SVG_구조_검증_테스트() {
        // given
        long total = 1000;
        String color = "#4c1";

        // when
        String result = logoMaker.makeOnlyTotalSvgLogo(total, color);

        // then
        assertTrue(result.contains("xmlns=\"http://www.w3.org/2000/svg\""));
        assertTrue(result.contains("role=\"img\""));
        assertTrue(result.contains("fill=\"#555\""));
        assertTrue(result.contains("fill=\"" + color + "\""));
        assertTrue(result.contains("font-family=\"Verdana, sans-serif\""));
        assertTrue(result.contains("font-size=\"12\""));
    }

    @Test
    void 오늘_방문자가_전체보다_많은_경우_테스트() {
        // given
        long today = 1000;
        long total = 100;
        String color = "#4c1";

        // when
        String result = logoMaker.makeTodaySvgLogo(today, total, color);

        // then
        assertNotNull(result);
        assertTrue(result.contains("1,000"));
        assertTrue(result.contains("100"));
    }

    @Test
    void 슬래시_구분자_포함_테스트() {
        // given
        long today = 50;
        long total = 100;
        String color = "#4c1";

        // when
        String result = logoMaker.makeTodaySvgLogo(today, total, color);

        // then
        assertTrue(result.contains("50 / 100"));
    }

    @Test
    void 최소_길이_적용_테스트() {
        // given
        long total = 1; // 길이가 1이지만 최소 5로 설정됨
        String color = "#4c1";

        // when
        String result = logoMaker.makeOnlyTotalSvgLogo(total, color);

        // then
        assertNotNull(result);
        assertTrue(result.contains("width="));
        // 최소 길이가 5로 적용되어 SVG가 정상 생성되는지 확인
    }

    @Test
    void 텍스트_앵커_미들_검증_테스트() {
        // given
        long total = 100;
        String color = "#4c1";

        // when
        String result = logoMaker.makeOnlyTotalSvgLogo(total, color);

        // then
        assertTrue(result.contains("text-anchor=\"middle\""));
    }

    @Test
    void 동일한_입력에_대한_일관성_테스트() {
        // given
        long total = 5000;
        String color = "#4c1";

        // when
        String result1 = logoMaker.makeOnlyTotalSvgLogo(total, color);
        String result2 = logoMaker.makeOnlyTotalSvgLogo(total, color);

        // then
        assertEquals(result1, result2);
    }

    @Test
    void 숫자_포맷_쉼표_검증_테스트() {
        // given
        long total = 1234567890L;
        String color = "#4c1";

        // when
        String result = logoMaker.makeOnlyTotalSvgLogo(total, color);

        // then
        assertTrue(result.contains("1,234,567,890"));
    }
}