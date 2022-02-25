<#include "include/imports.ftl">

<#list facebookVerifications as facebookVerification>
    <@hst.headContribution category="facebookVerification">
        <meta name="facebook-domain-verification" content="${facebookVerification.code}" />
    </@hst.headContribution>
</#list>

<#list googleVerifications as googleVerification>
    <@hst.headContribution category="googleVerification">
    <meta name="google-site-verification" content="${googleVerification.code}" />
    </@hst.headContribution>
</#list>

