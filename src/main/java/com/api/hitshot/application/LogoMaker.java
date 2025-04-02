package com.api.hitshot.application;

import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.List;

@Component
public class LogoMaker {


    private static final String LOGO = "hit-shot";
    private static final NumberFormat FORMATTER = NumberFormat.getNumberInstance();

    public String makeTodaySvgLogo(long today, long total, String color) {
        String totalNumberFormat = FORMATTER.format(total);
        String todayNumberFormat = FORMATTER.format(today);

        String numberFormat = String.join("", List.of(todayNumberFormat, " / ", totalNumberFormat));

        return makeLogo(numberFormat.length(), color, numberFormat);
    }


    public String makeOnlyTotalSvgLogo(long total, String color) {
        String numberFormat = FORMATTER.format(total);

        return makeLogo(numberFormat.length(), color, numberFormat);
    }


    private String makeLogo(int length, String color, String numberFormat) {
        if (length < 5) {
            length = 5;
        }

        int labelWidth = LOGO.length() * 10;
        int statusWidth = length * 10;
        int totalWidth = labelWidth + statusWidth;

        return String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"20\" role=\"img\">" +
                        "<rect width=\"%d\" height=\"20\" fill=\"#555\" rx=\"3\"/>" +
                        "<rect x=\"%d\" width=\"%d\" height=\"20\" fill=\"%s\" rx=\"3\"/>" +
                        "<text x=\"%d\" y=\"14\" font-family=\"Verdana, sans-serif\" font-size=\"12\" fill=\"#fff\" text-anchor=\"middle\">%s</text>" +
                        "<text x=\"%d\" y=\"14\" font-family=\"Verdana, sans-serif\" font-size=\"12\" fill=\"#fff\" text-anchor=\"middle\">%s</text>" +
                        "</svg>",
                totalWidth,
                labelWidth,
                labelWidth, statusWidth, color,
                labelWidth / 2, "hit-shot",
                labelWidth + statusWidth / 2, numberFormat
        );
    }

}
