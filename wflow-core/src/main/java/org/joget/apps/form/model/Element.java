package org.joget.apps.form.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.joget.apps.form.service.FormUtil;
import org.joget.plugin.base.ExtDefaultPlugin;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.plugin.property.service.PropertyUtil;

/**
 * Base class for all elements within a form. All forms, containers and form fields must override this class.
 */
public abstract class Element extends ExtDefaultPlugin implements PropertyEditable{

    private Collection<Element> children = new ArrayList<Element>();
    private Element parent;
    private String customParameterName;
    private FormLoadBinder loadBinder;
    private FormLoadBinder optionsBinder;
    private FormStoreBinder storeBinder;
    private FormValidator validator;

    public FormLoadBinder getLoadBinder() {
        return loadBinder;
    }

    public void setLoadBinder(FormLoadBinder loadBinder) {
        this.loadBinder = loadBinder;
    }

    public FormLoadBinder getOptionsBinder() {
        return optionsBinder;
    }

    public void setOptionsBinder(FormLoadBinder optionsBinder) {
        this.optionsBinder = optionsBinder;
    }

    public FormStoreBinder getStoreBinder() {
        return storeBinder;
    }

    public void setStoreBinder(FormStoreBinder storeBinder) {
        this.storeBinder = storeBinder;
    }

    public FormValidator getValidator() {
        return validator;
    }

    public void setValidator(FormValidator validator) {
        this.validator = validator;
    }

    public Collection<Element> getChildren() {
        return children;
    }

    public void setChildren(Collection<Element> children) {
        this.children = children;

        // reset parent for each child
        if (children != null) {
            for (Element child : children) {
                child.parent = this;
            }
        }
    }

    /**
     * Returns the immediate parent for this element
     * @return null if there is no parent.
     */
    public Element getParent() {
        return parent;
    }

    /**
     * Sets the immediate parent for this element.
     * @param parent
     */
    public void setParent(Element parent) {
        this.parent = parent;
    }

    /**
     * @return If non-null, this is to be used as the HTML input name for the element
     */
    public String getCustomParameterName() {
        return customParameterName;
    }

    public void setCustomParameterName(String customParameterName) {
        this.customParameterName = customParameterName;
    }

    /**
     * Method for override to perform format data in request parameter before execute validation
     * @param formData
     * @return the formatted data.
     */
    public FormData formatDataForValidation(FormData formData) {
        //do nothing
        return formData;
    }

    /**
     * Method that retrieves loaded or submitted form data, and formats it for a store binder.
     * The formatted data is to be stored and returned in a FormRowSet.
     * @param formData
     * @return the formatted data.
     */
    public FormRowSet formatData(FormData formData) {
        FormRowSet rowSet = null;

        // get value
        String id = getPropertyString(FormUtil.PROPERTY_ID);
        if (id != null) {
            String value = FormUtil.getElementPropertyValue(this, formData);
            if (value != null) {
                // set value into Properties and FormRowSet object
                FormRow result = new FormRow();
                result.setProperty(id, value);
                rowSet = new FormRowSet();
                rowSet.add(result);
            }
        }

        return rowSet;
    }

    /**
     * Returns the primary key value for the current element.
     * Defaults to the primary key value of the form.
     */
    public String getPrimaryKeyValue(FormData formData) {
        String primaryKeyValue = null;
        // get value from form's ID field
        Element primaryElement = FormUtil.findElement(FormUtil.PROPERTY_ID, this, formData);
        if (primaryElement != null) {
            primaryKeyValue = FormUtil.getElementPropertyValue(primaryElement, formData);
        }
        if ((primaryKeyValue == null || primaryKeyValue.trim().isEmpty()) && formData != null) {
            // ID field not available, use parent primary key
            Element parent = this.getParent();
            if (parent != null) {
                primaryKeyValue = parent.getPrimaryKeyValue(formData);
            }
        }
        if ((primaryKeyValue == null || primaryKeyValue.trim().isEmpty()) && formData != null) {
            // ID field not available, use default form primary key
            primaryKeyValue = formData.getPrimaryKeyValue();
        }
        return primaryKeyValue;
    }

    /**
     * Render HTML template for UI, with option for form builder design mode
     * @param formData
     * @param includeMetaData set true to render additional meta required for the Form Builder.
     * @return
     */
    public String render(FormData formData, Boolean includeMetaData) {
        Map dataModel = FormUtil.generateDefaultTemplateDataModel(this, formData);

        // set metadata for form builder
        String elementMetaData = FormUtil.generateElementMetaData(this);
        dataModel.put("includeMetaData", includeMetaData);
        if (includeMetaData) {
            dataModel.put("elementMetaData", elementMetaData);
        }

        return renderTemplate(formData, dataModel);
    }

    /**
     * HTML template for front-end UI
     * @param formData
     * @param dataModel Model containing values to be displayed in the template.
     * @return
     */
    public abstract String renderTemplate(FormData formData, Map dataModel);

    /**
     * HTML template with errors for front-end UI
     * @param formData
     * @param dataModel Model containing values to be displayed in the template.
     * @return
     */
    public String renderErrorTemplate(FormData formData, Map dataModel) {
        return renderTemplate(formData, dataModel);
    }

    /**
     * Read-only HTML template for front-end UI (Not used at the moment)
     * @param formData
     * @param dataModel Model containing values to be displayed in the template.
     * @return
     */
    public String renderReadOnlyTemplate(FormData formData, Map dataModel) {
        // set readonly flag
        dataModel.put(FormUtil.PROPERTY_READONLY, Boolean.TRUE);

        return renderTemplate(formData, dataModel);
    }

    /**
     * Flag to indicate whether or not continue validating descendent elements.
     * @param formData
     * @return
     */
    public boolean continueValidation(FormData formData) {
        return true;
    }
    
    public String getDefaultPropertyValues(){
        return PropertyUtil.getDefaultPropertyValues(getPropertyOptions());
    }

    @Override
    public String toString() {
        return "Element {" + "className=" + getClassName() + ", properties=" + getProperties() + '}';
    }
}
