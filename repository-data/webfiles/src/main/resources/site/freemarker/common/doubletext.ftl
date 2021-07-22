<#include "include/imports.ftl">
<#include "include/cms-placeholders.ftl">

<div class="ds_cb  ds_cb--double-text
    <#if backgroundcolor?? && backgroundcolor?length gt 0>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">

            <div class="ds_cb__text">
                <#if document1??>
                    <@hst.html hippohtml=document1.content/>
                <#elseif editMode>
                    <div class="cms-blank">
                        <@placeholdertext lines=7/>

                        <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document1" rootPath="text"/>
                    </div>
                </#if>
            </div>

            <div class="ds_cb__text">
                <#if document2??>
                    <@hst.html hippohtml=document2.content/>
                <#elseif editMode>
                    <div class="cms-blank">
                        <@placeholdertext lines=7/>

                        <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document2" rootPath="text"/>
                    </div>
                </#if>
            </div>
        </div>
    </div>
</div>
