package com.nikhil.ratelimit.processor;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Objects;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.nikhil.ratelimit.config.RateLimitConfiguration.Rule;
import com.nikhil.ratelimit.config.RateLimitResponse;
import com.nikhil.ratelimit.error.RateLimiterErrorHandler;

@RequiredArgsConstructor
@Component
public class RedisRateLimitProcessor implements RateLimitProcessor {
	private final RateLimiterErrorHandler rateLimiterErrorHandler;
	private final RedisTemplate redisTemplate;

	@Override
	public synchronized RateLimitResponse process(Rule rule, String requestKey,
			Long requestTime) {
		final Long refreshAfter = rule.getRefreshAfter();
		final RateLimitResponse response = new RateLimitResponse(requestKey,
				rule.getRuleLimit(),null);
		remainingLimit(rule.getRuleLimit(), refreshAfter, requestKey, requestTime, response);
		return response;
	}

	protected void remainingLimit(Long limit, Long refreshAfter,
			String requestKey, Long requestTime, RateLimitResponse response) {
		if (Objects.nonNull(limit)) {
			long usage = requestTime == null ? 1L : 0L;
			Long remaining = calcRemaining(limit, refreshAfter, usage,
					requestKey, response);
			response.setRemaining(remaining);
		}
	}

	private Long calcRemaining(Long limit, Long refreshAfter, long usage,
			String key, RateLimitResponse response) {
		response.setReset(SECONDS.toMillis(refreshAfter));
		Long current = 0L;
		try {
			current = redisTemplate.opsForValue().increment(key, usage);
			if (current != null && current.equals(1L)) {
				handleExpiration(key, refreshAfter);
			}
		} catch (RuntimeException e) {
			String msg = "Failed retrieving rate for " + key
					+ ", will return the current value";
			rateLimiterErrorHandler.handleError(msg, e);
		}
		return Math.max(-1, limit - current);
	}

    private void handleExpiration(String key, Long refreshInterval) {
        try {
            this.redisTemplate.expire(key, refreshInterval, SECONDS);
        } catch (RuntimeException e) {
            String msg = "Failed retrieving expiration for " + key + ", will reset now";
            rateLimiterErrorHandler.handleError(msg, e);
        }
    }
}
