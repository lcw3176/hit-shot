package com.api.hitshot.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UrlParts {

    private String protocol;
    private String domain;
    private String rootDomain;


    public String merge() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.protocol);
        sb.append(this.domain);
        sb.append(".");
        sb.append(this.rootDomain);

        return sb.toString();
    }
}
