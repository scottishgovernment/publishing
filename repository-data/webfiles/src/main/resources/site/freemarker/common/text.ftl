<#include "include/imports.ftl">

<#if document??>
<div class="ds_cb  ds_cb--text
    <#if backgroundcolor??>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor??>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <div class="ds_cb__text  <#if position??>ds_cb__text--${position}</#if>">
                <@hst.html hippohtml=document.content/>
            </div>
        </div>
    </div>
</div>

<#elseif editMode>
    <@hst.manageContent documentTemplateQuery="new-publishing-article" parameterName="document" rootPath="text"/>
    Click to edit Text
</#if>
