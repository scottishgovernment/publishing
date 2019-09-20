<#include "include/imports.ftl">

<div class="ds_wrapper">

<#list children as child>
    <div>
        <@hst.link var="link" hippobean=child/>
        <a href="${link}">${child.title}</a>
    </div>
</#list>

</div>
