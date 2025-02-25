package com.report.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {
    OVERSEAS("해외주식", "국내주식"), DOMESTIC("국내주식", "해외주식");

    private final String name;

    private final String opposite;
}
