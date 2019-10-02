<#include "include/imports.ftl">

<#-- @ftlvariable name="breadcrumb" type="org.onehippo.forge.breadcrumb.om.Breadcrumb" -->
<#if breadcrumbs??>
<div class="ds_wrapper">
    <nav aria-label="Breadcrumb">
        <ol class="ds_breadcrumbs">
            <#list breadcrumbs as item>
                <li class="ds_breadcrumbs__item">
                    <@hst.link var="link" link=item.link/>
                    <a data-navigation="breadcrumb-${item?index + 1}" class="ds_breadcrumbs__link" href="${link}">
                    <#if item?index == 0>
                        Home
                    <#else>
                        ${item.title?html}
                    </#if>
                    </a>
                </li>
            </#list>

            <li class="ds_breadcrumbs__item" aria-current="page">
                ${document.title?html}
            </li>
        </ol>
    </nav>
</div>
</#if>
