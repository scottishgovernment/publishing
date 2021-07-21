<#include "include/imports.ftl">

<#if document1??>
<div class="ds_cb  ds_cb--double-text
    <#if backgroundcolor??>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor??>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <div class="ds_cb__text">
                <@hst.html hippohtml=document1.content/>
            </div>

            <#if document2??>
                <div class="ds_cb__text">
                    <@hst.html hippohtml=document2.content/>
                </div>
            </#if>
        </div>
    </div>
</div>

<#elseif editMode>
    <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document1" rootPath="text"/>
    <div class="ds_wrapper">
        Click to edit Text
    </div>
</#if>
