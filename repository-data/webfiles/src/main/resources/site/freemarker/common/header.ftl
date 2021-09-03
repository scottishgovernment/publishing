<#include "include/imports.ftl">

<div class="ds_cb  ds_cb--header
    <#if backgroundcolor??>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor??>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <div class="ds_cb__text  <#if position??>ds_cb__text--${position}</#if>">
                <#switch weight>
                    <#case 'h2'>
                        <h2>${text}</h2>
                        <#break>
                    <#case 'h3'>
                        <h3>${text}</h3>
                        <#break>
                </#switch>
            </div>
        </div>
    </div>
</div>
