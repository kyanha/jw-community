<div class="form-cell" ${elementMetaData!}>
    <label class="label">${element.properties.label} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
    <div class="form-fileupload">
    <#if element.properties.readonly! != 'true'>
        <input id="${elementParamName!}" name="${elementParamName!}" type="file" size="${element.properties.size!}" <#if error??>class="form-error-cell"</#if> />
    </#if>
    <#if value??>
        <span class="form-fileupload-value">
            <a href="${request.contextPath}${filePath}">${value}</a>
            <#if value != '' && element.properties.readonly! != 'true'>
                <input type="checkbox" name="${elementParamName!}_remove" /> <span style="font-size:smaller">@@form.fileupload.remove@@</span>
            </#if>
        </span>
    </#if>
    </div>
</div>
