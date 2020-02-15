package com.nikhil.ratelimit.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.nikhil.ratelimit.utilities.RateLimitUtils;

/*
 * It is configuration class to have all the 
 * URLMapping for HTTP Request
 * RuleMap :- For pointing of all the Rate Limit rules corresponding to services
 * RateLimitPersistence:- For persistence logic in place. It is ENUM that can have any preference.
 * Check the application.yml file to know about the present values associated to this object
 */

@Data
@Validated
@ConfigurationProperties(RateLimitConfiguration.PREFIX)
public class RateLimitConfiguration {

	@NotNull
	private RateLimitPersistence persistence;
	
	private boolean enabled;
	
	public static final String PREFIX = "ratelimit";
	
	private List<Rule> defaultRule = new ArrayList<>();
	
	private Integer preOrderFilterOrder;
	
	private Map<String,URLMapping> mappingMap = new HashMap<String, URLMapping>();

	public Map<String, List<Rule>> ruleMap = new HashMap<String, List<Rule>>();
	
	public List<Rule> getRule(String key){
		return ruleMap.getOrDefault(key, defaultRule);
	}

	@Data
	@NoArgsConstructor
	public static class Rule {
		private Long ruleLimit;

		@NotNull
		private Long refreshAfter = TimeUnit.MINUTES.toSeconds(1L);

		@Valid
		@NotNull
		private List<RuleType> type = new ArrayList<>();
		
		@Data
		@AllArgsConstructor
		public static class RuleType {
			@Valid
			@NotNull
			private RateLimitType type;
			
			@Valid
			@NotNull
			private String match;

			public boolean applyRateLimit(HttpServletRequest request, URLMapping mapping,
					RateLimitUtils rateLimitUtils) {
				return type.applyRateLimit(request, mapping, rateLimitUtils, match);
			}

			public String key(HttpServletRequest request, URLMapping mapping,
					RateLimitUtils rateLimitUtils) {
				return type.key(request, mapping, rateLimitUtils, match);
			}
		}
	}
}
