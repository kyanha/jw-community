package org.joget.apps.app.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.commons.util.LogUtil;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * DAO to load/store FormDefinition objects
 */
public class FormDefinitionDaoImpl extends AbstractAppVersionedObjectDao<FormDefinition> implements FormDefinitionDao {

    public static final String ENTITY_NAME = "FormDefinition";

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    /**
     * Retrieves FormDefinitions mapped to a table name.
     * @param tableName
     * @return
     */
    @Override
    public Collection<FormDefinition> loadFormDefinitionByTableName(String tableName) {
        // load the form definitions
        String condition = " WHERE e.tableName=?";
        Object[] params = {tableName};
        Collection<FormDefinition> results = find(getEntityName(), condition, params, null, null, 0, -1);
        return results;
    }

    public Collection<FormDefinition> getFormDefinitionList(String filterString, AppDefinition appDefinition, String sort, Boolean desc, Integer start, Integer rows) {
        String conditions = "";
        List params = new ArrayList();

        if (filterString == null) {
            filterString = "";
        }
        conditions = "and (id like ? or name like ? or tableName like ?)";
        params.add("%" + filterString + "%");
        params.add("%" + filterString + "%");
        params.add("%" + filterString + "%");

        return this.find(conditions, params.toArray(), appDefinition, sort, desc, start, rows);
    }

    public Long getFormDefinitionListCount(String filterString, AppDefinition appDefinition) {
        String conditions = "";
        List params = new ArrayList();

        if (filterString == null) {
            filterString = "";
        }
        conditions = "and (id like ? or name like ? or tableName like ?)";
        params.add("%" + filterString + "%");
        params.add("%" + filterString + "%");
        params.add("%" + filterString + "%");

        return this.count(conditions, params.toArray(), appDefinition);
    }

    @Override
    public boolean add(FormDefinition object) {
        object.setDateCreated(new Date());
        object.setDateModified(new Date());
        return super.add(object);
    }

    @Override
    public boolean update(FormDefinition object) {
        object.setDateModified(new Date());
        return super.update(object);
    }

    @Override
    public boolean delete(String id, AppDefinition appDef) {
        boolean result = false;
        try {
            FormDefinition obj = loadById(id, appDef);

            // detach from app
            if (obj != null) {
                Collection<FormDefinition> list = appDef.getFormDefinitionList();
                for (FormDefinition object : list) {
                    if (obj.getId().equals(object.getId())) {
                        list.remove(obj);
                        break;
                    }
                }
                obj.setAppDefinition(null);

                // delete obj
                super.delete(getEntityName(), obj);
                result = true;
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "");
        }
        return result;
    }

    public Collection<String> getTableNameList(AppDefinition appDefinition) {
        final AppDefinition appDef = appDefinition;
        
        Collection<String> result = (Collection<String>) this.findHibernateTemplate().execute(
                new HibernateCallback() {

                    public Object doInHibernate(Session session) throws HibernateException {
                        String query = "SELECT DISTINCT e.tableName FROM " + getEntityName() + " e where e.appId = ? and e.appVersion = ?";
                        
                        Query q = session.createQuery(query);
                        q.setParameter(0, appDef.getAppId());
                        q.setParameter(1, appDef.getVersion());
                        
                        return q.list();
                    }
                });
        return result;
    }
}
