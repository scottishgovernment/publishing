<#include "../common/include/imports.ftl">

<@hst.include ref="seo"/>

<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  ds_layout--article">
        <div class="ds_layout__header">
            <header class="ds_page-header">
                <h1 class="ds_page-header__title">${document.title}</h1>
                <dl class="ds_page-header__metadata  ds_metadata">
                    <#if document.lastUpdatedDate??>
                        <div class="ds_metadata__item">
                            <dt class="ds_metadata__key">Last updated</dt>
                            <dd class="ds_metadata__value"><@fmt.formatDate value=document.lastUpdatedDate.time type="both" pattern="d MMM yyyy"/></dd>
                        </div>
                    </#if>
                </dl>
            </header>
        </div>

        <div class="ds_layout__content">
            <article class="listing">
                <dl class="listing__datalist  datalist">
                    <#if document.salaryband??>
                        <div class="datalist__item">
                            <dt class="datalist__title">Salary</dt>
                            <dd class="datalist__value">${document.salaryband}</dd>
                        </div>
                    </#if>
                    <#if document.location??>
                        <div class="datalist__item">
                            <dt class="datalist__title">Location</dt>
                            <dd class="datalist__value">${document.location}</dd>
                        </div>
                    </#if>
                    <#if document.hours??>
                        <div class="datalist__item">
                            <dt class="datalist__title">Hours</dt>
                            <dd class="datalist__value"></dd>
                        </div>
                    </#if>
                    <#if document.type??>
                        <div class="datalist__item">
                            <dt class="datalist__title">Employment type</dt>
                            <dd class="datalist__value"></dd>
                        </div>
                    </#if>
                    <#if document.reference??>
                        <div class="datalist__item">
                            <dt class="datalist__title">Reference</dt>
                            <dd class="datalist__value">${document.reference}</dd>
                        </div>
                    </#if>
                </dl>

                <div class="listing__content">
                    <@hst.html hippohtml=document.content/>
                </div>

                <#if document.closingDate??>
                    <div class="listing__callout">
                        Closing date: <@fmt.formatDate value=document.closingDate.time type="date" dateStyle="long"/> at midnight
                    </div>
                </#if>
            </article>
        </div>
    </main>
</div>
