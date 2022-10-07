<#ftl output_format="HTML">
<#include "include/imports.ftl">
<!--noindex-->
SIZE: ${stepbysteps?size}
<#list stepbysteps as stepbystep>


    <div>
        steps ${stepbystep.stepByStepGuide.steps?size}
        Part of<br />
        <a href="<@hst.link var=link hippobean=stepbystep.stepByStepGuide/>">${stepbystep.stepByStepGuide.title}</a>
    </div>
</#list>
<!--endnoindex-->