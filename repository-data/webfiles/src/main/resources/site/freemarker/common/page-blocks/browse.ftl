<#ftl output_format="HTML">
<div class="ds_pb  
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
</#if> ">
    <div class="ds_pb--browse <#if removebottompadding> ds_!_padding-bottom--0</#if>">    
        <div class="ds_wrapper">
            <#if navigationType == "image-card">
                <#include '../card-navigation--image.ftl'/>
            </#if>
            <#if navigationType == "card">
                <#include '../card-navigation.ftl'/>
            </#if>
            <#if navigationType == "grid">
                <#include '../grid-navigation.ftl'/>
            </#if>
            <#if navigationType == "list">
                <#include '../list-navigation.ftl'/>
            </#if>
        </div>
    </div>
</div>
