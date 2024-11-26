package com.example.pygmyhippo.common;

public class EventEntrant {
    private Event event;
    private Entrant entrant;

    public EventEntrant(Event event, Entrant entrant) {
        this.event = event;
        this.entrant = entrant;
    }

    public Entrant getEntrant() {
        return entrant;
    }

    public void setEntrant(Entrant entrant) {
        this.entrant = entrant;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
