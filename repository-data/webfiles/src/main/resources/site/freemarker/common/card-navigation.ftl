<#include "include/imports.ftl">

<div class="ds_wrapper">

    <ol class="ds_category-list  ds_category-list--grid  ds_category-list--narrow">
        <#list children as child>
            <@hst.link var="link" hippobean=child/>
            <li class="ds_card  ds_card--no-padding  ds_card--has-hover">
                <article class="ds_category-item  ds_category-item--gradient">
                    <h2 class="ds_category-item__title">
                        <a data-navigation="category-item-${child?index + 1}" href="${link}" class="ds_category-item__link">${child.title}</a>
                    </h2>

                    <p class="ds_category-item__summary">
                        ${child.summary}
                    </p>
                </article>
            </li>
        </#list>
    </ol>

</div>
