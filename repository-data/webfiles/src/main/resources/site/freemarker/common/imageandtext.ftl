<#include "include/imports.ftl">
<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Imageandtext" -->
<#if document??>

<div class="ds_cb  ds_cb--image-text
    <#if backgroundcolor??>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor??>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
">
    <div class="ds_wrapper">
        <@hst.manageContent hippobean=document parameterName="document" rootPath="images"/>
        <div class="ds_cb__inner">
            <div class="ds_cb__text">
                <@hst.html hippohtml=document.content/>
            </div>

            <div class="ds_cb__poster">
                <img src="<@hst.link hippobean=document.image />" alt="${document.alt?html}"/>
            </div>
        </div>
    </div>
</div>

<#elseif editMode>
    <div class="ds_wrapper">
        <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document" rootPath="images"/>
        Click to edit Image and text
    </div>
</#if>
