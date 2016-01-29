package com.devoxx.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.EnumMap;

@EBean(scope = EBean.Scope.Singleton)
public class FontUtils {

    @RootContext
    Context context;

    public enum Font {
        REGULAR("fonts/pirulen_rg.ttf");

        private final String path;

        Font(String fontPath) {
            path = fontPath;
        }

        public String getPath() {
            return path;
        }
    }

    private EnumMap<Font, Typeface> typefaces = new EnumMap<>(Font.class);

    public void applyTypeface(ViewGroup viewGroup, Font font) {
        final Typeface typeface;
        if (typefaces.containsKey(font)) {
            typeface = typefaces.get(font);
        } else {
            typeface = loadTypefaceFromAssets(font.getPath());
            typefaces.put(font, typeface);
        }

        final int size = viewGroup.getChildCount();
        for (int i = 0; i < size; i++) {
            final View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                applyTypeface((ViewGroup) child, font);
            } else if (child instanceof TextView) {
                applyTypeface((TextView) child, font);
            }
        }
    }

    public void applyTypeface(TextView textView, Font font) {
        final Typeface typeface;
        if (typefaces.containsKey(font)) {
            typeface = typefaces.get(font);
        } else {
            typeface = loadTypefaceFromAssets(font.getPath());
            typefaces.put(font, typeface);
        }
        textView.setTypeface(typeface);
    }

    private Typeface loadTypefaceFromAssets(String path) {
        return Typeface.createFromAsset(context.getAssets(), path);
    }
}
