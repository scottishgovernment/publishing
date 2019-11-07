<#include "./include/imports.ftl">

<#if useLiveAnalytics>
    <#assign gtmAuth = "DxwtOU_tpyLiqCMda2mJCg" />
    <#assign gtmEnv = "2" />
<#else>
    <#assign gtmAuth = "QSeXb9g32a8PRRDfiJ3vAw" />
    <#assign gtmEnv = "5" />
</#if>

<@hst.headContribution category="googleTagManager">
<!-- Google Tag Manager (GTM) -->
<script id="gtm-datalayer">
    window.dataLayer = window.dataLayer || [];
    window.dataLayer.push({
        'gtm.whitelist': ['google', 'jsm', 'lcl'],
        <#if gtmName??>'format' : '${gtmName}',</#if>
        <#if gtmId??>'siteid' : '${gtmId}'</#if>
    });
</script>
</@hst.headContribution>

<@hst.headContribution category="googleTagManager">
<script>
initGTM = function () {
    (function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&amp;l='+l:'';j.async=true;j.src=
'https://www.googletagmanager.com/gtm.js?id='+i+dl+ '&amp;gtm_auth=${gtmAuth}&amp;gtm_preview=env-${gtmEnv}&amp;gtm_cookies_win=x';f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-NGMNV4B');
}
</script>
<!-- End Google Tag Manager -->
</@hst.headContribution>

<!-- Google Tag Manager (noscript) -->
<noscript><iframe src="https://www.googletagmanager.com/ns.html?id=GTM-NGMNV4B&gtm_auth=${gtmAuth}&gtm_preview=env-${gtmEnv}&gtm_cookies_win=x"
height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->
