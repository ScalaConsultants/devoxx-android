package io.scalac.degree.connection.model;

import java.io.Serializable;
import java.util.List;

/**
 * scalac.io
 * jacek.modrakowski@scalac.io
 * 21/12/2015.
 */
public class TracksApiModel implements Serializable {
    public String content;
    public List<TrackApiModel> tracks;
}
