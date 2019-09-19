<#include "include/imports.ftl">

<ul>
    <#list children as child>
        <li>
            <@hst.link var="link" hippobean=child/>
            <a href="${link}">${child.title}</a>
        </li>
    </#list>
</ul>