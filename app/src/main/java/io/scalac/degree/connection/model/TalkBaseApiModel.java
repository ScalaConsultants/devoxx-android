package io.scalac.degree.connection.model;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

import io.scalac.degree33.R;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 26/10/2015
 */
public class TalkBaseApiModel implements Serializable {
    public String title;
    public String talkType;
    public String track;
    public String id;

    public static @DrawableRes int getTrackIcon(final String trackName) {
        return R.drawable.ic_star;
    }
}
