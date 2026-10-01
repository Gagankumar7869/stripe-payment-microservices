package com.gagan.payments.constant;

import java.util.Optional;

import com.gagan.payments.service.impl.businessvalidator.DuplicateTxnValidator;
import com.gagan.payments.service.impl.businessvalidator.PaymentAttemptThresholdValidator;
import com.gagan.payments.service.interfaces.BusinessValidator;

public enum ValidatorRuleEnum {
    DUPLICATE_TXN_RULE("DUPLICATE_TXN_RULE", DuplicateTxnValidator.class),
    PAYMENT_ATTEMPT_THRESHOLD_RULE("PAYMENT_ATTEMPT_THRESHOLD_RULE", PaymentAttemptThresholdValidator.class);

    private final String ruleName;
    private final Class<? extends BusinessValidator> validatorClass;

    ValidatorRuleEnum(String ruleName, Class<? extends BusinessValidator> validatorClass) {
        this.ruleName = ruleName;
        this.validatorClass = validatorClass;
    }

    public String getRuleName() {
        return ruleName;
    }

    public Class<? extends BusinessValidator> getValidatorClass() {
        return validatorClass;
    }

    public static Optional<Class<? extends BusinessValidator>> getValidatorClassByRule(String ruleName) {
        if (ruleName == null) {
            return Optional.empty();
        }
        for (ValidatorRuleEnum v : values()) {
            if (v.ruleName.equals(ruleName)) {
                return Optional.of(v.getValidatorClass());
            }
        }
        return Optional.empty();
    }
}