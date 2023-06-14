<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
234324
<#if updates?size gt 0>
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <h2>Latest updates</h2>
            <div class="ds_cb__text">
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
</#if>
