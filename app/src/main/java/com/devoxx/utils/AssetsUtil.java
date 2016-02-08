package com.devoxx.utils;

import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@EBean
public class AssetsUtil {

    @RootContext
    Context context;

    public String loadStringFromAssets(final String path) {
        return loadString(path);
    }

    private String loadString(final String path) {
        final String result;
        try {
            final StringBuilder buf = new StringBuilder();
            final InputStream json = context.getAssets().open(path);
            final BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();

            result = buf.toString();
        } catch (final IOException ignored) {
            throw new RuntimeException("No json found!");
        }
        return result;
    }
}
