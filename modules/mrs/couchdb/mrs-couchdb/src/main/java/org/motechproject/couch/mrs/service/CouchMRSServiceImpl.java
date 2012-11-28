package org.motechproject.couch.mrs.service;

import java.util.Date;
import java.util.List;

import org.motechproject.couch.mrs.model.CouchMRSPerson;
import org.motechproject.couch.mrs.model.Attribute;
import org.motechproject.couch.mrs.repository.AllCouchMRSPersons;
import org.motechproject.couch.mrs.repository.AllCouchMRSPersonsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouchMRSServiceImpl implements CouchMRSService {

    @Autowired
    private AllCouchMRSPersons allCouchMRSPersons;

    @Override
    public void addPerson(String externalId, String firstName, String lastName, Date dateOfBirth, String gender,
            String address, List<Attribute> attributes) {
        CouchMRSPerson person = new CouchMRSPerson();
        person.setExternalId(externalId);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setDateOfBirth(dateOfBirth);
        person.setGender(gender);
        person.setAddress(address);
        person.setAttributes(attributes);
        allCouchMRSPersons.add(person);
    }

    @Override
    public void addPerson(CouchMRSPerson person) {
        allCouchMRSPersons.add(person);
    }

    @Override
    public void updatePerson(CouchMRSPerson person) {
        allCouchMRSPersons.update(person);
    }

    @Override
    public void removePerson(CouchMRSPerson person) {
        allCouchMRSPersons.remove(person);

    }

    @Override
    public List<CouchMRSPerson> findAllCouchMRSPersons() {
        return allCouchMRSPersons.findAllPersons();
    }

    @Override
    public List<CouchMRSPerson> findByExternalId(String externalId) {
        List<CouchMRSPerson> found = allCouchMRSPersons.findByExternalId(externalId);
        return found;
    }

    @Override
    public void removeAll() {
        ((AllCouchMRSPersonsImpl) allCouchMRSPersons).removeAll();

    }

}