<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#include "imports.ftl">
<#if pageable??>
    <@hst.setBundle basename="essentials.pagination"/>

    <nav id="pagination" class="ds_pagination" aria-label="Search pages">
        <div class="ds_pagination__load-more">
            <button class="ds_button">Load more</button>
        </div>

        <ul class="ds_pagination__list">
            <#if pageable.totalPages gt 1>
                <#list pageable.pageNumbersArray as pageNr>
                    <@hst.renderURL var="pageUrl">
                        <@hst.param name="page" value="${pageNr}"/>
                    </@hst.renderURL>
                    <#if (pageNr_index==0 && pageable.previous)>
                        <@hst.renderURL var="pageUrlPrevious">
                            <@hst.param name="page" value="${pageable.previousPage}"/>
                        </@hst.renderURL>
                        <li class="ds_pagination__item">
                            <a class="ds_pagination__link  ds_pagination__link--text" href="${pageUrlPrevious}"><@fmt.message key="page.previous" var="prev"/>${prev?html}</a>
                        </li>
                    </#if>
                    <#if pageable.currentPage == pageNr>
                        <li class="ds_pagination__item" aria-current="page">
                            <span class="ds_pagination__link  ds_current">${pageNr}</span>
                        </li>
                    <#else>
                        <li class="ds_pagination__item ">
                            <a class="ds_pagination__link" href="${pageUrl}">${pageNr}</a>
                        </li>
                    </#if>

                    <#if !pageNr_has_next && pageable.next>
                        <@hst.renderURL var="pageUrlNext">
                            <@hst.param name="page" value="${pageable.nextPage}"/>
                        </@hst.renderURL>
                        <li class="ds_pagination__item ">
                            <a class="ds_pagination__link  ds_pagination__link--text" href="${pageUrlNext}"><@fmt.message key="page.next" var="next"/>${next?html}</a>
                        </li>
                    </#if>
                </#list>
            </#if>
        </ul>
    </nav>
</#if>
