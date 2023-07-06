<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<#include "../common/macros/lang-attributes.ftl">
<!--noindex-->
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

    <#if document.script?has_content>
        <@hst.headContribution category="footerScripts">
            <script type="module" src='<@hst.webfile path="assets/scripts/${document.script}.js"/>'></script>
        </@hst.headContribution>

        <@hst.headContribution category="footerScripts">
            <script nomodule="true" src='<@hst.webfile path="assets/scripts/${document.script}.es5.js"/>'></script>
        </@hst.headContribution>
    </#if>

    <#if document.includedsscript>
        <#assign scriptName="ds-example">
        <#assign noGlobal=true>
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
<!--endnoindex-->
