<#include "include/imports.ftl">

<#if document??>
<div class="content-block__text" style="text-align:${alignment}">
<@hst.html hippohtml=document.content/>
</div>

<#elseif editMode>
    <@hst.manageContent documentTemplateQuery="new-publishing-article" parameterName="document" rootPath="text"/>
    Click to edit Text
</#if>