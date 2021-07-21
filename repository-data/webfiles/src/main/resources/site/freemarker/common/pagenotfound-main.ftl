<#include "include/imports.ftl">
<@hst.setBundle basename="essentials.pagenotfound"/>
<div class="cms-editable">
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1><@fmt.message key="pagenotfound.title" var="title"/>${title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <@fmt.message key="pagenotfound.text" var="text"/>${text}
            </div>
        </main>
    </div>

</div>

<@hst.headContribution category="title">
    <#if contentBean?? && contentBean.title??>
    <title>Page not fount - ${siteTitle}</title>
    </#if>
</@hst.headContribution>

<@hst.headContribution category="meta">
<meta name="description" content="404 - Page not found"/>
</@hst.headContribution>

<#assign scriptName="default">
<#include 'scripts.ftl'/>
