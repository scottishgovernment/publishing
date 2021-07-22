<#include "../common/include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="item" type="scot.mygov.publishing.beans.Base" -->
<#if pageable?? && pageable.items?has_content>
<div class="ds_wrapper">
    <#list pageable.items as item>
        <article class="has-edit-button">
            <@hst.link var="link" hippobean=item/>
            <h3><a href="${link}">${item.title?html}</a></h3>
            <#if item.publicationDate??>
                <p>
                    <@fmt.formatDate value=item.publicationDate.time type="both" dateStyle="medium" timeStyle="short"/>
                </p>
            </#if>
            <#if item.introduction??>
                <p>${item.introduction?html}</p>
            </#if>
        </article>
    </#list>

    <#if cparam.showPagination>
        <#include "../common/include/pagination.ftl">
    </#if>
</div>
</#if>
