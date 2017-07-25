package com.gabrielsamojlo.offit;

import android.content.Context;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;


class MockableCallAdapterFactory extends CallAdapter.Factory {

    private static MockableCallAdapterFactory sInstance;
    private boolean isOffItTurnedOn;
    private Context context;

    public static MockableCallAdapterFactory getInstance(Context context, boolean isOffItTurnedOn) {
        if (sInstance == null || sInstance.isOffItTurnedOn() != isOffItTurnedOn) {
            sInstance = new MockableCallAdapterFactory(context, isOffItTurnedOn);
        }

        return sInstance;
    }

    private MockableCallAdapterFactory(Context context, boolean isOffItTurnedOn) {
        this.isOffItTurnedOn = isOffItTurnedOn;
        this.context = context;
    }

    public boolean isOffItTurnedOn() {
        return isOffItTurnedOn;
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(final Type returnType, final Annotation[] annotations, final Retrofit retrofit) {
        return new CallAdapter<Object, Call<?>>() {

            private Type type;

            private Mockable[] getMockableAnnotations() {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Mockables) {
                        return ((Mockables) annotation).value();
                    }
                }

                for (Annotation annotation : annotations) {
                    if (annotation instanceof Mockable) {
                        return new Mockable[] {(Mockable) annotation};
                    }
                }

                return new Mockable[0];
            }

            @Override
            public Type responseType() {
                Type[] types = ((ParameterizedType) returnType).getActualTypeArguments();
                Type paramType = types[0];
                if (paramType instanceof WildcardType) {
                    return ((WildcardType) paramType).getUpperBounds()[0];
                }

                this.type = paramType;

                return paramType;
            }

            @Override
            public Call<?> adapt(Call<Object> call) {
                if (isOffItTurnedOn && getMockableAnnotations() != null && getMockableAnnotations().length > 0) {
                    return new MockedCall<>(type, call, context, getMockableAnnotations());
                }

                return new ExecutorCallbackCall<>(retrofit.callbackExecutor(), call);
            }
        };
    }
}
