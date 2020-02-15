

package com.nikhil.ratelimit.error;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DefaultRateLimiterErrorHandler implements RateLimiterErrorHandler {

    @Override
    public void handleSaveError(String key, Exception e) {
        log.error("Failed saving rate for " + key + ", returning unsaved rate", e);
    }

    @Override
    public void handleFetchError(String key, Exception e) {
        log.error("Failed retrieving rate for " + key + ", will create new rate", e);
    }

    @Override
    public void handleError(String msg, Exception e) {
        log.error(msg, e);
    }
}
