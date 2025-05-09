package org.joget.apps.userview.lib;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.model.PackageDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListCollection;
import org.joget.apps.datalist.model.DataListQueryParam;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormService;
import org.joget.apps.userview.model.UserviewBuilderPalette;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.apps.workflow.lib.AssignmentCompleteButton;
import org.joget.apps.workflow.lib.AssignmentWithdrawButton;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.PagedList;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONArray;
import org.springframework.context.ApplicationContext;

public class InboxMenu extends UserviewMenu implements PluginWebSupport {

    public static final String PREFIX_SELECTED = "selected_";
    public static final String PROPERTY_FILTER = "appFilter";
    public static final String PROPERTY_FILTER_ALL = "all";
    public static final String PROPERTY_FILTER_PROCESS = "process";

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getLabel() {
        return "Inbox";
    }

    @Override
    public String getIcon() {
        return "/plugin/org.joget.apps.userview.lib.InboxMenu/images/grid_icon.gif";
    }

    @Override
    public String getRenderPage() {
        return null;
    }

    public String getName() {
        return "Inbox Menu";
    }

    public String getVersion() {
        return "3.0.0";
    }

    public String getDescription() {
        return "";
    }

    @Override
    public String getCategory() {
        return UserviewBuilderPalette.CATEGORY_GENERAL;
    }

    @Override
    public boolean isHomePageSupported() {
        return true;
    }

    @Override
    public String getDecoratedMenu() {
        String menuItem = null;
        boolean showRowCount = Boolean.valueOf(getPropertyString("rowCount")).booleanValue();
        if (showRowCount) {
            int rowCount = getDataTotalRowCount();

            // generate menu link
            menuItem = "<a href=\"" + getUrl() + "\" class=\"menu-link default\"><span>" + getPropertyString("label") + "</span> <span class='rowCount'>(" + rowCount + ")</span></a>";
        }
        return menuItem;
    }

    @Override
    public String getJspPage() {
        String mode = getRequestParameterString("mode");

        if ("assignment".equals(mode)) {
            setProperty("customHeader", getPropertyString(mode + "-customHeader"));
            setProperty("customFooter", getPropertyString(mode + "-customFooter"));
            setProperty("messageShowAfterComplete", getPropertyString(mode + "-messageShowAfterComplete"));
            return handleForm();
        } else {
            setProperty("customHeader", getPropertyString("list-customHeader"));
            setProperty("customFooter", getPropertyString("list-customFooter"));
            return handleList();
        }
    }

    protected String handleList() {
        viewList();

        return "userview/plugin/datalist.jsp";
    }

    protected void viewList() {
        try {
            // get data list
            DataList dataList = getDataList();
            dataList.setCheckboxPosition(DataList.CHECKBOX_POSITION_NO);
            dataList.setRows(getRows(dataList));
            dataList.setSize(getDataTotalRowCount());

            // set data list
            setProperty("dataList", dataList);
        } catch (Exception ex) {
            StringWriter out = new StringWriter();
            ex.printStackTrace(new PrintWriter(out));
            String message = ex.toString();
            message += "\r\n<pre class=\"stacktrace\">" + out.getBuffer() + "</pre>";
            setProperty("error", message);
        }
    }

    protected DataList getDataList() {
        // get datalist
        ApplicationContext ac = AppUtil.getApplicationContext();
        AppService appService = (AppService) ac.getBean("appService");
        DataListService dataListService = (DataListService) ac.getBean("dataListService");
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/userview/inboxMenuListJson.json", null, true, "message/userview/inboxMenu");
        DataList dataList = dataListService.fromJson(json);

        return dataList;
    }

    protected DataListCollection getRows(DataList dataList) {
        try {
            DataListCollection resultList = new DataListCollection();

            // determine filter
            String packageId = null;
            String processDefId = null;
            String appFilter = getPropertyString(PROPERTY_FILTER);
            if (PROPERTY_FILTER_ALL.equals(appFilter)) {
                AppDefinition appDef = AppUtil.getCurrentAppDefinition();
                if (appDef != null) {
                    PackageDefinition packageDef = appDef.getPackageDefinition();
                    if (packageDef != null) {
                        packageId = packageDef.getId();
                    }
                }
            } else if (PROPERTY_FILTER_PROCESS.equals(appFilter)) {
                String processId = getPropertyString("processId");
                AppDefinition appDef = AppUtil.getCurrentAppDefinition();
                if (appDef != null) {
                    ApplicationContext ac = AppUtil.getApplicationContext();
                    AppService appService = (AppService) ac.getBean("appService");
                    WorkflowProcess process = appService.getWorkflowProcessForApp(appDef.getId(), appDef.getVersion().toString(), processId);
                    processDefId = process.getId();
                }
            }

            DataListQueryParam param = dataList.getQueryParam(null, null);

            // get assignments
            WorkflowManager workflowManager = (WorkflowManager) WorkflowUtil.getApplicationContext().getBean("workflowManager");
            PagedList<WorkflowAssignment> assignmentList = workflowManager.getAssignmentPendingAndAcceptedList(packageId, processDefId, null, param.getSort(), param.getDesc(), param.getStart(), param.getSize());

            // set results
            resultList.addAll(assignmentList);
            return resultList;
        } catch (Exception e) {
            return null;
        }
    }

