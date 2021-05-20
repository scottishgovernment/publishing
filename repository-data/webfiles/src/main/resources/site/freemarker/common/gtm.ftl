<#ftl output_format="HTML">
<#include "./include/imports.ftl">

<@hst.headContribution category="googleTagManager">

<!-- Google Tag Manager (GTM) -->
<script id="gtm-datalayer">
    window.dataLayer = window.dataLayer || [];
    window.dataLayer.push({
        'gtm.whitelist': ['google', 'jsm', 'lcl'],

        <#if document.audience?has_content>
            'audience': '${document.audience}',
        </#if>
        <#if reportingTags?has_content>
            'reportingTags': [<#list reportingTags as tag>'${tag?js_string}'<#sep>, </#sep></#list>],
        </#if>
        <#if document.lifeEvents?has_content>
            'lifeEvents': [<#list document.lifeEvents as lifeEvent>'${lifeEvent?js_string}'<#sep>, </#sep></#list>],
        </#if>
        <#if document.serviceproviders?has_content>
            'serviceproviders': [<#list document.serviceproviders as serviceprovider>'${serviceprovider?js_string}'<#sep>, </#sep></#list>],
        </#if>
            <#if gtmName??>'format' : '${gtmName?js_string}',</#if>
            <#if gtmId??>'siteid' : '${gtmId?js_string}'</#if>
    });
</script>
</@hst.headContribution>

<@hst.headContribution category="googleTagManager">
<script id="gtm-script">
    initGTM = function () {
        (function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
                new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
                j=d.createElement(s),dl=l!='dataLayer'?'&amp;l='+l:'';j.async=true;j.src=
                'https://www.googletagmanager.com/gtm.js?id='+i+dl+'<#if gtmAuth?has_content>&amp;gtm_auth=${gtmAuth?js_string}</#if><#if gtmEnv?has_content>&amp;gtm_preview=${gtmEnv?js_string}&amp;gtm_cookies_win=x</#if>';f.parentNode.insertBefore(j,f);
        })(window,document,'script','dataLayer','${gtmContainerId?js_string}');
    }
</script>
</@hst.headContribution>

<!-- Google Tag Manager (noscript) -->
<noscript id="gtm-noscript"><iframe src="https://www.googletagmanager.com/ns.html?id=${gtmContainerId?js_string}<#if gtmAuth?has_content>&amp;gtm_auth=${gtmAuth?js_string}</#if><#if gtmEnv?has_content>&amp;gtm_preview=${gtmEnv?js_string}&amp;gtm_cookies_win=x</#if>"
                                    heigh t="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->
