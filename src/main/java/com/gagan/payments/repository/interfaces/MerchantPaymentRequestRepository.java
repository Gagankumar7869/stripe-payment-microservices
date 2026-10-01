package com.gagan.payments.repository.interfaces;

import com.gagan.payments.entity.MerchantPaymentRequestEntity;

public interface MerchantPaymentRequestRepository {
    
    public int saveMerchantPaymentRequest(MerchantPaymentRequestEntity merchantPaymentRequestEntity);
    
    public int countRequestsForUserInLastMinutes(String endUserId, int minutes);

    public MerchantPaymentRequestEntity findByMerchantTxnReference(String merchantTxnRef);

}