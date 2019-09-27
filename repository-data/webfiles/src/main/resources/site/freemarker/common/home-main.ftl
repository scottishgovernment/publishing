<#include "include/imports.ftl">

<div class="category-upper">
    <div class="ds_wrapper">
        <div class="ds_category-header">
            <header class="ds_category-header__header">
                <h1 class="ds_category-header__title">${document.title}</h1>
                <div class="ds_category-header__summary">
                    <p>${document.summary}</p>
                </div>
            </header>
            <#--  <img class="ds_category-header__image" src="/assets/images/examples/category-header-design-standards.svg" alt="" />  -->
        </div>
    </div>
</div>

<div class="category-lower">
    <@hst.html hippohtml=document.prologue/>
    <#include 'card-navigation.ftl'/>
    <@hst.html hippohtml=document.epilogue/>
</div>

<@hst.headContribution category="title">
    <#if document??>
        <title>${document.title}</title>
    </#if>
</@hst.headContribution>
