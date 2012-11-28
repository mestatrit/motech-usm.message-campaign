package org.motechproject.couch.mrs.repository;

import java.util.Collections;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.couch.mrs.model.CouchMRSPerson;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AllCouchMRSPersonsImpl extends MotechBaseRepository<CouchMRSPerson> implements AllCouchMRSPersons {

    @Autowired
    protected AllCouchMRSPersonsImpl(@Qualifier("couchMRSDatabaseConnector") CouchDbConnector db) {
        super(CouchMRSPerson.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_externalId", map = "function(doc) { if (doc.type ==='CouchMRSPerson') { emit(doc.externalId, doc._id); }}")
    public List<CouchMRSPerson> findByExternalId(String externalId) {
        if (externalId == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_externalId").key(externalId).includeDocs(true);
        List<CouchMRSPerson> ret = db.queryView(viewQuery, CouchMRSPerson.class);
        return ret;
    }

    @Override
    public void add(CouchMRSPerson person) {
        if (person.getExternalId() == null) {
            return;
        }

        if (!findByExternalId(person.getExternalId()).isEmpty()) {
            return;
        }
        super.add((CouchMRSPerson) person);
    }

    @Override
    public void update(CouchMRSPerson person) {
        super.update((CouchMRSPerson) person);
    }

    @Override
    public void remove(CouchMRSPerson person) {
        super.remove((CouchMRSPerson) person);
    }

    @Override
    @View(name = "findAllPersons", map = "function(doc) {if (doc.type == 'CouchMRSPerson') {emit(null, doc._id);}}")
    public List<CouchMRSPerson> findAllPersons() {
        List<CouchMRSPerson> ret = queryView("findAllPersons");
        if (null == ret) {
            ret = Collections.<CouchMRSPerson> emptyList();
        }
        return ret;
    }
}
