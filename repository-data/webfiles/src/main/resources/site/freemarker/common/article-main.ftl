<#include "include/imports.ftl">

<h1>${document.title}</h1>
<p>${document.summary}</p>
<@hst.html hippohtml=document.content/>

<hr>

<#if prev??>
<@hst.link var="link" hippobean=prev/>
<a href="${link}">Prev (${prev.title?html})</a>
</#if>

<#if next??>
<@hst.link var="link" hippobean=next/>
<a href="${link}">Next (${next.title?html})</a>
</#if>
