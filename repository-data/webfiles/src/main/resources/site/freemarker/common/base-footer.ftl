<#include "include/imports.ftl">

<hr>

<footer>
    <ul>
        <#list pageable.items as item>
            <li>
                <@hst.link var="link" hippobean=item />
                <a href="${link}">${item.title}</a>
            </li>
        </#list>
    </ul>
</footer>
