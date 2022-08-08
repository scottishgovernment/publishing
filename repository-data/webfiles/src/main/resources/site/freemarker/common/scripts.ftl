<#ftl output_format="HTML">
<#include "./include/imports.ftl">

<@hst.headContribution category="footerScripts">
<script src="<@hst.link path='/assets/scripts/vendor/svgxuse.min.js'/>"></script>
</@hst.headContribution>

<@hst.headContribution category="footerScripts">
<script type="module" src='<@hst.webfile path="assets/scripts/global.js"/>'></script>
</@hst.headContribution>

<@hst.headContribution category="footerScripts">
<script nomodule="true" src='<@hst.webfile path="assets/scripts/global.es5.js"/>'></script>
</@hst.headContribution>

<#if scriptName??>
    <@hst.headContribution category="footerScripts">
        <script type="module" src='<@hst.webfile path="assets/scripts/${scriptName}.js"/>'></script>
    </@hst.headContribution>

    <@hst.headContribution category="footerScripts">
        <script nomodule="true" src='<@hst.webfile path="assets/scripts/${scriptName}.es5.js"/>'></script>
    </@hst.headContribution>
</#if>
