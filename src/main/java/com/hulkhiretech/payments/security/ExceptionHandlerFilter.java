package com.hulkhiretech.payments.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaymentValidationException;
import com.hulkhiretech.payments.pojo.ErrorResponse;
import com.hulkhiretech.payments.util.JsonUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

	private final JsonUtil jsonUtil;

	public ExceptionHandlerFilter(JsonUtil jsonUtil) {
		this.jsonUtil = jsonUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (PaymentValidationException e) {
			handleCustomException(response, e.getHttpStatus(), e.getErrorCode(), e.getErrorMessage());
		} catch (Exception e) {
			handleCustomException(response, HttpStatus.INTERNAL_SERVER_ERROR, 
					ErrorCodeEnum.GENERIC_ERROR.getErrorCode(), ErrorCodeEnum.GENERIC_ERROR.getErrorMessage());
		}
	}

	private void handleCustomException(HttpServletResponse response, HttpStatus status, String errorCode, String errorMessage) throws IOException {
		response.setStatus(status.value());
		response.setContentType("application/json");

		ErrorResponse errorResponse = new ErrorResponse(errorCode, errorMessage);

		String jsonResponse = jsonUtil.convertObjectToJson(errorResponse);
		response.getWriter().write(jsonResponse);
	}
}