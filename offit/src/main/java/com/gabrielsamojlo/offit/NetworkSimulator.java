package com.gabrielsamojlo.offit;

public interface NetworkSimulator {

    float getSuccessChance();
    float getErrorToFailureRatio();
    Throwable getDefaultFailureThrowable();

}
