package com.gabrielsamojlo.offit;

public class DefaultNetworkSimulator implements NetworkSimulator {
    @Override
    public float getSuccessChance() {
        return 0.5f;
    }

    @Override
    public float getErrorToFailureRatio() {
        return 0.5f;
    }

    @Override
    public Throwable getDefaultFailureThrowable() {
        return new Throwable("OffIt decided to make your call fail. Im not even sorry.");
    }
}
