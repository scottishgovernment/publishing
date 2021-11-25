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
                            <h2 class="listing__title">
                                <#if item.link?has_content>
                                    <a href="${item.link}">${item.title?html}</a>
                                <#else>
                                    <@hst.link var="link" hippobean=item/>
                                    <a href="${link}">${item.title?html}</a>
                                </#if>
                            </h2>

                            <dl class="listing__datalist  datalist">
                                <#if item.location??>
                                    <div class="datalist__item">
                                        <dt class="datalist__title">Location</dt>
                                        <dd class="datalist__value">${item.location}</dd>
                                    </div>
                                </#if>
                                <#if item.salaryband??>
                                    <div class="datalist__item">
                                        <dt class="datalist__title">Salary</dt>
                                        <dd class="datalist__value">${item.salaryband}</dd>
                                    </div>
                                </#if>
                                <#if item.closingDate??>
                                    <div class="datalist__item">
                                        <dt class="datalist__title">Closing date</dt>
                                        <dd class="datalist__value"><@fmt.formatDate value=item.closingDate.time type="date" dateStyle="long"/> at midnight</dd>
                                    </div>
                                </#if>
                                <#if item.reference??>
                                    <div class="datalist__item">
                                        <dt class="datalist__title">Reference</dt>
                                        <dd class="datalist__value">${item.reference}</dd>
                                    </div>
                                </#if>
                            </dl>

                            <#if item.link?has_content>
                                <a href="${item.link}">Apply for this role</a>
                            <#else>
                                <@hst.link var="link" hippobean=item/>
                                <a href="${link}">More about this role</a>
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
