package com.serenitydojo.cashback_rewards.domain.exception;

public class MerchantAlreadyRegisteredException extends RuntimeException {

    public MerchantAlreadyRegisteredException(String name) {
        super("Merchant '" + name + "' is already registered");
    }
}
