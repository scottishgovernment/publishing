<#include "include/imports.ftl">

<h1>${document.title}</h1>
<p>${document.summary}</p>
<@hst.html hippohtml=document.prologue/>

<#if document.navigationType == "grid">
    <#include 'grid-navigation.ftl'/>
</#if>

<#if document.navigationType == "list">
    <#include 'list-navigation.ftl'/>
</#if>

<@hst.html hippohtml=document.epilogue/>