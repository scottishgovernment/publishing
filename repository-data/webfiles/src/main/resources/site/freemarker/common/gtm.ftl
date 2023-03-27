<#ftl output_format="HTML">
<#include "./include/imports.ftl">

<#if document??>
    <@hst.headContribution category="googleTagManagerDataLayer">

    <!-- Google Tag Manager (GTM) -->
    <script id="gtm-datalayer">
        window.dataLayer = window.dataLayer || [];
        window.dataLayer.push({
            'gtm.whitelist': ['google', 'jsm', 'lcl'],
            <#if userType??>
                'userType': '${userType?js_string}',
            </#if>
            <#if document.audience?has_content>
                'audience': '${document.audience}',
            </#if>
            <#if reportingTags?has_content>
                'reportingTags': [<#list reportingTags as tag>'${tag?js_string}'<#sep>, </#sep></#list>],
            </#if>
            <#if document.lifeEvents?has_content>
                'lifeEvents': [<#list document.lifeEvents as lifeEvent><#if lifeEvent?has_content>'${lifeEvent?js_string}'<#sep>, </#sep></#if></#list>],
            </#if>
            <#if document.serviceproviders?has_content>
                'serviceproviders': [<#list document.serviceproviders as serviceprovider>'${serviceprovider?js_string}'<#sep>, </#sep></#list>],
            </#if>
                <#if gtmName??>'format' : '${gtmName?js_string}',</#if>
                <#if gtmId??>'siteid' : '${gtmId?js_string}'</#if>
        });
    </script>
    </@hst.headContribution>
</#if>

<@hst.headContribution category="googleTagManager">
    <script src='<@hst.webfile path="assets/scripts/gtm.js"/>' id="gtm-script" data-containerid="${gtmContainerId?js_string}" <#if gtmEnv?has_content>data-env="${gtmEnv?js_string}"</#if> <#if gtmAuth?has_content>data-auth"=${gtmAuth?js_string}"</#if>></script>
</@hst.headContribution>

<!-- Google Tag Manager (noscript) -->
<noscript id="gtm-noscript"><iframe src="https://www.googletagmanager.com/ns.html?id=${gtmContainerId?js_string}<#if gtmAuth?has_content>&amp;gtm_auth=${gtmAuth?js_string}</#if><#if gtmEnv?has_content>&amp;gtm_preview=${gtmEnv?js_string}&amp;gtm_cookies_win=x</#if>"
                                    height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->
