package io.scalac.degree.connection.model;

import java.io.Serializable;

public class BreakApiModel implements Serializable {

    public String id;
    public String nameEN;
    public String nameFR;
    public RoomApiModel room;

    @Override
    public String toString() {
        return "BreakApiModel{" +
                "id='" + id + '\'' +
                ", nameEN='" + nameEN + '\'' +
                ", nameFR='" + nameFR + '\'' +
                ", room=" + room +
                '}';
    }
}
