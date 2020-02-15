package com.nikhil.ratelimit.filter;

import java.util.Optional;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import com.nikhil.ratelimit.config.RateLimitConfiguration;
import com.nikhil.ratelimit.config.RateLimitResponse;
import com.nikhil.ratelimit.config.URLMapping;
import com.nikhil.ratelimit.constants.RateLimitConstants;
import com.nikhil.ratelimit.generator.RateLimitKeyGenerator;
import com.nikhil.ratelimit.processor.RateLimitProcessor;
import com.nikhil.ratelimit.utilities.RateLimitUtils;
import com.nikhil.ratelimit.utilities.URLMappingFinder;


@Component
@Singleton
public class RedisRateLimitFilter extends RateLimitFilter {
	private final RateLimitConfiguration configuration;
	private final RateLimitProcessor processor;
	private final RateLimitKeyGenerator generator;

	public RedisRateLimitFilter(RateLimitConfiguration configuration,
			URLMappingFinder finder, UrlPathHelper helper,
			RateLimitUtils utils, RateLimitKeyGenerator generator,
			RateLimitProcessor processor) {
		super(configuration, finder, helper, utils);
		this.configuration = configuration;
		this.processor = processor;
		this.generator = generator;
	}
	
    @Override
    public String filterType() {
        return RateLimitConstants.RATE_LIMIT_PRE_FILTER;
    }

    @Override
    public int filterOrder() {
        return configuration.getPreOrderFilterOrder()!= null ?configuration.getPreOrderFilterOrder():RateLimitConstants.RATE_LIMIT_DEFAULT_PRE_ORDER;
    }

	@Override
	public Object applyFilter(HttpServletRequest request,
			HttpServletResponse response) {
		//Find out the URL Mapping associated to this request
		final Optional<URLMapping> optionalMapping = mapping(request);
		//Find out the Rules. In this case API throttling rules associated to URL Maapping
		getRules(optionalMapping, request).forEach(
				rule -> {
					//For each rule perform below task. 
					
					//Like generate the Key associated to this Request call. 
					final String key = generator.key(request, optionalMapping.orElse(null), rule);
					System.out.println("****"+key);
					//Call the Processor :- RedisRateLimitProcessor process this rule
					final RateLimitResponse res = processor.process(rule, key,null);
					
					//post processing find out the Header information that need to be associate with Response
					final Long limit = rule.getRuleLimit();
					final Long remaining = res.getRemaining();
					if (limit != null) {
						response.setHeader(RateLimitConstants.LIMIT
								+ key, String.valueOf(limit));
						response.setHeader(RateLimitConstants.REMAINING
								+ key,
								String.valueOf(Math.max(remaining, 0)));
					}
					
					if(limit !=null && remaining < 0){
						response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
						response.addHeader("rateLimitExceeded", "true");
						throw new RuntimeException();
					}
				});

		return null;
	}

}
