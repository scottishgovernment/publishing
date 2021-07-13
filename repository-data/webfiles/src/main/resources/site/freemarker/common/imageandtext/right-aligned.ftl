<#include "../include/imports.ftl">
<p>Right-aligned Template Variant</p>
<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Imageandtext" -->
<#if document??>
<div>
    <p>this is the standard template</p>
    <a href="<@hst.link hippobean=document.link />">
        <figure>
            <@hst.manageContent hippobean=document parameterName="document" rootPath="images"/>
            <img src="<@hst.link hippobean=document.image />" alt="${document.alt?html}"/>
        </figure>
    </a>
    <div>
        <@hst.html hippohtml=document.content/>
    </div>
</div>
<#elseif editMode>
EDITMODE
<div>
    <figure>
        <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document" rootPath="images"/>
        Click to edit Image and text
    </figure>
</div>
</#if>
