package io.scalac.degree.connection.model;

import java.io.Serializable;

public class SpeakerBaseApiModel implements Serializable {
    public String uuid;
    public String firstName;
    public String lastName;
    public String avatarURL;

    public String getName() {
        return String.format("%s %s", firstName, lastName);
    }
}
