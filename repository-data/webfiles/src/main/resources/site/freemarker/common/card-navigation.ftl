<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="ds_wrapper">
    <ol class="ds_category-list  ds_category-list--grid  ds_category-list--narrow">
        <#list pinned as child>
            <@hst.link var="link" hippobean=child/>
            <li class="ds_card  ds_card--no-padding  ds_card--has-hover">
                <div class="ds_category-item  ds_category-item--card  ds_category-item--pinned">
                    <h2 class="ds_category-item__title">
                        <a data-navigation="category-item-${child?index + 1}" href="${link}" class="ds_category-item__link">
                            ${child.title}
                            <svg class="ds_icon  ds_category-item__pinned-icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#star"></use></svg>
                        </a>
                    </h2>

                    <p class="ds_category-item__summary">
                        ${child.summary}
                    </p>
                </div>
            </li>
        </#list>
        <#list unpinned as child>
            <@hst.link var="link" hippobean=child/>
            <li class="ds_card  ds_card--no-padding  ds_card--has-hover">
                <div class="ds_category-item  ds_category-item--card">
                    <h2 class="ds_category-item__title">
                        <a data-navigation="category-item-${child?index + 1}" href="${link}" class="ds_category-item__link">
                            ${child.title}
                        </a>
                    </h2>

                    <p class="ds_category-item__summary">
                        ${child.summary}
                    </p>
                </div>
            </li>
        </#list>
    </ol>
</div>
