package com.devoxx.connection.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ConferencesApiModel implements Serializable {
    public final String content;
    public final List<LinkApiModel> links;

    public ConferencesApiModel(String content, List<LinkApiModel> links) {
        this.content = content;
        this.links = links;
    }
}
