package org.motechproject.openmrs.ws.resource.model;

import java.util.List;

public class EncounterListResult {

    private List<Encounter> results;

    public List<Encounter> getResults() {
        return results;
    }

    public void setResults(List<Encounter> results) {
        this.results = results;
    }
}
