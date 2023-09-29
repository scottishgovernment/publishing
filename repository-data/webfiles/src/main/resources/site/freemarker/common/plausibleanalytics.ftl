<#ftl output_format="HTML">
<#include "./include/imports.ftl">
<#if plausibleAnalyticsDomains?has_content>
<#if requiresPlausibleAnalyticsHashScript>
<script defer data-domain="${plausibleAnalyticsDomains}" src="<@hst.link path='/assets/scripts/vendor/plausible/script.hash.js'/>"></script>
<#else>
<script defer data-domain="${plausibleAnalyticsDomains}" src="<@hst.link path='/assets/scripts/vendor/plausible/script.js'/>"></script>
</#if>
<script defer data-domain="${plausibleAnalyticsDomains}" src="<@hst.link path='/assets/scripts/vendor/plausible/script.file-downloads.js'/>"></script>
<script defer data-domain="${plausibleAnalyticsDomains}" src="<@hst.link path='/assets/scripts/vendor/plausible/script.outbound-links.js'/>"></script>
</#if>
