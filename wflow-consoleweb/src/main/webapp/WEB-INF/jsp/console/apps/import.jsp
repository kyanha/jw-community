<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<%@ page import="org.joget.workflow.util.WorkflowUtil"%>

<commons:popupHeader/>
    <div id="main-body-header">
        <fmt:message key="console.app.import.label.title"/>
    </div>
    <div id="main-body-content">

        <c:if test="${error}">
            <div class="form-error">
                <fmt:message key="console.app.import.error.pleaseCheckAndTryAgain"/>
            </div>
        </c:if>

        <c:if test="${errorList != null}">
            <div class="form-error">
                <div style="padding: 0.5em">
                <c:forEach var="error" items="${errorList}">
                    <div style="margin-bottom: 5px">${error}</div>
                </c:forEach>
                </div>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/web/console/app/import/submit" class="form" enctype="multipart/form-data">
            <div class="form-row">
                <label for="appZip" class="upload"><fmt:message key="console.app.import.label.selectFile"/></label>
                <span class="form-input">
                    <input id="appZip" type="file" name="appZip"/>
                </span>
            </div>
            <div class="form-buttons">
                <input class="form-button" type="submit" value="<fmt:message key="general.method.label.upload"/>" />
            </div>
        </form>
    </div>

<commons:popupFooter />