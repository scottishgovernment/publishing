<#include "include/imports.ftl">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <div class="category-upper">
        <@hst.include ref="breadcrumbs"/>

        <div class="ds_wrapper">
            <div class="ds_category-header">
                <header class="ds_category-header__header">
                    <h1 class="ds_category-header__title">${document.title}</h1>
                    <div class="ds_category-header__summary">
                        <p>${document.summary}</p>
                    </div>
                </header>
                <div class="ds_category-header__media-container">
                    <#if document.heroImage??>
                        <img class="ds_category-header__media" alt=""
                             src="<@hst.link hippobean=document.heroImage/>" />
                    </#if>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="category-lower">
    <div class="ds_wrapper">
        <ol class="ds_category-list">
            <#list services as child>
                <@hst.link var="link" hippobean=child/>
                <li class="ds_category-item">
                    <h2 class="ds_category-item__title">
                        <a data-navigation="category-item-${child?index + 1}" href="${link}" class="ds_category-item__link">
                        ${child.title}
                        </a>
                    </h2>

                </li>
            </#list>
        </ol>
    </div>

</div>
</#if>

<@hst.headContribution category="title">
    <#if document??>
    <title>${document.title} - A Trading Nation</title>
    </#if>
</@hst.headContribution>

<@hst.headContribution category="meta">
    <#if document??>
    <meta name="description" content="${document.metaDescription?html}"/>
    </#if>
</@hst.headContribution>
