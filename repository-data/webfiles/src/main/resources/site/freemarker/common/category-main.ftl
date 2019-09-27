<#include "include/imports.ftl">



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
            <img class="ds_category-header__image" src="https://tradingnation.mygov.scot/assets/images/hero/CountryNavImage-x2.png" alt="" />
        </div>
    </div>
</div>




<div class="category-lower"s>
    <@hst.html hippohtml=document.prologue/>

    <#if document.navigationType == "grid">
        <#include 'grid-navigation.ftl'/>
    </#if>

    <#if document.navigationType == "list">
        <#include 'list-navigation.ftl'/>
    </#if>

    <@hst.html hippohtml=document.epilogue/>
</div>

<@hst.headContribution category="title">
    <#if document??>
        <title>${document.title}</title>
    </#if>
</@hst.headContribution>
