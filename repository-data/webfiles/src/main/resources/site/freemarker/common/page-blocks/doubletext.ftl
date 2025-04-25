<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "../include/cms-placeholders.ftl">
<#include "../macros/content-blocks.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<div class="ds_pb  ds_pb--double-text
<#if backgroundcolor?has_content> 
<#switch backgroundcolor?lower_case> 
  <#case 'secondary'>
  ds_pb--background-secondary
  <#break>
  <#case 'tertiary'>
  ds_pb--background-tertiary
  <#break>
  <#case 'theme'>
  ds_pb__theme--background-secondary
  <#break>
</#switch>
</#if>">
    <div class="ds_wrapper">
        <div class="ds_pb__inner <#if removebottompadding> ds_!_padding-bottom--0</#if>">

            <#if document1?has_content>
                <div class="ds_pb__text" style="position: relative">
                    <@renderContentBlocks document1.contentBlocks />

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-text-document" parameterName="document1" rootPath="text"/>
                </div>
            <#elseif editMode>
                <div class="ds_pb__text  cms-blank">
                    <@placeholdertext lines=7/>

                    <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document1" rootPath="text"/>
                </div>
            </#if>

            <#if document2?has_content>
                <div class="ds_pb__text">
                    <@renderContentBlocks document2.contentBlocks />

                    <@hst.manageContent hippobean=document2 documentTemplateQuery="new-text-document" parameterName="document2" rootPath="text"/>
                </div>
            <#elseif editMode>
                <div class="ds_pb__text  cms-blank">
                    <@placeholdertext lines=7/>

                    <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document2" rootPath="text"/>
                </div>
            </#if>
        </div>
    </div>
</div>
</@hst.messagesReplace>