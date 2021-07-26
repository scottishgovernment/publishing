<#include "include/imports.ftl">

<#if hstRequestContext.preview><div class="cms-display-grid"></#if>
<div class="ds_cb  ds_cb--divider
    <#if backgroundcolor?? && backgroundcolor?length gt 0>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>">
    <div class="ds_wrapper">
        <hr />
    </div>
</div>
<#if hstRequestContext.preview></div></#if>
