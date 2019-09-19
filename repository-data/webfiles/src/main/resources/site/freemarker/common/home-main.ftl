<#include "include/imports.ftl">

<h1>${document.title}</h1>
<p>${document.summary}</p>
<@hst.html hippohtml=document.prologue/>
<#include 'grid-navigation.ftl'/>
<@hst.html hippohtml=document.epilogue/>