package com.hulkhiretech.payments.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import com.hulkhiretech.payments.repository.interfaces.ValidationRulesParamsRepository;
import com.hulkhiretech.payments.repository.interfaces.ValidationRulesRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ValidatorRuleCacheV2 {

	private final ValidationRulesRepository validationRulesRepository;
	private final ValidationRulesParamsRepository validationRulesParamsRepository;

	// Local In-Memory Cache replacements for Redis
	private List<String> validatorRulesCache = new CopyOnWriteArrayList<>();
	private Map<String, Map<String, String>> validatorParamsCache = new ConcurrentHashMap<>();

	public ValidatorRuleCacheV2(
			ValidationRulesRepository validationRulesRepository,
			ValidationRulesParamsRepository validationRulesParamsRepository) {
		this.validationRulesRepository = validationRulesRepository;
		this.validationRulesParamsRepository = validationRulesParamsRepository;
	}

	public List<String> getValidatorRules() {
		return new ArrayList<>(validatorRulesCache);
	}
	
	public Map<String, String> getValidatorParamsForRule(String ruleName) {
		return validatorParamsCache.getOrDefault(ruleName, new ConcurrentHashMap<>());
	}

	public void setValidatorRules(List<String> rules) {  
		validatorRulesCache.clear();
		if (rules != null && !rules.isEmpty()) {
			validatorRulesCache.addAll(rules);
			log.info("Updated validator rules in LOCAL cache: {}", rules);
		} else {
			log.info("No validator rules to update in LOCAL cache; cleared existing rules");
		}
	}

	public void setValidatorRuleParams(Map<String, Map<String, String>> validatorRuleParams) {
		validatorParamsCache.clear();
		if (validatorRuleParams != null && !validatorRuleParams.isEmpty()) {
			validatorParamsCache.putAll(validatorRuleParams);
			log.info("Updated params in LOCAL cache.");
		} else {
			log.info("No params to update in LOCAL cache; cleared existing params");
		}
	}

	@PostConstruct
	public void init() { 
		try {
			log.info("Initializing LOCAL Java Cache (Redis Bypassed)");
			
			List<String> dbRules = validationRulesRepository.loadActiveValidatorNamesOrderedByPriority();

			if (dbRules != null && !dbRules.isEmpty()) {
				setValidatorRules(dbRules);
				log.info("Loaded {} validator rules from DB into LOCAL cache", dbRules.size());
			} else {
				log.info("No validator rules found in DB; using empty rule set");
			}

			Map<String, Map<String, String>> validatorRuleParams = validationRulesParamsRepository.loadAllValidatorParams();
			if (validatorRuleParams == null) {
				validatorRuleParams = Map.of();
			}
			setValidatorRuleParams(validatorRuleParams);
			log.info("Loaded validator rule config entries into LOCAL cache: {}", validatorRuleParams.size());

		} catch (Exception ex) {
			log.error("Failed to load validator rules or params from DB; using empty rule set and config", ex);
		}
	}
}