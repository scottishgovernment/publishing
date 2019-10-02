<#include "include/imports.ftl">

<div class="ds_wrapper">

    <ol class="ds_category-list">
        <#list children as child>
            <@hst.link var="link" hippobean=child/>
            <li class="ds_category-item">
                <h2 class="ds_category-item__title">
                    <a data-navigation="category-item-${child?index + 1}" href="${link}" class="ds_category-item__link">${child.title}</a>
                </h2>

                <p class="ds_category-item__summary">
                    ${child.summary}
                </p>
            </li>
        </#list>
    </ol>

</div>
