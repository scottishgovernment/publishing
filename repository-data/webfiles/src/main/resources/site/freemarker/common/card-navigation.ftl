<#ftl output_format="HTML">
<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<ol class="ds_category-list  ds_category-list--grid  ds_category-list--narrow">
    <#list children as child>
        <@hst.link var="link" hippobean=child.bean/>
        <li class="ds_card  ds_card--has-hover">
            <div class="ds_card__content">
                <h2 class="ds_card__title">
                    <a href="${link}" class="ds_card__link--cover">
                        ${child.bean.title}
                    </a>
                </h2>

                <#--  <#if document.showSummaries>  -->
                    <p>
                        ${child.bean.summary}
                    </p>
                <#--  </#if>  -->
            </div>
        </li>
    </#list>
</ol>
</@hst.messagesReplace>