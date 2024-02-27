<#ftl output_format="HTML">

<#include "../common/macros/lang-attributes.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<#-- process update history -->
<#assign sortedUpdateHistory = document.updateHistory?sort_by('lastUpdated') />
<#assign firstPublished = sortedUpdateHistory?first />
<#assign remainingUpdates = sortedUpdateHistory[1..]?reverse />

<#if remainingUpdates?size gt 0>
<div <@revertlang document /> id="history" class="ds_!_margin-top--4">
    <dl class="ds_metadata">
        <#if firstPublished??>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">First published</dt>
                <dd class="ds_metadata__value"><strong><@fmt.formatDate value=firstPublished.lastUpdated.time type="both" pattern="d MMMM yyyy"/></strong></dd>
            </div>
        </#if>

        <#assign latestUpdateDate = remainingUpdates[0].lastUpdated>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">Last updated</dt>
            <dd class="ds_metadata__value">
                <strong><@fmt.formatDate value=latestUpdateDate.time type="both" pattern="d MMMM yyyy"/></strong>
                <span class="dss_toggle-link">
                    - <a href="#full-history" data-module="ds-toggle-link" aria-controls="full-history" data-toggled-text="hide all updates">show all updates</a>
                </span>
            </dd>
        </div>

        <div id="full-history" data-module="ds-update-history" class="ds_metadata__item  dss_toggle-link__target">
            <dt class="ds_metadata__key  visually-hidden">All updates</dt>
            <dd class="ds_metadata__value  dss_history-list">
                <ol class="ds_no-bullets">
                    <#list remainingUpdates as history>
                        <li class="dss_history-list__item">
                            <time datetime="<@fmt.formatDate value=history.lastUpdated.time type="both" pattern="yyyy-MM-dd'T'HH:mm:ssz"/>">
                                <@fmt.formatDate value=history.lastUpdated.time type="both" pattern="d MMMM yyyy"/>
                            </time>
                            <p style="font-weight: normal">${history.updateText}</p>
                        </li>
                    </#list>
                </ol>
            </dd>
        </div>
    </dl>
</div>
</#if>
</@hst.messagesReplace>

