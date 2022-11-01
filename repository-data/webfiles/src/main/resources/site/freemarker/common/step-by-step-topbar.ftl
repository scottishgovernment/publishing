<#ftl output_format="HTML">
<#include "include/imports.ftl">
<!--noindex-->

<aside class="ds_callout">
    <h3>Part of</h3>
    <#if stepbysteps?size gt 1>
        <ul class="ds_no-bullets">
            <#list stepbysteps as stepbystep>
                <li><a href="<@hst.link var=link hippobean=stepbystep.stepByStepGuide/>"><b>${stepbystep.stepByStepGuide.title}</b></a></li>
            </#list>
        </ul>
    <#else>
        <a href="<@hst.link var=link hippobean=stepbysteps[0].stepByStepGuide/>"><b>${stepbysteps[0].stepByStepGuide.title}</b></a>
    </#if>
</aside>
<!--endnoindex-->
