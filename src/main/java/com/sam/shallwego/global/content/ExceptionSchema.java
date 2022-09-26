package com.sam.shallwego.global.content;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionSchema {

    private final long timestamp;
    private final String path;
    private final int status;
    private final String requestId;
    private final String message;

}
