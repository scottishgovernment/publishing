<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="ds_wrapper">
    <ol class="ds_category-list">
        <#list children as child>
            <@hst.link var="link" hippobean=child.bean/>
            <li class="ds_category-item  <#if child.pinned>ds_category-item--pinned</#if>">
                <h2 class="ds_category-item__title">
                    <a data-navigation="category-item-${child?index + 1}" href="${link}" class="ds_category-item__link">
                        ${child.bean.title}
                        <#if child.pinned>
                            <svg class="ds_icon  ds_category-item__pinned-icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#star"></use></svg>
                        </#if>
                    </a>
                </h2>

                <p class="ds_category-item__summary">
                    ${child.bean.summary}
                </p>
            </li>
        </#list>
    </ol>
</div>
