<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#assign variables = hstRequestContext.getAttribute("variables")/>

<#-- @ftlvariable name="breadcrumb" type="org.onehippo.forge.breadcrumb.om.Breadcrumb" -->
<#if breadcrumbs??>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<!--noindex-->
<div class="ds_wrapper">
    <nav aria-label="Breadcrumb">
        <ol class="ds_breadcrumbs">
            <#list breadcrumbs as item>
                <li class="ds_breadcrumbs__item">
                    <@hst.link var="link" link=item.link/>
                    <a class="ds_breadcrumbs__link" href="${link}">
                    <#if item?index == 0>
                        Home
                    <#else>
                        ${item.title}
                    </#if>
                    </a>
                </li>
            </#list>

            <li class="ds_breadcrumbs__item" aria-current="page">
                ${documentBreadcrumbItem.title}
            </li>
        </ol>
    </nav>
</div>
<!--endnoindex-->
</@hst.messagesReplace>
</#if>

<@hst.headContribution category="schema">
<#if breadcrumbs??>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<script type="application/ld+json">
    {
        "@context": "http://schema.org",
        "@type": "BreadcrumbList",
        "itemListElement": [
            <#list breadcrumbs as item>
            <@hst.link var="link" link=item.link/>
            {
                "@type": "ListItem",
                "position": ${item?index + 1},
                "item": {
                    "@id": "${link}",
                    "name": "<#if item.title??>${item.title?json_string}</#if>"
                }
            },
            </#list>
            {
                "@type": "ListItem",
                "position": ${breadcrumbs?size + 1},
                "item": {
                    <@hst.link var="link" link=documentBreadcrumbItem.link/>
                    "@id": "${link}",
                    "name": "${documentBreadcrumbItem.title?json_string}"
                }
            }
        ]
    }
</script>
</@hst.messagesReplace>
</#if>
</@hst.headContribution>
