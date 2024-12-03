<#ftl output_format="HTML">
<#include "include/imports.ftl">

<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

<!--noindex-->
<#if stepBySteps?? && stepBySteps?size gt 0>
<aside class="ds_callout  ds_step-navigation-top" aria-labelledby="step-navigation-topbar">
    <span class="ds_h3" id="step-navigation-topbar">Part of <#if stepBySteps?size gt 1><span class="visually-hidden">${stepBySteps?size} step by step guides</span></#if></span>
    <#if stepBySteps?size gt 1>
        <ul class="ds_no-bullets">
            <#list stepBySteps as stepbystep>
                <li><a href="<@hst.link var=link hippobean=stepbystep.stepByStepGuide/>"><b>${stepbystep.stepByStepGuide.title}</b></a></li>
            </#list>
        </ul>
    <#else>
        <a href="<@hst.link var=link hippobean=stepBySteps[0].stepByStepGuide/>"><b>${stepBySteps[0].stepByStepGuide.title}</b></a>
    </#if>
</aside>
</#if>
</@hst.messagesReplace>
<!--endnoindex-->
