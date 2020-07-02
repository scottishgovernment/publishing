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


<ol>
    <#list orgsBySector as orgAndSector>
        <li >
            <h2>${orgAndSector.sector.name}</h2>
            <p>
                ${orgAndSector.organisations?size}
                ${orgAndSector.sector.description}
            </p>

        </li>
    </#list>
</ol>

<ol>
    <#list orgsByLetter as orgAndLetter>
        <li >
            <h2>${orgAndLetter.leter}</a></h2>
            <p>
                ${orgAndLetter.organisations?size}
                <ol>
                    <#list orgAndLetter.organisations as org>
                        <li>
                            <@hst.link var="link" hippobean=org/>
                            <a href="${link}">${org.title}</a>
                        </li>
                    </#list>
                </ol>
            </p>

        </li>
    </#list>
</ol>
</#if>

<@hst.headContribution category="title">
    <#if document??>
    <title>${document.title} - A Trading Nation</title>
    </#if>
</@hst.headContribution>

<@hst.headContribution category="meta">
    <#if document??>
    <meta name="description" content=""/>
    </#if>
</@hst.headContribution>
