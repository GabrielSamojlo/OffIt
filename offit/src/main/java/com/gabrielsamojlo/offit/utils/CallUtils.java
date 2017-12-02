package com.gabrielsamojlo.offit.utils;

import android.content.Context;
import android.util.Log;

import com.gabrielsamojlo.offit.annotations.Mockable;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Gabriel Samojło on 02.12.2017.
 */

public class CallUtils {

    public static String getJsonFromAssetsPath(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    public static Mockable getDefaultAnnotation(Mockable[] annotations) {
        if (annotations != null && annotations.length > 0) {
            return annotations[0];
        }

        else throw new NullPointerException("Mockable or Mockables annotation not found! (null or empty list)");
    }

    public static Mockable getAnnotationByTag(String tag, Mockable[] annotations) {
        for (Mockable mockable : annotations) {
            if (mockable.tag().equals(tag)) {
                return mockable;
            }
        }

        Log.e("OffIt", "Annotation with tag " + tag + " was not found, using default one");
        return getDefaultAnnotation(annotations);
    }

    public static Response<?> getSuccessResponse(String json, Type type, okhttp3.Response response) {
        return Response.success(new Gson().fromJson(json, type), response);
    }

    public static Response<?> getErrorResponse(String json, okhttp3.Response response) {
        return Response.error(ResponseBody.create(MediaType.parse("application/json"), json), response);
    }

}