    public int getDataTotalRowCount() {
        WorkflowManager workflowManager = (WorkflowManager) WorkflowUtil.getApplicationContext().getBean("workflowManager");

        String packageId = null;
        String processDefId = null;
        String appFilter = getPropertyString(PROPERTY_FILTER);
        if (PROPERTY_FILTER_ALL.equals(appFilter)) {
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            if (appDef != null) {
                PackageDefinition packageDef = appDef.getPackageDefinition();
                if (packageDef != null) {
                    packageId = packageDef.getId();
                }
            }
        } else if (PROPERTY_FILTER_PROCESS.equals(appFilter)) {
            String processId = getPropertyString("processId");
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            if (appDef != null) {
                ApplicationContext ac = AppUtil.getApplicationContext();
                AppService appService = (AppService) ac.getBean("appService");
                WorkflowProcess process = appService.getWorkflowProcessForApp(appDef.getId(), appDef.getVersion().toString(), processId);
                processDefId = process.getId();
            }
        }
        int count = workflowManager.getAssignmentSize(packageId, processDefId, null);
        return count;
    }

    protected String handleForm() {
        if ("submit".equals(getRequestParameterString("action"))) {
            // submit form
            submitForm();
        } else {
            displayForm();

        }
        return "userview/plugin/form.jsp";
    }

    protected void displayForm() {

        String activityId = getRequestParameterString("activityId");

        ApplicationContext ac = AppUtil.getApplicationContext();
        FormService formService = (FormService) ac.getBean("formService");
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");

        Form form = null;
        WorkflowAssignment assignment = null;
        FormData formData = new FormData();

        // get assignment by activity ID if available
        if (activityId != null && !activityId.trim().isEmpty()) {
            assignment = workflowManager.getAssignment(activityId);
        }

        if (assignment != null) {
            // load assignment form
            PackageActivityForm activityForm = retrieveAssignmentForm(formData, assignment);
            form = activityForm.getForm();
            setProperty("activityForm", activityForm);
        }

        if (form != null) {
            // generate form HTML
            String formHtml = formService.retrieveFormHtml(form, formData);
            String formJson = formService.generateElementJson(form);
            setProperty("view", "formView");
            setProperty("formHtml", formHtml);
            setProperty("formJson", formJson);
            if (assignment != null) {
                setProperty("headerTitle", assignment.getProcessName() + " - " + assignment.getActivityName());
            }
        } else {
            setProperty("headerTitle", "Assignment Unavailable");
            setProperty("view", "assignmentFormUnavailable");
            setProperty("formHtml", "Assignment Unavailable");
        }
    }

    protected PackageActivityForm retrieveAssignmentForm(FormData formData, WorkflowAssignment assignment) {
        String activityId = assignment.getActivityId();
        String formUrl = addParamToUrl(getUrl(), "action", "submit");
        formUrl = addParamToUrl(formUrl, "mode", "assignment");
        formUrl = addParamToUrl(formUrl, "activityId", activityId);

        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        ApplicationContext ac = AppUtil.getApplicationContext();
        AppService appService = (AppService) ac.getBean("appService");
        PackageActivityForm activityForm = appService.viewAssignmentForm(appDef.getId(), appDef.getVersion().toString(), activityId, formData, formUrl);
        return activityForm;
    }

