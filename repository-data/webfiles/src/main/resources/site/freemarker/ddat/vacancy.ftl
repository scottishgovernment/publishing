<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">

<@hst.include ref="seo"/>

<main id="main-content">
    <div class="ds_cb  ds_cb--page-title">
        <div class="ds_wrapper">
            <div class="ds_cb__inner  ds_!_padding-top--9  ds_!_padding-bottom--4">
                <div class="ds_cb__text  ds_cb__content">
                    <header class="ds_page-header">
                        <h1 class="ds_page-header__title">${document.title}</h1>
                    </header>
                </div>
            </div>
        </div>
    </div>

    <div class="ds_cb  ds_cb--text  ds_cb--bg-grey">
        <div class="ds_wrapper">
            <div class="ds_cb__inner  ds_!_padding-top--4  ds_!_padding-bottom--3">
                <div class="ds_cb__text  ds_cb__content">
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
                        <#if document.closingDate??>
                            <div class="datalist__item">
                                <dt class="datalist__title">Closing date</dt>
                                <dd class="datalist__value"><@fmt.formatDate value=document.closingDate.time type="date" dateStyle="long"/> at midnight</dd>
                            </div>
                        </#if>
                    </dl>
                </div>
            </div>
        </div>
    </div>

    <div class="ds_cb  ds_cb--text">
        <div class="ds_wrapper">
            <div class="ds_cb__inner">
                <div class="ds_cb__text  ds_cb__content">
                    <div class="listing__content">
                        <@hst.html hippohtml=document.content/>
                    </div>

                    <#if document.link?has_content>
                        <p>
                            <a href="${document.link}">Apply for this role</a>
                        </p>
                    </#if>
                </div>
            </div>
        </div>
    </div>
</main>
