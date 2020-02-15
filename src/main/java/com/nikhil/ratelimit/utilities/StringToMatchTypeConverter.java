package com.nikhil.ratelimit.utilities;
import javax.validation.constraints.NotNull;

import org.springframework.core.convert.converter.Converter;

import com.nikhil.ratelimit.config.RateLimitType;
import com.nikhil.ratelimit.config.RateLimitConfiguration.Rule.RuleType;


public class StringToMatchTypeConverter implements Converter<String, RuleType> {

    private static final String DELIMITER = "=";

    @Override
    public RuleType convert(@NotNull String type) {
        if (type.contains(DELIMITER)) {
            String[] matchType = type.split(DELIMITER);
            return new RuleType(RateLimitType.valueOf(matchType[0].toUpperCase()), matchType[1]);
        }
        return new RuleType(RateLimitType.valueOf(type.toUpperCase()), null);
    }
}
