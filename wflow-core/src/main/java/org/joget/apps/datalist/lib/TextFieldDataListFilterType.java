package org.joget.apps.datalist.lib;

import java.util.HashMap;
import java.util.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListFilterQueryObject;
import org.joget.apps.datalist.model.DataListFilterTypeDefault;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.util.WorkflowUtil;

public class TextFieldDataListFilterType extends DataListFilterTypeDefault {

    public String getName() {
        return "Text Field Data List Filter Type";
    }

    public String getVersion() {
        return "3.0.0";
    }

    public String getDescription() {
        return "Data List Filter Type - Text Field";
    }

    public String getLabel() {
        return "Text Field";
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    public String getPropertyOptions() {
        return "";
    }

    public String getTemplate(DataList datalist, String name, String label) {
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        Map dataModel = new HashMap();
        dataModel.put("name", datalist.getDataListEncodedParamName(DataList.PARAMETER_FILTER_PREFIX+name));
        dataModel.put("label", label);
        dataModel.put("value", getValue(datalist, name));
        dataModel.put("contextPath", WorkflowUtil.getHttpServletRequest().getContextPath());
        return pluginManager.getPluginFreeMarkerTemplate(dataModel, getClassName(), "/templates/textFieldDataListFilterType.ftl", null);
    }

    public DataListFilterQueryObject getQueryObject(DataList datalist, String name) {
        DataListFilterQueryObject queryObject = new DataListFilterQueryObject();
        if (datalist != null && datalist.getBinder() != null && getValue(datalist, name) != null) {
            queryObject.setQuery(datalist.getBinder().getColumnName(name) + " like ?");
            queryObject.setValues(new String[]{'%' + getValue(datalist, name) + '%'});

            return queryObject;
        }
        return null;
    }
}
