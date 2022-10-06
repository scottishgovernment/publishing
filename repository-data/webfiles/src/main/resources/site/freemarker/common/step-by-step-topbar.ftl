<#ftl output_format="HTML">
<#include "include/imports.ftl">
<!--noindex-->
<#list stepbysteps as stepbystep>

    <div>
        Part of<br />
        <a href="<@hst.link var=link hippobean=stepbystep/>">${stepbystep.title}</a>
    </div>
</#list>
<!--endnoindex-->