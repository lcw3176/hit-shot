package com.api.hitshot.application;

import com.api.hitshot.application.dto.UrlParts;
import com.api.hitshot.domain.sites.SiteService;
import com.api.hitshot.infra.exception.ApiException;
import com.api.hitshot.infra.exception.status.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class SiteUseCase {

    private static final NumberFormat FORMATTER = NumberFormat.getNumberInstance();
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://(?:www\\.)?([a-zA-Z0-9.-]+?)\\.([a-zA-Z]+)(?:/|$)"
    );
    private static final String LOGO = "hit-shot";

    private final SiteService siteService;

    public long onlyGetVisitorsCount(String url) {
        return siteService.readVisitors(url);
    }

    public long increaseAndGetCount(String url) {
        return siteService.addVisitors(url);
    }

    public String makeSvgLogo(long visitor, String color) {
        String numberFormat = FORMATTER.format(visitor);
        int length = numberFormat.length();

        if (length < 5) {
            length = 5;
        }

        int labelWidth = LOGO.length() * 10;
        int statusWidth = length * 10;
        int totalWidth = labelWidth + statusWidth;

        String svg = String.format(
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

        return svg;
    }

    public UrlParts filterUrlParts(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        if (matcher.find()) {

            return UrlParts.builder()
                    .protocol(matcher.group(1) + "://")
                    .domain(matcher.group(2))
                    .rootDomain(matcher.group(3))
                    .build();
        }

        throw new ApiException(ErrorCode.NOT_VALID_URL);
    }

}

