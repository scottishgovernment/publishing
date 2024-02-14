<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<#if updates?size gt 0>
    <div class="ds_cb  ds_cb--text">
        <div class="ds_wrapper">
            <div class="ds_cb__inner">
                <div class="ds_cb__text">
                    <h2>Latest updates</h2>

                    <ul class="dss_whats-new  ds_no-bullets">
                        <#list updates as update>
                            <li class="dss_whats-new__item">
                                <@hst.html hippohtml=update.updateTextLong/>
                                <span class="dss_whats-new__date">
                                    <@fmt.formatDate value=update.lastUpdated.time type="both" pattern="d MMMM yyyy"/>
                                </span>
                            </li>
                        </#list>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</#if>
</@hst.messagesReplace>
