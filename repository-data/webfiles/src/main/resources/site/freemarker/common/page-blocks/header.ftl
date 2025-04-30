<#ftl output_format="HTML">
<#include "../include/imports.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<div class="ds_pb  ds_pb--header
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
</#if>
<#if removebottompadding>  ds_!_padding-bottom--0</#if>">
    <div class="ds_wrapper">
        <div class="ds_pb__inner">
            <div class="ds_pb__text  <#if position??>ds_pb__text--${position}</#if>">
                <#switch weight>
                    <#case 'h2'>
                        <h2><#if text?has_content>${text}</#if></h2>
                        <#break>
                    <#case 'h3'>
                        <h3><#if text?has_content>${text}</#if></h3>
                        <#break>
                </#switch>
            </div>
        </div>
    </div>
</div>
</@hst.messagesReplace>
