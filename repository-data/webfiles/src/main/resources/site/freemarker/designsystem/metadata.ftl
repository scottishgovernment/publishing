<#ftl output_format="HTML">

<#if document.updateHistory?has_content>

<#-- process update history -->
<#assign sortedUpdateHistory = document.updateHistory?sort_by('lastUpdated') />
<#assign firstPublished = sortedUpdateHistory?first />
<#assign remainingUpdates = sortedUpdateHistory[1..]?reverse />

<dl <@revertlang document /> class="ds_page-header__metadata  ds_metadata">
    <#if remainingUpdates?size gt 0>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">Last updated</dt>
            <dd class="ds_metadata__value"><@fmt.formatDate value=remainingUpdates[0].lastUpdated.time type="both" pattern="d MMMM yyyy"/> - <a href="#full-history">see all updates</a></dd>
        </div>
    <#else>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">Published</dt>
            <dd class="ds_metadata__value"><strong id="sg-meta__publication-date"><@fmt.formatDate value=firstPublished.lastUpdated.time type="both" pattern="d MMMM yyyy"/></strong></dd>
        </div>
    </#if>
</dl>
</#if>
