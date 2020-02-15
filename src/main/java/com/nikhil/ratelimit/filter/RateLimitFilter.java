package com.nikhil.ratelimit.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.util.UrlPathHelper;

import com.nikhil.ratelimit.config.RateLimitConfiguration;
import com.nikhil.ratelimit.config.RateLimitConfiguration.Rule;
import com.nikhil.ratelimit.config.RateLimitConfiguration.Rule.RuleType;
import com.nikhil.ratelimit.config.URLMapping;
import com.nikhil.ratelimit.utilities.RateLimitUtils;
import com.nikhil.ratelimit.utilities.URLMappingFinder;

/*
 * RateLimitFilter is abstract filter class. All Request will end up here as par of FilterChain processing.
 * It do basic validation regarding 
 * 		do we have Rate Limit Filter configured, 
 * 		do we have required mapping available for API or do we need to go for Default mapping
 * 	If above pass it will go for filtering the API as per Throttling rule defined for API
 *  And go with next step of processing of return back in case of failure.	
 * 
 */
@RequiredArgsConstructor
public abstract class RateLimitFilter implements Filter {

	private final RateLimitConfiguration configuration;
	private final URLMappingFinder finder;
	private final UrlPathHelper helper;
	private final RateLimitUtils utils;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			if (isFilterConfigured(httpRequest)) {
				try {
					applyFilter(httpRequest, httpResponse);
					httpResponse
							.getHeaderNames()
							.stream()
							.forEach(
									header -> System.out.println(header
											+ "--> "
											+ httpResponse.getHeader(header)));
				} catch (Throwable t) {
					System.out.println("Failed to execute filter");
					//throw t;
					return;
				}
			}
		}
		chain.doFilter(request, response);
		return;
	}

	//Type of Filter it is like PRE or POST. Individual subclass implementation need to define it.
	public abstract String filterType();

	//Order of execution for this filter.
	public abstract int filterOrder();

	//Apply the filter logic. It is abstract so individual implementation class need to implement this logic.
	public abstract Object applyFilter(HttpServletRequest request,
			HttpServletResponse response);

	protected Optional<URLMapping> mapping(HttpServletRequest request) {
		String requestURI = helper.getPathWithinApplication(request);
		return finder.getMapping(requestURI, configuration.getMappingMap());
	}

	private boolean isFilterConfigured(HttpServletRequest request) {
		return configuration.isEnabled()
				&& !getRules(mapping(request), request).isEmpty();
	}

	protected List<Rule> getRules(Optional<URLMapping> optionalMapping,
			HttpServletRequest request) {
		String mappingId = optionalMapping.map(URLMapping::getId).orElse(null);

		return configuration
				.getRule(mappingId)
				.stream()
				.filter(rule -> applyFilter(request, optionalMapping.orElse(null),
						rule)).collect(Collectors.toList());
	}

	private boolean applyFilter(HttpServletRequest request, URLMapping mapping,
			Rule rule) {
		List<RuleType> types = rule.getType();
		return types.isEmpty()
				|| types.stream().allMatch(
						type -> type.applyRateLimit(request, mapping, utils));
	}
}
