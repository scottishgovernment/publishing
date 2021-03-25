<#include "include/imports.ftl">

<#-- @ftlvariable name="breadcrumb" type="org.onehippo.forge.breadcrumb.om.Breadcrumb" -->
<#if breadcrumbs??>
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
                        ${item.title?html}
                    </#if>
                    </a>
                </li>
            </#list>

            <li class="ds_breadcrumbs__item" aria-current="page">
                ${documentBreadcrumbItem.title?html}
            </li>
        </ol>
    </nav>
</div>


</#if>

<@hst.headContribution category="schema">
<#if breadcrumbs??>
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
                    "name": "${item.title?html}"
                }
            },
            </#list>
            {
                "@type": "ListItem",
                "position": ${breadcrumbs?size + 1},
                "item": {
                    <@hst.link var="link" link=documentBreadcrumbItem.link/>
                    "@id": "${link}",
                    "name": "${documentBreadcrumbItem.title?html}"
                }
            }
        ]
    }
</script>
</#if>
</@hst.headContribution>
