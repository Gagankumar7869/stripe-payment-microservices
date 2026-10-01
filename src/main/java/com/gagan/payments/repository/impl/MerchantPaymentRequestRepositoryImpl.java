package com.gagan.payments.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.gagan.payments.constant.ErrorCodeEnum;
import com.gagan.payments.entity.MerchantPaymentRequestEntity;
import com.gagan.payments.exception.PaymentValidationException;
import com.gagan.payments.repository.interfaces.MerchantPaymentRequestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MerchantPaymentRequestRepositoryImpl implements MerchantPaymentRequestRepository {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String INSERT_SQL = """
			INSERT INTO merchant_payment_request
			(endUserID, merchantTxnReference, transactionRequest)
			VALUES (:endUserID, :merchantTxnReference, :transactionRequest)
			""";

	private static final String SELECT_BY_TXN_REF_SQL = """
			SELECT id, endUserID, merchantTxnReference, transactionRequest, creationDate
			FROM merchant_payment_request
			WHERE merchantTxnReference = :merchantTxnReference
			""";

	@Override
	public int saveMerchantPaymentRequest(MerchantPaymentRequestEntity merchantPaymentRequestEntity) {
		log.info("Saving merchant payment request entity : {}", merchantPaymentRequestEntity);
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("endUserID", merchantPaymentRequestEntity.getEndUserID());
		params.addValue("merchantTxnReference", merchantPaymentRequestEntity.getMerchantTxnReference());
		params.addValue("transactionRequest", merchantPaymentRequestEntity.getTransactionRequest());
		KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			namedParameterJdbcTemplate.update(INSERT_SQL, params, keyHolder, new String[]{"id"});
			Number generatedId = keyHolder.getKey();
			if (generatedId != null) {
				log.info("Merchant payment request inserted with id : {}", generatedId);
				return generatedId.intValue();
			}
			log.error("Failed to retrieve generated id after inserting merchant payment request : {}", merchantPaymentRequestEntity);
			throw new PaymentValidationException(
					ErrorCodeEnum.FAILED_TO_SAVE_PAYMENT_REQUEST.getErrorCode(),
					ErrorCodeEnum.FAILED_TO_SAVE_PAYMENT_REQUEST.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (DuplicateKeyException ex) {
			log.error("Duplicate merchantTxnReference detected : {}", merchantPaymentRequestEntity.getMerchantTxnReference(), ex);
			return -1;
		}
	}

	@Override
	public int countRequestsForUserInLastMinutes(String endUserId, int minutes) {
		Instant startTime = Instant.now().minus(minutes, ChronoUnit.MINUTES);
		String sql = """
				SELECT COUNT(*)
				FROM merchant_payment_request
				WHERE endUserID = :endUserId
				AND creationDate >= :startTime
				""";
		Map<String, Object> params = Map.of(
				"endUserId", endUserId,
				"startTime", Timestamp.from(startTime)
				);
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
		return count == null ? 0 : count;
	}

	@Override
	public MerchantPaymentRequestEntity findByMerchantTxnReference(String merchantTxnRef) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("merchantTxnReference", merchantTxnRef);

		try {
			return namedParameterJdbcTemplate.queryForObject(
					SELECT_BY_TXN_REF_SQL, 
					params,
					(rs, rowNum) -> {
						MerchantPaymentRequestEntity entity = new MerchantPaymentRequestEntity();
						entity.setId(rs.getInt("id"));
						entity.setEndUserID(rs.getString("endUserID"));
						entity.setMerchantTxnReference(rs.getString("merchantTxnReference"));
						entity.setTransactionRequest(rs.getString("transactionRequest"));
						entity.setCreationDate(rs.getTimestamp("creationDate"));
						return entity;
					}
					);
		} catch (EmptyResultDataAccessException ex) {
			log.warn("No transaction found for reference: {}", merchantTxnRef);
			return null;
		}
	}
}