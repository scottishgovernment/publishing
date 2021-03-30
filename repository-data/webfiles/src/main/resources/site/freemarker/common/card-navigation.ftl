<#ftl output_format="HTML">
<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<ol class="ds_category-list  ds_category-list--grid  ds_category-list--narrow">
    <#list children as child>
        <@hst.link var="link" hippobean=child.bean/>
        <li class="ds_card  ds_card--no-padding  ds_card--has-hover">
            <div class="ds_category-item  ds_category-item--card">
                <h2 class="ds_category-item__title">
                    <a href="${link}" class="ds_category-item__link">
                        ${child.bean.title}
                    </a>
                </h2>

                <#--  <#if document.showSummaries>  -->
                    <p class="ds_category-item__summary">
                        ${child.bean.summary}
                    </p>
                <#--  </#if>  -->
            </div>
        </li>
    </#list>
</ol>
