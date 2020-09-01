<#include "include/imports.ftl">
<@hst.setBundle basename="preview.indicator"/>

<#if isStaging>
    <div style="padding: 20px;
    background-color: #f8d7da;
    position: fixed;
    bottom: 0;
    z-index: 10000;
    left: 50%;
    transform: translateX(-50%);
    width: 60%;
    border-radius: 3px;
    text-decoration: underline">
        <span><@fmt.message key="indicator.content" var="content"/>${content?html}</span><br/>
        <span><@fmt.message key="indicator.disclaimer" var="disclaimer"/>${disclaimer?html}</span>
    </div>
</#if>
