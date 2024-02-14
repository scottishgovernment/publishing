<#ftl output_format="HTML">
<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<ol class="ds_category-list  ds_category-list--grid">
    <#list children as child>
        <@hst.link var="link" hippobean=child.bean/>
        <li class="ds_category-item">
            <h2 class="ds_category-item__title">
                <a href="${link}" class="ds_category-item__link">
                    ${child.bean.title}
                </a>
            </h2>

            <#if document.showSummaries>
                <p class="ds_category-item__summary">
                    ${child.bean.summary}
                </p>
            </#if>
        </li>
    </#list>
</ol>
</@hst.messagesReplace>
