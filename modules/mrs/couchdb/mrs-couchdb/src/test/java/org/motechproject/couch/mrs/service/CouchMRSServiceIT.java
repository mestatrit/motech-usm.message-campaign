package org.motechproject.couch.mrs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchMRSPerson;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchMRSServiceIT extends SpringIntegrationTest {

    @Autowired
    private CouchMRSService couchMRSService;

    private Initializer init;

    @Autowired
    @Qualifier("couchMRSDatabaseConnector")
    CouchDbConnector connector;

    @Before
    public void initialize() {
        init = new Initializer();
    }

    @Test
    public void shouldSaveAPersonAndRetrieveByExternalId() {
        CouchMRSPerson person1 = init.initializePerson1();
        couchMRSService.addPerson(person1.getExternalId(), person1.getFirstName(), person1.getLastName(),
                person1.getDateOfBirth(), person1.getGender(), person1.getAddress(), person1.getAttributes());
        List<CouchMRSPerson> personsRetrieved = couchMRSService.findByExternalId(person1.getExternalId());
        assertEquals(person1, personsRetrieved.get(0));
    }

    @Test
    public void shouldHandleNullFirstAndLastName() {
        CouchMRSPerson person2 = new CouchMRSPerson();
        person2.setExternalId("externalid");
        couchMRSService.addPerson(person2.getExternalId(), null, null, null, null, null, null);
        List<CouchMRSPerson> personsRetrieved = couchMRSService.findByExternalId(person2.getExternalId());
        assertEquals(person2, personsRetrieved.get(0));
    }

    @Test
    public void shouldHandleNullExternalId() {
        CouchMRSPerson person3 = new CouchMRSPerson();
        assertNull(person3.getExternalId());
        couchMRSService.addPerson(person3.getExternalId(), null, null, null, null, null, null);
        List<CouchMRSPerson> personsRetrieved = couchMRSService.findByExternalId(person3.getExternalId());
        assertNull(personsRetrieved);
    }

    @Test
    public void shouldUpdatePerson() {
        CouchMRSPerson person = init.initializeSecondPerson();
        couchMRSService.addPerson(person);
        assertTrue(person.getFirstName().matches("AName"));
        person.setFirstName("ANewName");
        couchMRSService.updatePerson(person);
        List<CouchMRSPerson> personsRetrieved = couchMRSService.findByExternalId(person.getExternalId());
        assertTrue(personsRetrieved.get(0).getFirstName().matches("ANewName"));
        assertTrue(person.getExternalId().matches("externalId"));
        person.setExternalId("newExternalId");
        couchMRSService.updatePerson(person);
        personsRetrieved = couchMRSService.findByExternalId(person.getExternalId());
        assertTrue(personsRetrieved.get(0).getExternalId().matches("newExternalId"));

    }

    @Test
    public void shouldRemovePerson() {
        CouchMRSPerson person = init.initializeSecondPerson();
        couchMRSService.addPerson(person);
        List<CouchMRSPerson> personsRetrieved = couchMRSService.findByExternalId(person.getExternalId());
        couchMRSService.removePerson(personsRetrieved.get(0));
        List<CouchMRSPerson> shouldBeEmpty = couchMRSService.findByExternalId(person.getExternalId());
        assertTrue(shouldBeEmpty.isEmpty());
    }

    @Test
    public void shouldFindThreePersonsWhenFindAll() {
        CouchMRSPerson first = init.initializePerson1();
        couchMRSService.addPerson(first);
        CouchMRSPerson second = init.initializeSecondPerson();
        couchMRSService.addPerson(second);
        CouchMRSPerson third = init.initializeThirdPerson();
        couchMRSService.addPerson(third);
        List<CouchMRSPerson> allPersons = couchMRSService.findAllCouchMRSPersons();
        assertEquals(3, allPersons.size());
        assertEquals(first, allPersons.get(0));
        assertEquals(second, allPersons.get(1));
        assertEquals(third, allPersons.get(2));
    }

    @Test
    public void sizeShouldBeZeroWhenFindAllNoPersons() {
        List<CouchMRSPerson> allPersons = couchMRSService.findAllCouchMRSPersons();
        assertTrue(allPersons.isEmpty());
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        couchMRSService.removeAll();
    }

}
