<#ftl output_format="HTML">
<#include "./include/imports.ftl">

<#if document??>
    <#if !dateCreated??><#assign dateCreated = document.getSingleProperty('hippostdpubwf:creationDate')/></#if>
    <#if !lastUpdated??><#assign lastUpdated = document.getSingleProperty('hippostdpubwf:lastModificationDate')/></#if>
    <@hst.headContribution category="googleTagManagerDataLayer">

    <!-- Google Tag Manager (GTM) -->
    <script src='<@hst.webfile path="assets/scripts/datalayer.js"/>'
        id="gtm-datalayer"
        <#if userType??>data-usertype="${userType?js_string}"</#if>
        <#if document.audience?has_content>data-audience="${document.audience?js_string}"</#if>
        <#if reportingTags?has_content>data-reportingtags="<#list reportingTags as item>${item?js_string}<#sep>|</#sep></#list>"</#if>
        <#if document.lifeEvents?has_content>data-lifeevents="<#list document.lifeEvents as item><#if item?has_content>${item?js_string}<#sep>|</#sep></#if></#list>"</#if>
        <#if document.serviceproviders?has_content>data-serviceproviders="<#list document.serviceproviders as item>${item?js_string}<#sep>|</#sep></#list>"</#if>
        <#if gtmName??>data-format="${gtmName?js_string}"</#if>
        <#if gtmId??>data-siteid="${gtmId?js_string}"</#if>
        data-lastupdated='<@fmt.formatDate value=lastUpdated.time type="Date" pattern="dd/MM/yyyy" />'
        data-datecreated='<@fmt.formatDate value=dateCreated.time type="Date" pattern="dd/MM/yyyy" />'
        >
    </script>
    </@hst.headContribution>
</#if>

<@hst.headContribution category="googleTagManager">
    <script src='<@hst.webfile path="assets/scripts/gtm.js"/>'
        id="gtm-script"
        data-containerid="${gtmContainerId?js_string}"
        <#if gtmEnv?has_content>data-env="${gtmEnv?js_string}"</#if>
        <#if gtmAuth?has_content>data-auth="${gtmAuth?js_string}"</#if>></script>
</@hst.headContribution>

<!-- Google Tag Manager (noscript) -->
<noscript id="gtm-noscript"><iframe src="https://www.googletagmanager.com/ns.html?id=${gtmContainerId?js_string}<#if gtmAuth?has_content>&amp;gtm_auth=${gtmAuth?js_string}</#if><#if gtmEnv?has_content>&amp;gtm_preview=${gtmEnv?js_string}&amp;gtm_cookies_win=x</#if>"
                                    height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->
