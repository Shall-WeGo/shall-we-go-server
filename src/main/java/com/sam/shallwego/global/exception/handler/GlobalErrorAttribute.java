package com.sam.shallwego.global.exception.handler;

import com.sam.shallwego.global.exception.BusinessException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class GlobalErrorAttribute extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request,
                                                  ErrorAttributeOptions options) {
        Map<String, Object> map = super.getErrorAttributes(request, options);
        Throwable throwable = getError(request);
        if (throwable instanceof BusinessException) {
            BusinessException exception = (BusinessException) throwable;
            map.put("message", exception.getMessage());
            map.put("status", exception.getStatus().value());
            map.put("error", exception.getStatus().getReasonPhrase());
        }
        else {
            map.put("exception", "SystemException");
            map.put("message", throwable.getMessage());
        }
        return map;
    }
}
