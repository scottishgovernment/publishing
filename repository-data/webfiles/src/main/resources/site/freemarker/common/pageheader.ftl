<#include "include/imports.ftl">
<#include "include/cms-placeholders.ftl">
<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Pageheading" -->

<div class="ds_wrapper">
    <#if document??>
        <h1>${document.title}</h1>
        <img src="<@hst.link hippobean=document.image />" alt="${document.alt?html}"/>
        <@hst.html hippohtml=document.description/>

        <@hst.manageContent hippobean=document documentTemplateQuery="new-pageheading-document" parameterName="document" rootPath="pageheadings"/>
    <#elseif editMode>
        Edit page heading
        <@hst.manageContent documentTemplateQuery="new-pageheading-document" parameterName="document" rootPath="pageheadings"/>
    </#if>
</div>
