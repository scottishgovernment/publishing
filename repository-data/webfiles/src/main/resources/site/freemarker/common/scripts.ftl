<#include "./include/imports.ftl">

<@hst.headContribution category="footerScripts">
<script type="module" src='<@hst.webfile path="assets/scripts/global.js"/>'></script>
</@hst.headContribution>

<@hst.headContribution category="footerScripts">
<script nomodule="true" src='<@hst.webfile path="assets/scripts/global.es5.js"/>'></script>
</@hst.headContribution>

<@hst.headContribution category="footerScripts">
<#--  <#if scriptName>  -->
<script type="module" src='<@hst.webfile path="assets/scripts/${scriptName}.js"/>'></script>
<#--  </#if>  -->
</@hst.headContribution>

<@hst.headContribution category="footerScripts">
<#--  <#if scriptName>  -->
<script nomodule="true" src='<@hst.webfile path="assets/scripts/${scriptName}.es5.js"/>'></script>
<#--  </#if>  -->
</@hst.headContribution>
