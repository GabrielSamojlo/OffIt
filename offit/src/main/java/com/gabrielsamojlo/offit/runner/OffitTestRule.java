package com.gabrielsamojlo.offit.runner;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.gabrielsamojlo.offit.utils.MockedCallExecutor;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.concurrent.Executor;

/**
 * Created by Gabriel Samojło on 08.01.2018.
 */

public class OffitTestRule extends TestWatcher {

    @Override
    protected void starting(Description description) {
        super.starting(description);

        MockedCallExecutor.getInstance().setDelegate(new Executor() {
            @Override
            public void execute(@NonNull Runnable runnable) {
                new Handler(Looper.getMainLooper()).post(runnable);
            }
        });
    }

    @Override
    protected void finished(Description description) {
        super.finished(description);

        MockedCallExecutor.getInstance().setDelegate(null);
    }
}
