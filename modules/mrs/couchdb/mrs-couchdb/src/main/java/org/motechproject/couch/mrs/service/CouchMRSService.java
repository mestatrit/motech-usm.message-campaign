package org.motechproject.couch.mrs.service;

import java.util.Date;
import java.util.List;

import org.motechproject.couch.mrs.model.CouchMRSPerson;
import org.motechproject.couch.mrs.model.Attribute;

/**
 * This service provides a means for other modules to perform CRUD operations on
 * the AllCouchMRSPersons entity in CouchDB.
 */
public interface CouchMRSService {

    void addPerson(CouchMRSPerson person);

    void addPerson(String externalId, String firstName, String lastName, Date dateOfBirth, String gender,
            String address, List<Attribute> attributes);

    void updatePerson(CouchMRSPerson person);

    void removePerson(CouchMRSPerson person);

    List<CouchMRSPerson> findAllCouchMRSPersons();

    List<CouchMRSPerson> findByExternalId(String externalId);

    void removeAll();

}
