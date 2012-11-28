package org.motechproject.couch.mrs.repository;

import java.util.List;

import org.motechproject.couch.mrs.model.CouchMRSPerson;

public interface AllCouchMRSPersons {

    List<CouchMRSPerson> findByExternalId(String externalId);

    void add(CouchMRSPerson person);

    void update(CouchMRSPerson person);

    void remove(CouchMRSPerson person);

    List<CouchMRSPerson> findAllPersons();
}
