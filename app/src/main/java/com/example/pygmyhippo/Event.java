package com.example.pygmyhippo;

import java.util.ArrayList;

// Event dataclass.
// The structure of this class should be similar to the schema used for Event Documents
// on firebase.
public class Event {
    String eventID;
    String organizerID;
    ArrayList<Entrant> entrants;

    String location;
    String date;
    String time;

    String description;
    String cost;
    // FIXME: unsure of datatype
    // Might change to eventPosterID depending on how we decide to handle images.
    String eventPoster;

    public static class Entrant {
        public enum EntrantStatus {
            waitlisted,
            cancelled,
            invited,
            accepted;
        };

        String accountID;
        EntrantStatus entrantStatus;
    }

    public enum EventStatus {
        cancelled,
        ongoing;
    }
}
