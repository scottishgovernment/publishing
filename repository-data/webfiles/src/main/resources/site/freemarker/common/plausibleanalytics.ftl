<#ftl output_format="HTML">
<#include "./include/imports.ftl">
<#if plausibleAnalyticsDomains?has_content>
<#if requiresPlausibleAnalyticsHashScript>
<script defer data-domain="${plausibleAnalyticsDomains}" src="https://plausible.io/js/script.hash.js"></script>
<#else>
<script defer data-domain="${plausibleAnalyticsDomains}" src="https://plausible.io/js/script.js"></script>
</#if>
<script defer data-domain="${plausibleAnalyticsDomains}" src="https://plausible.io/js/script.file-downloads.js"></script>
<script defer data-domain="${plausibleAnalyticsDomains}" src="https://plausible.io/js/script.outbound-links.js"></script>
</#if>