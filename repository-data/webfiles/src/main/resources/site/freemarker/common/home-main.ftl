<#include "include/imports.ftl">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <div class="category-upper">
        <div class="ds_wrapper">
            <div class="ds_category-header">
                <header class="ds_category-header__header">
                    <h1 class="ds_category-header__title">${document.title}</h1>
                    <div class="ds_category-header__summary">
                        <p>${document.summary}</p>
                    </div>
                </header>
                <div class="ds_category-header__media-container">
                    <img class="ds_category-header__media" src="https://tradingnation.mygov.scot/assets/images/hero/FirstLevelNavImage-x2.png" alt="" />
                </div>
            </div>
        </div>
    </div>
</div>

<div class="category-lower">
    <@hst.html hippohtml=document.prologue/>
    <div class="ds_category-list-container">
        <#include 'card-navigation.ftl'/>
    </div>
    <@hst.html hippohtml=document.epilogue/>
</div>
</#if>

<@hst.headContribution category="title">
    <#if document??>
        <title>${document.title}</title>
    </#if>
</@hst.headContribution>
