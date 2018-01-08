package com.gabrielsamojlo.offit;

import com.gabrielsamojlo.offit.annotations.Mockable;
import com.gabrielsamojlo.offit.annotations.Mockables;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import retrofit2.*;
import retrofit2.Call;

/**
 * Created by Gabriel Samojło on 13.12.2017.
 */

public class MockableCallAdapter<R> implements CallAdapter<R, Object> {

    private Type type;
    private Type returnType;
    private Retrofit retrofit;
    private Annotation[] annotations;
    private MockableCallAdapterFactory factory;

    public MockableCallAdapter(Type type, Retrofit retrofit, Annotation[] annotations, MockableCallAdapterFactory factory) {
        this.returnType = type;
        this.retrofit = retrofit;
        this.annotations = annotations;
        this.factory = factory;
    }

    private Mockable[] getMockableAnnotations() {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Mockables) {
                return ((Mockables) annotation).value();
            }
        }

        for (Annotation annotation : annotations) {
            if (annotation instanceof Mockable) {
                return new Mockable[]{(Mockable) annotation};
            }
        }

        return new Mockable[0];
    }

    private boolean shouldMockCall() {
        return (factory.isOffItTurnedOn() && getMockableAnnotations() != null && getMockableAnnotations().length > 0);
    }

    @Override
    public Type responseType() {
        Type rawType = ((ParameterizedType) returnType).getRawType();
        if (rawType.equals(Call.class) || rawType.equals(MockableCall.class) || rawType.equals(com.gabrielsamojlo.offit.Call.class)) {
            Type[] types = ((ParameterizedType) returnType).getActualTypeArguments();
            Type paramType = types[0];
            if (paramType instanceof WildcardType) {
                return ((WildcardType) paramType).getUpperBounds()[0];
            }

            this.type = paramType;

            return paramType;
        } else {
            CallAdapter callAdapter = retrofit.nextCallAdapter(factory, returnType, annotations);
            return callAdapter.responseType();
        }
    }

    @Override
    public Object adapt(Call<R> call) {
        Type rawType = ((ParameterizedType) returnType).getRawType();
        if (rawType.equals(Call.class) || rawType.equals(MockableCall.class) || rawType.equals(com.gabrielsamojlo.offit.Call.class)) {
            if (shouldMockCall()) {
                return new MockedCall<>(type, call, factory.getAssetManager(), getMockableAnnotations(), factory.getInterceptors());
            } else {
                return new ExecutorCallbackCall<>(retrofit.callbackExecutor(), call);
            }
        }

        CallAdapter adapter = retrofit.nextCallAdapter(factory, returnType, annotations);
        if (shouldMockCall()) {
            MockedCall<?> mockedCall = new MockedCall<>(adapter.responseType(), call, factory.getAssetManager(), getMockableAnnotations(), factory.getInterceptors());
            return adapter.adapt(mockedCall);
        } else {
            return adapter.adapt(new ExecutorCallbackCall<>(retrofit.callbackExecutor(), call));
        }

    }

}
