<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<#include "../common/macros/lang-attributes.ftl">

<#if document??>
    <div class="example-frame__intro">
        <h1 class="visually-hidden">
            ${document.title}
        </h1>
    </div>

    <div class="example-frame__content">
        <#if document.dswrapper>
            <div class="ds_wrapper">
                ${document.code?no_esc}
            </div>
        <#else>
            ${document.code?no_esc}
        </#if>
    </div>

    <#if document.includedsscript??>
        <#assign scriptName="ds-example">
        <#include '../common/scripts.ftl'/>
    </#if>
</#if>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Article"/>
</@hst.headContribution>

<@hst.headContribution category="resourcehints">
    <#if nextlink??>
        <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>
