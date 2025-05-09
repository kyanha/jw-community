package org.joget.apps.form.dao;

import java.io.IOException;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

public interface FormDataDao {

    /**
     * Loads a data row for a form based on the primary key
     * @param form
     * @param primaryKey
     * @return null if the row does not exist
     */
    public FormRow load(Form form, String primaryKey);

    /**
     * Loads a data row for a form based on the primary key. 
     * This method runs outside of a db transaction, to cater to hibernate's auto schema update requirement.
     * @param form
     * @param primaryKey
     * @return 
     */
    public FormRow loadWithoutTransaction(Form form, String primaryKey);

    /**
     * Loads a data row for an entity and table based on the primary key
     * @param entityName
     * @param tableName
     * @param primaryKey
     * @return null if the row does not exist
     */
    public FormRow load(String entityName, String tableName, String primaryKey);

    /**
     * Loads a data row for a table based on the primary key
     * @param tableName
     * @param primaryKey
     * @return null if the row does not exist
     */
    public FormRow loadByTableNameAndColumnName(String tableName, String columnName, String primaryKey);

    /**
     * Query to find a list of matching form rows.
     * @param form
     * @param condition
     * @param params
     * @param sort
     * @param desc
     * @param start
     * @param rows
     * @return
     */
    public FormRowSet find(Form form, final String condition, final Object[] params, final String sort, final Boolean desc, final Integer start, final Integer rows);

    /**
     * Query total row count for a form.
     * @param form
     * @param condition
     * @param params
     * @return
     */
    public Long count(Form form, final String condition, final Object[] params);

    /**
     * Query to find find primary key based on a field name and it's value.
     * @param form
     * @param fieldName
     * @param value
     * @return
     */
    public String findPrimaryKey(Form form, final String fieldName, final String value);

    /**
     * Saves (creates or updates) form data
     * @param form
     */
    public void saveOrUpdate(Form form, FormRowSet rowSet);

    /**
     * Saves (creates or updates) form data
     * @param form
     */
    public void saveOrUpdate(String entityName, String tableName, FormRowSet rowSet);

    /**
     * Delete form data by primary keys
     * @param form
     * @param primaryKeyValues 
     */
    public void delete(Form form, String[] primaryKeyValues);

    /**
     * Gets the generated hibernate entity name for the form
     * @param form
     * @return
     */
    public String getFormEntityName(Form form);

    /**
     * Gets the defined table name for the form
     * @param form
     */
    public String getFormTableName(Form form);

    /**
     * Returns collection of all column names to be saved
     * @param form
     * @param rowSet
     * @return
     */
    public Collection<String> getFormRowColumnNames(FormRowSet rowSet);

    /**
     * Returns collection of all columns from forms mapped to a table
     * @param tableName
     * @return
     */
    public Collection<String> getFormDefinitionColumnNames(String tableName);

    /**
     * Returns EntityName of form mapped to a table & column
     * @param tableName
     * @param columnName
     * @return
     */
    public String getEntityName(String tableName, String columnName);

    /**
     * Returns a customized hibernate configuration for the form.
     * @param config
     * @return
     * @throws DOMException
     * @throws HibernateException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws MappingException
     * @throws TransformerException
     */
    public Configuration customizeConfiguration(Configuration config) throws DOMException, HibernateException, ParserConfigurationException, SAXException, IOException, MappingException, TransformerException;
}
