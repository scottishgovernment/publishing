<#if document??>
    <#assign documentUuid = document.uuid/>
<#else>
    <#assign documentUuid = ""/>
</#if>
<input id="documentUuid" type="hidden" name="uuid" value="${documentUuid}"/>
<@hst.include ref="feedback"/>
