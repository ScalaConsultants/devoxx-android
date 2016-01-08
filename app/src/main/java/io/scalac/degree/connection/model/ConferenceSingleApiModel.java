package io.scalac.degree.connection.model;

import java.io.Serializable;
import java.util.List;

public class ConferenceSingleApiModel implements Serializable {
    public String label;
    public String localisation;
    public String eventCode;
    public List<LinkApiModel> links;
    public List<String> locale;
}
