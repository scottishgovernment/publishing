<#ftl output_format="HTML">
<#include "include/imports.ftl">
HERE
<!--noindex-->
<#list stepbysteps as stepbystep>
    <div class="ds_article-aside">
        <h2 class="gamma">
            Part of<br />
            <hst:link var="link" hippobean="${stepbystep}"/>
            <a href="${link}">${stepbystep.title}</a>
        </h2>
    </div>
</#list>
<!--endnoindex-->