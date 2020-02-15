package com.nikhil.tests.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.nikhil.ratelimit.constants.RateLimitConstants;
import com.nikhil.ratelimit.processor.RateLimitProcessor;
import com.nikhil.ratelimit.processor.RedisRateLimitProcessor;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RedisApplicationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ApplicationContext context;

	@Test
	public void test1RedisRateLimiter() {
		RateLimitProcessor rateLimiter = context.getBean(RateLimitProcessor.class);
		assertTrue("RedisRateLimiter", rateLimiter instanceof RedisRateLimitProcessor);
	}

	@Test
	public void test2DevopersAPILimitPositiveReqForUser1() {
		HttpHeaders reqHeaders =  new HttpHeaders();
		reqHeaders.set("user", "user1");
		ResponseEntity<String> response = this.restTemplate.exchange("/api/v1/developers",
				HttpMethod.GET,new HttpEntity<String>(null, reqHeaders), String.class);
		HttpHeaders headers = response.getHeaders();
		assertHeaders(headers, "rate_limit_api_v1_developers_user1",
				false, false);
		assertEquals(OK, response.getStatusCode());
	}
	
	
	@Test
	public void test3OrganizationsAPILimitPositiveReqForUser2() {
		HttpHeaders reqHeaders =  new HttpHeaders();
		reqHeaders.set("user", "user2");
		ResponseEntity<String> response = this.restTemplate.exchange("/api/v1/organizations",
				HttpMethod.GET,new HttpEntity<String>(null, reqHeaders), String.class);
		HttpHeaders headers = response.getHeaders();
		assertHeaders(headers, "rate_limit_api_v1_organizations_user2",
				false, false);
		assertEquals(OK, response.getStatusCode());
	}
	
	@Test
	public void test4OrganizationsAPILimitPositiveReqForUser1() {
		HttpHeaders reqHeaders =  new HttpHeaders();
		reqHeaders.set("user", "user1");
		ResponseEntity<String> response = this.restTemplate.exchange("/api/v1/organizations",
				HttpMethod.GET,new HttpEntity<String>(null, reqHeaders), String.class);
		HttpHeaders headers = response.getHeaders();
		assertHeaders(headers, "rate_limit_api_v1_organizations_user1",
				false, false);
		assertEquals(OK, response.getStatusCode());
	}
	
	
	@Test
	public void test5DevopersAPILimitPositiveReqForUser2() {
		HttpHeaders reqHeaders =  new HttpHeaders();
		reqHeaders.set("user", "user2");
		ResponseEntity<String> response = this.restTemplate.exchange("/api/v1/developers",
				HttpMethod.GET,new HttpEntity<String>(null, reqHeaders), String.class);
		HttpHeaders headers = response.getHeaders();
		assertHeaders(headers, "rate_limit_api_v1_developers_user2",
				false, false);
		assertEquals(OK, response.getStatusCode());
	}
	
	@Test
	public void test6DefaultAPILimitPositiveReqForUnknown() {
		HttpHeaders reqHeaders =  new HttpHeaders();
		reqHeaders.set("user", "unknown");
		ResponseEntity<String> response = this.restTemplate.exchange("/api/v3/developers",
				HttpMethod.GET,new HttpEntity<String>(null, reqHeaders), String.class);
		HttpHeaders headers = response.getHeaders();
		assertHeaders(headers, "rate_limit_unknown",
				false, false);
		assertEquals(OK, response.getStatusCode());
	}

	@Test
	public void test7DevopersAPILimitNegativeReqForUser1() throws InterruptedException {
		HttpHeaders reqHeaders =  new HttpHeaders();
		reqHeaders.set("user", "user1");
		ResponseEntity<String> response = this.restTemplate.exchange("/api/v1/developers",
				HttpMethod.GET,new HttpEntity<String>(null, reqHeaders), String.class);
		HttpHeaders headers = response.getHeaders();
		String key = "rate_limit_api_v1_developers_user1";
		assertHeaders(headers, key, false, false);
		assertEquals(OK, response.getStatusCode());

		for (int i = 0; i < 4; i++) {
			System.out.println("Calling RestAPI /api/v1/developers times"+i);
			response = this.restTemplate.exchange("/api/v1/developers",
					HttpMethod.GET,new HttpEntity<String>(null, reqHeaders), String.class);
		}
		System.out.println("Checking too many request");
		assertEquals(TOO_MANY_REQUESTS, response.getStatusCode());
	}


	private void assertHeaders(HttpHeaders headers, String key,
			boolean nullable, boolean quotaHeaders) {
		String limit = headers.getFirst(RateLimitConstants.LIMIT + key);
		String remaining = headers.getFirst(RateLimitConstants.REMAINING + key);

		if (nullable) {

			assertNull(limit);
			assertNull(remaining);

		} else {

			assertNotNull(limit);
			assertNotNull(remaining);

		}
	}
}
