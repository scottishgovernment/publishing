<#ftl output_format="HTML">
<#include "include/imports.ftl">

<!--noindex-->
<#if stepBySteps?? && stepBySteps?size gt 0>
<aside class="ds_callout  ds_step-navigation-top" aria-labelledby="step-navigation-topbar">
    <h3 id="step-navigation-topbar">Part of <#if stepBySteps?size gt 1><span class="visually-hidden">${stepBySteps?size} step by step guides</span></#if></h3>
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
<!--endnoindex-->
