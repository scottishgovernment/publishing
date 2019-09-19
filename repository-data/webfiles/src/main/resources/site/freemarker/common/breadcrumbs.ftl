<#include "include/imports.ftl">

<#-- @ftlvariable name="breadcrumb" type="org.onehippo.forge.breadcrumb.om.Breadcrumb" -->
<#if breadcrumbs??>
    <#list breadcrumbs as item>
        <@hst.link var="link" link=item.link/>
        <a href="${link}">${item.title?html}</a> > &nbsp;
    </#list>
    ${document.title?html}
</#if>

<hr>