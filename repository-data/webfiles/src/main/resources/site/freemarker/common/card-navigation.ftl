<#ftl output_format="HTML">
<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<ul class="ds_card-grid  ds_card-grid--narrow  ds_card-grid--min-height  ds_card-grid--medium-2">
    <#list children as child>
        <@hst.link var="link" hippobean=child.bean/>
        <li class="ds_card  ds_card--navigation">
            <div class="ds_card__content">
                <div class="ds_card__content-header">
                    <h2 class="ds_card__title">
                        <a href="${link}" class="ds_card__link  ds_card__link--cover">
                            ${child.bean.title}
                        </a>
                    </h2>
                </div>
                <div class="ds_card__content-main">
                <#--  <#if document.showSummaries>  -->
                    <p class="ds_category-item__summary">
                        ${child.bean.summary}
                    </p>
                <#--  </#if>  -->
                </div>
            </div>
        </li>
    </#list>
</ul>
</@hst.messagesReplace>
