<#include "include/imports.ftl">

<div class="ds_wrapper">

<h1>${document.title}</h1>

<@hst.html hippohtml=document.content/>

<nav class="ds_sequential-nav" aria-label="Article navigation">
    <#if prev??>
        <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
            <@hst.link var="link" hippobean=prev/>
            <a data-navigation="sequential-previous" title="Previous section" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                <span class="ds_sequential-nav__text" data-label="previous">
                    ${prev.title?html}
                </span>
            </a>
        </div>
    </#if>
    <#if next??>
        <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
            <@hst.link var="link" hippobean=next/>
            <a data-navigation="sequential-next" title="Next section" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--right">
                <span class="ds_sequential-nav__text" data-label="next">
                    ${next.title?html}
                </span>
            </a>
        </div>
    </#if>
</nav>

</div>
