package com.api.hitshot.application;

import com.api.hitshot.infra.exception.ApiException;
import com.api.hitshot.infra.exception.status.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UrlParser {


    private static final NumberFormat FORMATTER = NumberFormat.getNumberInstance();
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://(?:www\\.)?([a-zA-Z0-9.-]+?)\\.([a-zA-Z]+)(?:/|$)"
    );

    public String makeValidFormat(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);

        if (matcher.find()) {
            StringBuilder sb = new StringBuilder();
            sb.append(matcher.group(1) + "://");
            sb.append(matcher.group(2));
            sb.append(".");
            sb.append(matcher.group(3));

            return sb.toString();
        }

        throw new ApiException(ErrorCode.NOT_VALID_URL);
    }


}
