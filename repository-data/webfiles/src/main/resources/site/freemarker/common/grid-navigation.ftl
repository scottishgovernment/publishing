<#include "include/imports.ftl">

<#list children as child>
    <div>
        <@hst.link var="link" hippobean=child/>
        <a href="${link}">${child.title}</a>
    </div>
</#list>