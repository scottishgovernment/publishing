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



<!-- simple analytics -->
<@hst.headContribution category="footerScripts">
    <script>
        // this polyfill is required until Simple Analytics fix this issue in their code
        (function () {
        if ( typeof window.CustomEvent === "function" ) return false; //If not IE

        function CustomEvent ( event, params ) {
            params = params || { bubbles: false, cancelable: false, detail: undefined };
            var evt = document.createEvent( 'CustomEvent' );
            evt.initCustomEvent( event, params.bubbles, params.cancelable, params.detail );
            return evt;
        }

        CustomEvent.prototype = window.Event.prototype;
        window.Event = CustomEvent;
        window.CustomEvent = CustomEvent;
        })();
    </script>
</@hst.headContribution>

<@hst.headContribution category="footerScripts">
    <script type="module" src='<@hst.webfile path="assets/scripts/simple-analytics.js"/>'></script>
</@hst.headContribution>

<@hst.headContribution category="footerScripts">
    <script nomodule="true" src='<@hst.webfile path="assets/scripts/simple-analytics.es5.js"/>'></script>
</@hst.headContribution>
