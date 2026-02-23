package com.hulkhiretech.payments.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import lombok.Data;
@Data
public class HttpRequest {
	private HttpMethod httpMethod;
	private HttpHeaders httpHeaders;
	private String url;
	private Object requestData;
	

}
