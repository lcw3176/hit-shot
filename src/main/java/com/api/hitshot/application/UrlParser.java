package com.api.hitshot.application;

import com.api.hitshot.infra.exception.ApiException;
import com.api.hitshot.infra.exception.status.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UrlParser {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://(?:www\\.)?([a-zA-Z0-9.-]+)(/[^?#]*)?"
    );

    public String makeValidFormat(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);

        if (matcher.find()) {
            return matcher.group(1) + "://" + matcher.group(2);
        }

        throw new ApiException(ErrorCode.NOT_VALID_URL);
    }

    public String makeValidFormatWithPath(String url, String path) {
        String domain = makeValidFormat(url);
        String cleanPath = path.replaceAll("/+$", "");

        if (cleanPath.isEmpty() || cleanPath.equals("/")) {
            return domain;
        }

        if (!cleanPath.startsWith("/")) {
            cleanPath = "/" + cleanPath;
        }

        return domain + cleanPath;
    }

}