    protected void submitForm() {

        String activityId = getRequestParameterString("activityId");

        ApplicationContext ac = AppUtil.getApplicationContext();
        FormService formService = (FormService) ac.getBean("formService");
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");

        Form form = null;
        WorkflowAssignment assignment = null;
        FormData formData = new FormData();

        // get assignment by activity ID if available
        if (activityId != null && !activityId.trim().isEmpty()) {
            assignment = workflowManager.getAssignment(activityId);
        }

        if (assignment != null) {
            // load assignment form
            PackageActivityForm activityForm = retrieveAssignmentForm(formData, assignment);

            // submit assignment form
            form = submitAssignmentForm(formData, assignment, activityForm);
        }

        if (form != null) {
            // generate form HTML
            String formHtml = null;

            // check for validation errors
            Map<String, String> errors = formData.getFormErrors();
            int errorCount = 0;
            if (errors == null || errors.isEmpty()) {
                // render normal template
                formHtml = formService.generateElementHtml(form, formData);
            } else {
                // render error template
                formHtml = formService.generateElementErrorHtml(form, formData);
                errorCount = errors.size();
            }

            // show form
            String formJson = formService.generateElementJson(form);
            setProperty("view", "formView");
            setProperty("errorCount", errorCount);
            setProperty("formHtml", formHtml);
            setProperty("formJson", formJson);
            setProperty("redirectUrlAfterComplete", getUrl());
            if (assignment != null) {
                setProperty("headerTitle", assignment.getProcessName() + " - " + assignment.getActivityName());
            }
        } else if (assignment != null) {
            setProperty("headerTitle", assignment.getProcessName() + " - " + assignment.getActivityName());
            setProperty("errorCount", 0);
            setProperty("submitted", Boolean.TRUE);
            setProperty("redirectUrlAfterComplete", getUrl());
        } else {
            setProperty("headerTitle", "Assignment Unavailable");
            setProperty("view", "assignmentFormUnavailable");
            setProperty("formHtml", "Assignment Unavailable");
        }

    }

    protected Form submitAssignmentForm(FormData formData, WorkflowAssignment assignment, PackageActivityForm activityForm) {
        Form nextForm = null;
        ApplicationContext ac = AppUtil.getApplicationContext();
        AppService appService = (AppService) ac.getBean("appService");
        FormService formService = (FormService) ac.getBean("formService");
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
        String activityId = assignment.getActivityId();
        String processId = assignment.getProcessId();

        formData = formService.retrieveFormDataFromRequestMap(formData, getRequestParameters());

        // get form
        Form currentForm = activityForm.getForm();

        // submit form
        formData = formService.executeFormActions(currentForm, formData);

        // check for validation errors
        if (formData.getFormResult(AssignmentWithdrawButton.DEFAULT_ID) != null) {
            // withdraw assignment
            workflowManager.assignmentWithdraw(activityId);
        } else if (formData.getFormResult(AssignmentCompleteButton.DEFAULT_ID) != null) {
            // complete assignment
            Map<String, String> variableMap = AppUtil.retrieveVariableDataFromMap(getRequestParameters());
            formData = appService.completeAssignmentForm(getRequestParameterString("appId"), getRequestParameterString("appVersion"), activityId, formData, variableMap);

            Map<String, String> errors = formData.getFormErrors();
            if (!errors.isEmpty()) {
                nextForm = currentForm;
            } else if (errors.isEmpty() && activityForm.isAutoContinue()) {
                // redirect to next activity if available
                WorkflowAssignment nextActivity = workflowManager.getAssignmentByProcess(processId);
                if (nextActivity != null) {
                    PackageActivityForm nextActivityForm = retrieveAssignmentForm(formData, nextActivity);
                    if (nextActivityForm != null) {
                        nextForm = nextActivityForm.getForm();
                    }
                }
            }
        }

        if (nextForm == null) {
            setProperty("submitted", Boolean.TRUE);
        } else {
            setProperty("submitted", Boolean.FALSE);
        }

        return nextForm;

    }

    public String getPropertyOptions() {
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        String appId = appDef.getId();
        String appVersion = appDef.getVersion().toString();
        Object[] arguments = new Object[]{PROPERTY_FILTER, PROPERTY_FILTER_ALL, PROPERTY_FILTER_PROCESS, appId, appVersion};
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/userview/inboxMenu.json", arguments, true, "message/userview/inboxMenu");
        return json;
    }

    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("getProcesses".equals(action)) {
            String appId = request.getParameter("appId");
            String appVersion = request.getParameter("appVersion");
            try {
                JSONArray jsonArray = new JSONArray();

                ApplicationContext ac = AppUtil.getApplicationContext();
                AppService appService = (AppService) ac.getBean("appService");
                WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
                AppDefinition appDef = appService.getAppDefinition(appId, appVersion);
                PackageDefinition packageDefinition = appDef.getPackageDefinition();
                Long packageVersion = (packageDefinition != null) ? packageDefinition.getVersion() : new Long(1);
                Collection<WorkflowProcess> processList = workflowManager.getProcessList(appId, packageVersion.toString());

                Map<String, String> empty = new HashMap<String, String>();
                empty.put("value", "");
                empty.put("label", "");
                jsonArray.put(empty);

                for (WorkflowProcess p : processList) {
                    Map<String, String> option = new HashMap<String, String>();
                    option.put("value", p.getIdWithoutVersion());
                    option.put("label", p.getName());
                    jsonArray.put(option);
                }

                jsonArray.write(response.getWriter());
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "Get Run Process's options Error!");
            }
        }
    }

    protected String addParamToUrl(String url, String name, String value) {
        if (url.contains("?")) {
            url += "&";
        } else {
            url += "?";
        }

        return url += name + "=" + value;
    }
}
