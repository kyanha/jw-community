<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>

<commons:popupHeader />

    <div id="main-body-header">
        <fmt:message key="console.directory.user.create.label.title"/>
    </div>

    <div id="main-body-content">
        <form:form id="createUser" action="${pageContext.request.contextPath}/web/console/directory/user/submit/create" method="POST" commandName="user" cssClass="form">
            <form:errors path="*" cssClass="form-errors"/>
            <c:if test="${!empty errors}">
                <span class="form-errors" style="display:block">
                    <c:forEach items="${errors}" var="error">
                        <fmt:message key="${error}"/>
                    </c:forEach>
                </span>
            </c:if>
            <fieldset>
                <legend><fmt:message key="console.directory.user.common.label.details"/></legend>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.user.common.label.username"/></label>
                    <span class="form-input"><form:input path="username" cssErrorClass="form-input-error" /> *</span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.user.common.label.firstName"/></label>
                    <span class="form-input"><form:input path="firstName" cssErrorClass="form-input-error" /> *</span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.user.common.label.lastName"/></label>
                    <span class="form-input"><form:input path="lastName" cssErrorClass="form-input-error" /></span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.user.common.label.email"/></label>
                    <span class="form-input"><form:input path="email" cssErrorClass="form-input-error" /></span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.user.common.label.password"/></label>
                    <span class="form-input"><form:password path="password" cssErrorClass="form-input-error" /> *</span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.user.common.label.confirmPassword"/></label>
                    <span class="form-input"><form:password path="confirmPassword" cssErrorClass="form-input-error" /> *</span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.user.common.label.role"/></label>
                    <span class="form-input">
                        <form:select path="roles" cssErrorClass="form-input-error" size="4" multiple="true">
                            <form:options items="${roles}" itemValue="id" itemLabel="name"/>
                        </form:select>
                    </span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.user.common.label.timeZone"/></label>
                    <span class="form-input">
                        <form:select path="timeZone" cssErrorClass="form-input-error">
                            <form:options items="${timezones}"/>
                        </form:select>
                    </span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.user.common.label.status"/></label>
                    <span class="form-input">
                        <form:select path="active" cssErrorClass="form-input-error">
                            <form:options items="${status}"/>
                        </form:select>
                    </span>
                </div>
            </fieldset>
            <fieldset>
                <legend><fmt:message key="console.directory.employment.common.label.details"/></legend>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.employment.common.label.employeeCode"/></label>
                    <span class="form-input">
                        <input id="employeeCode" name="employeeCode" type="text" value="${employeeCode}"/>
                    </span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.employment.common.label.role"/></label>
                    <span class="form-input">
                        <input id="employeeRole" name="employeeRole" type="text" value="${employeeRole}"/>
                    </span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.employment.common.label.organization"/></label>
                    <span class="form-input">
                        <select id="employeeOrganization" name="employeeOrganization">
                            <option value=""></option>
                            <c:forEach items="${organizations}" var="org">
                                <option value="${org.id}" <c:if test="${org.id eq employeeOrganization}">selected</c:if>>${org.name}</option>
                            </c:forEach>
                        </select>
                    </span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.employment.common.label.department"/></label>
                    <span class="form-input">
                        <select id="employeeDepartment" name="employeeDepartment">
                            <option value=""></option>
                        </select>
                    </span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.employment.common.label.hod"/></label>
                    <span class="form-input">
                        <select id="employeeDepartmentHod" name="employeeDepartmentHod">
                            <option value="no" <c:if test="${employeeDepartmentHod eq 'no'}">selected</c:if>><fmt:message key="console.directory.employment.common.label.hod.no"/></option>
                            <option value="yes" <c:if test="${employeeDepartmentHod eq 'yes'}">selected</c:if>><fmt:message key="console.directory.employment.common.label.hod.yes"/></option>
                        </select>
                    </span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.employment.common.label.grade"/></label>
                    <span class="form-input">
                        <select id="employeeGrade" name="employeeGrade">
                            <option value=""></option>
                        </select>
                    </span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.employment.common.label.startDate"/></label>
                    <span class="form-input">
                        <input id="employeeStartDate" name="employeeStartDate" type="text" value="${employeeStartDate}"/>
                    </span>
                </div>
                <div class="form-row">
                    <label for="field1"><fmt:message key="console.directory.employment.common.label.endDate"/></label>
                    <span class="form-input">
                        <input id="employeeEndDate" name="employeeEndDate" type="text" value="${employeeEndDate}"/>
                    </span>
                </div>
            </fieldset>
            <div class="form-buttons">
                <input class="form-button" type="button" value="<fmt:message key="general.method.label.save"/>"  onclick="validateField()"/>
                <input class="form-button" type="button" value="<fmt:message key="general.method.label.cancel"/>" onclick="closeDialog()"/>
            </div>
        </form:form>
    </div>

    <script type="text/javascript">
        $(document).ready(function(){
            loadDepartmentAndGradeOption();

            $('#employeeOrganization').change(function(){
                loadDepartmentAndGradeOption();
            });
        });

        function validateField(){
            var valid = true;
            var alertString = "";
            var idMatch = /^[0-9a-zA-Z_-]+$/.test($("#username").attr("value"));
            if(!idMatch){
                if(!idMatch){
                    alertString += '<fmt:message key="console.directory.user.error.label.usernameInvalid"/>';
                    $("#username").focus();
                    valid = false;
                }
            }
            if($("#password").val() == "" || $("#confirmPassword").val() == ""){
                if(alertString != ""){
                    alertString += '\n';
                }
                alertString += '<fmt:message key="console.directory.user.error.label.passwordNotEmpty"/>';
                valid = false;
            }else if($("#password").val() != $("#confirmPassword").val()){
                if(alertString != ""){
                    alertString += '\n';
                }
                alertString += '<fmt:message key="console.directory.user.error.label.passwordNotMatch"/>';
                valid = false;
            }

            if(valid){
                $("#createUser").submit();
            }else{
                alert(alertString);
            }
        }

        function loadDepartmentAndGradeOption(){
            if($('#employeeOrganization').val() != ""){
                var callback = {
                    success : function(data) {
                        var obj = eval('(' + data + ')');
                        var departments = obj.departments;
                        var grades = obj.grades;

                        $('#employeeDepartment option').remove();
                        $('#employeeGrade option').remove();

                        if(departments !=null && departments.length > 0){
                            for(i=0; i<departments.length; i++){
                                $('#employeeDepartment').append('<option value="' + departments[i].id + '">' + departments[i].prefix + ' ' + departments[i].name + '</option>');
                            }
                        }

                        if(grades !=null && grades.length > 0){
                            for(i=0; i<grades.length; i++){
                                $('#employeeGrade').append('<option value="' + grades[i].id + '">' + grades[i].name + '</option>');
                            }
                        }

                        if($('#employeeOrganization').val() == "${employeeOrganization}"){
                            $('#employeeDepartment option[value=${employeeDepartment}]').attr("selected", "selected");
                            $('#employeeGrade option[value=${employeeGrade}]').attr("selected", "selected");
                        }
                    }
                }
                ConnectionManager.get('${pageContext.request.contextPath}/web/json/directory/admin/user/deptAndGrade/options', callback, 'rnd=' + new Date().valueOf().toString() + '&orgId='+$('#employeeOrganization').val());
            }else{
                $('#employeeDepartment option').remove();
                $('#employeeGrade option').remove();
            }
        }

        function closeDialog() {
            if (parent && parent.PopupDialog.closeDialog) {
                parent.PopupDialog.closeDialog();
            }
            return false;
        }

        Calendar.show("employeeStartDate");
        Calendar.show("employeeEndDate");
    </script>
<commons:popupFooter />
