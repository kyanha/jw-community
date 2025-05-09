package org.joget.apps.userview.lib;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.userview.model.UserviewPermission;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.Group;
import org.joget.directory.model.Organization;
import org.joget.directory.model.User;
import org.joget.directory.model.service.ExtDirectoryManager;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONArray;
import org.springframework.context.ApplicationContext;

public class GroupPermission extends UserviewPermission implements PluginWebSupport {

    @Override
    public boolean isAuthorize() {
        User user = getCurrentUser();

        if (user != null) {
            StringTokenizer strToken = new StringTokenizer(getPropertyString("allowedGroupIds"), ";");
            while (strToken.hasMoreTokens()) {
                String groupId = (String) strToken.nextElement();
                for (Group g : (Collection<Group>) user.getGroups()) {
                    if (groupId.equals(g.getId())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public String getName() {
        return "Group Permission";
    }

    public String getVersion() {
        return "3.0.0";
    }

    public String getDescription() {
        return "";
    }

    public String getLabel() {
        return "Group";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/userview/groupPermission.json", null, true, "message/userview/groupPermission");
    }

    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("getOrgs".equals(action)) {
            try {
                JSONArray jsonArray = new JSONArray();

                ApplicationContext ac = AppUtil.getApplicationContext();
                ExtDirectoryManager directoryManager = (ExtDirectoryManager) ac.getBean("directoryManager");

                Map<String, String> empty = new HashMap<String, String>();
                empty.put("value", "");
                empty.put("label", "All");
                jsonArray.put(empty);

                Collection<Organization> orgList = directoryManager.getOrganizationsByFilter(null, null, null, null, null);

                for (Organization o : orgList) {
                    Map<String, String> option = new HashMap<String, String>();
                    option.put("value", o.getId());
                    option.put("label", o.getName());
                    jsonArray.put(option);
                }

                jsonArray.write(response.getWriter());
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "Get Organization options Error!");
            }
        } else if ("getGroups".equals(action)) {
            String orgId = request.getParameter("orgId");

            if ("null".equals(orgId) || "".equals(orgId)) {
                orgId = null;
            }

            try {
                JSONArray jsonArray = new JSONArray();

                ApplicationContext ac = AppUtil.getApplicationContext();
                ExtDirectoryManager directoryManager = (ExtDirectoryManager) ac.getBean("directoryManager");

                Collection<Group> groupList = directoryManager.getGroupsByOrganizationId(null, orgId, null, null, null, null);

                for (Group g : groupList) {
                    Map<String, String> option = new HashMap<String, String>();
                    option.put("value", g.getId());
                    option.put("label", g.getName());
                    jsonArray.put(option);
                }

                jsonArray.write(response.getWriter());
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "Get Groups options Error!");
            }
        }
    }
}
