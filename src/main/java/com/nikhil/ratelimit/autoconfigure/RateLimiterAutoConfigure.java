package com.nikhil.ratelimit.autoconfigure;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.util.UrlPathHelper;

import com.nikhil.ratelimit.config.RateLimitConfiguration;
import com.nikhil.ratelimit.error.DefaultRateLimiterErrorHandler;
import com.nikhil.ratelimit.error.RateLimiterErrorHandler;
import com.nikhil.ratelimit.filter.RedisRateLimitFilter;
import com.nikhil.ratelimit.generator.DefaultRateLimitKeyGenerator;
import com.nikhil.ratelimit.generator.RateLimitKeyGenerator;
import com.nikhil.ratelimit.processor.RateLimitProcessor;
import com.nikhil.ratelimit.processor.RedisRateLimitProcessor;
import com.nikhil.ratelimit.utilities.RateLimitUtils;
import com.nikhil.ratelimit.utilities.RateLimitUtilsImpl;
import com.nikhil.ratelimit.utilities.StringToMatchTypeConverter;
import com.nikhil.ratelimit.utilities.URLMappingFinder;
import com.nikhil.ratelimit.utilities.URLMappingFinderImpl;

/*
 * This is a Auto Configure class defined to auto start the component with all necessary items.
 * It is calling is defined in Spring.factories file defined in Resources/META-INF
 */
@Configuration
@EnableConfigurationProperties(RateLimitConfiguration.class)
@ConditionalOnProperty(prefix = RateLimitConfiguration.PREFIX, name = "enabled", havingValue = "true")
public class RateLimiterAutoConfigure {
	private final UrlPathHelper helper = new UrlPathHelper();
	
    @Bean
    @ConditionalOnMissingBean(RateLimiterErrorHandler.class)
    public RateLimiterErrorHandler rateLimiterErrorHandler() {
        return new DefaultRateLimiterErrorHandler();
    }
    
    @Bean
    @ConditionalOnMissingBean(RateLimitUtils.class)
    public RateLimitUtils rateLimitUtils(){
    	return new RateLimitUtilsImpl();
    }

	@Configuration
	@ConditionalOnClass(RedisTemplate.class)
	@ConditionalOnMissingBean(RateLimitProcessor.class)
	@ConditionalOnProperty(prefix = RateLimitConfiguration.PREFIX, name = "persistence", havingValue = "REDIS")
	public static class RedisConfiguration {

		@Bean("rateLimiterRedisTemplate")
		public StringRedisTemplate redisTemplate(
				final RedisConnectionFactory connectionFactory) {
			return new StringRedisTemplate(connectionFactory);
		}

		@Bean
		public RateLimitProcessor redisRateLimiter(
				final RateLimiterErrorHandler rateLimiterErrorHandler,
				@Qualifier("rateLimiterRedisTemplate") final RedisTemplate redisTemplate) {
			return new RedisRateLimitProcessor(rateLimiterErrorHandler, redisTemplate);
		}
	}
	
	@Bean
	@ConditionalOnMissingBean(URLMappingFinder.class)
	public URLMappingFinder finder(){
		return new URLMappingFinderImpl();
	}

	@Bean
	public Filter rateLimiterPreFilter(final RateLimitProcessor processor,
			final RateLimitConfiguration configuration,
			final URLMappingFinder finder,
			final RateLimitKeyGenerator generator, final RateLimitUtils utils) {
		return new RedisRateLimitFilter(configuration, finder, helper, utils,
				generator, processor);
	}

	@Bean
	@ConditionalOnMissingBean(RateLimitKeyGenerator.class)
	public RateLimitKeyGenerator ratelimitKeyGenerator(
			final RateLimitUtils rateLimitUtils) {
		return new DefaultRateLimitKeyGenerator(rateLimitUtils);
	}
	
    @Bean
    @ConfigurationPropertiesBinding
    public StringToMatchTypeConverter stringToMatchTypeConverter() {
        return new StringToMatchTypeConverter();
    }

}
