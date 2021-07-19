<#include "include/imports.ftl">

<#if document1??>
    <div class="content-block__text" style="text-align:${alignment1}">
        <@hst.html hippohtml=document1.content/>
    </div>

    <div class="content-block__text" style="text-align:${alignment2}">
        <#if document2??>
            <@hst.html hippohtml=document2.content/>
        </#if>
    </div>

<#elseif editMode>
    <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document1" rootPath="text"/>
    Click to edit Text
</#if>