<#include "include/imports.ftl">

<#list facebookVerifications as facebookVerification>
    <@hst.headContribution category="facebookVerification">
        <meta name="facebook-domain-verification" content="${facebookVerification.code}" />
    </@hst.headContribution>
</#list>

