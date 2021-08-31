<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#list fragments as fragment>
    <@hst.html hippohtml=fragment.html/>
</#list>