<#include "../common/include/imports.ftl">

<#include "../common/include/imports.ftl">
<@hst.include ref="container"/>

<@hst.headContribution category="footerScripts">
<script type="module" src='<@hst.webfile path="assets/scripts/global.js"/>'></script>
</@hst.headContribution>

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="item" type="scot.mygov.publishing.beans.Base" -->

<#if pageable?? && pageable.items?has_content>
    <#list pageable.items as item>
        <div class="ds_cb  ds_cb--text">
            <div class="ds_wrapper">
                <div class="ds_cb__inner">
                    <div class="ds_cb__text">
                        <article class="listing">
                            <@hst.link var="link" hippobean=item/>
                            <h2 class="listing__title"><a href="${link}">${item.title?html}</a></h2>

                            <dl class="listing__datalist  datalist">
                                <#if item.salaryband??>
                                    <div class="datalist__item">
                                        <dt class="datalist__title">Salary</dt>
                                        <dd class="datalist__value">${item.salaryband}</dd>
                                    </div>
                                </#if>
                                <#if item.location??>
                                    <div class="datalist__item">
                                        <dt class="datalist__title">Location</dt>
                                        <dd class="datalist__value">${item.location}</dd>
                                    </div>
                                </#if>
                                <#if item.hours??>
                                    <div class="datalist__item">
                                        <dt class="datalist__title">Hours</dt>
                                        <dd class="datalist__value"></dd>
                                    </div>
                                </#if>
                                <#if item.type??>
                                    <div class="datalist__item">
                                        <dt class="datalist__title">Employment type</dt>
                                        <dd class="datalist__value"></dd>
                                    </div>
                                </#if>
                                <#if item.reference??>
                                    <div class="datalist__item">
                                        <dt class="datalist__title">Reference</dt>
                                        <dd class="datalist__value">${item.reference}</dd>
                                    </div>
                                </#if>
                            </dl>

                            <div class="listing__content">
                                <#if item.summary??>
                                    ${item.summary?html}
                                </#if>
                                <a href="${link}">read more</a>
                            </div>

                            <#if item.closingDate??>
                                <div class="listing__callout">
                                    Closing date: <@fmt.formatDate value=item.closingDate.time type="date" dateStyle="long"/> at midnight
                                </div>
                            </#if>
                        </article>
                    </div>
                </div>
            </div>
        </div>

        <#if item?has_next>
            <div class="ds_cb ds_cb--divider">
                <div class="ds_wrapper">
                    <hr />
                </div>
            </div>
        </#if>
    </#list>

    <#if cparam.showPagination>
        <div class="ds_wrapper">
            <#include "../common/include/pagination.ftl">
        </div>
    </#if>

    <@hst.include ref="seo"/>
</#if>

<@hst.headContribution category="footerScripts">
    <script type="module" src='<@hst.webfile path="assets/scripts/global.js"/>'></script>
</@hst.headContribution>
