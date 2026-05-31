package com.serenitydojo.cashback_rewards.domain.exception;

public class MerchantNotFoundException extends RuntimeException {

    public MerchantNotFoundException(String name) {
        super("Merchant '" + name + "' is not registered");
    }
}
