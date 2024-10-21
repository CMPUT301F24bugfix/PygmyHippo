/*
 * TODO: Decide how images are gonna be stored and set the appropriate datatype for eventPoster.
 *
 * Event Dataclass.
 * The structure of this class should be similar to the schema used for Event Documents
 * on firebase. This class contains all the information for displaying a single event in
 * the app. One Event has only one organizer associated with it, but one organizer can be
 * associated with multiple events.
 *
 * EventStatus Enum
 * Events can only have one status at any given time. Events with status that are not listed
 * in EventStatus enum should be supported and will lead to undefined behavior.
 */


package com.example.pygmyhippo;

import java.util.ArrayList;

//
public class Event {
    String eventID;
    String organizerID;
    ArrayList<Entrant> entrants;

    String location;
    String date;
    String time;

    String description;
    String cost;
    String eventPoster;

    public enum EventStatus {
        cancelled("cancelled"),
        ongoing("ongoing");

        public final String value;

        EventStatus(String value) {
            this.value = value;
        }
    }
}

