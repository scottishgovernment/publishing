<#include "include/imports.ftl">
<#switch weight>
    <#case 'h2'>
        <h2 style="text-align:${alignment}">${text}</h2>
        <#break>

    <#case 'h3'>
        <h3>${text} ${alignment}</h3>
        <#break>


</#switch>
