package com.aspire.blog.inventory.client;

import org.springframework.stereotype.Component;

import com.aspire.blog.inventory.security.SecurityUtils;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class UserFeignClientInterceptor implements RequestInterceptor {
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER = "Bearer";

	@Override
	public void apply(RequestTemplate template) {
		SecurityUtils.getCurrentUserJWT()
				.ifPresent(s -> template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER, s)));
	}
}
