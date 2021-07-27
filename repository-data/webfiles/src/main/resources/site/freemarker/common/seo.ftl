<#include "include/imports.ftl">
<#if contentBean??>
    <@hst.manageContent hippobean=document parameterName="contentBean" rootPath="seo"/>
    <@hst.headContribution category="title">
    <title>${titletag}</title>
    </@hst.headContribution>

    <#if contentBean?? && contentBean.metaDescription??>
        <@hst.headContribution category="meta">
        <meta name="description" content="${contentBean.metaDescription}"/>
        </@hst.headContribution>
    </#if>

    <@hst.link var="link" canonical=true fullyQualified=true/>
    <@hst.headContribution category="canonical">
        <link rel="canonical"  href="${link}" />
    </@hst.headContribution>

    <#-- Facebook meta tags -->
    <@hst.headContribution category="facebookMeta">
        <meta property="og:url" content="${link}" />
    </@hst.headContribution>

    <@hst.headContribution category="facebookMeta">
        <meta property="og:type" content="website" />
    </@hst.headContribution>

    <@hst.headContribution category="facebookMeta">
        <meta property="og:title" content="${pagetitle}" />
    </@hst.headContribution>

    <@hst.headContribution category="facebookMeta">
        <meta property="og:url" content="${link}" />
    </@hst.headContribution>

    <#if contentBean?? && contentBean.metaDescription??>
        <@hst.headContribution category="facebookMeta">
        <meta property="og:description" content="${contentBean.metaDescription}" />
        </@hst.headContribution>
    </#if>

    <#if image??>
        <@hst.headContribution category="facebookMeta">
        <meta property="og:image" content="<@hst.link hippobean=image.original/>" />
        </@hst.headContribution>
    </#if>

    <#-- Twitter Meta Tags -->
    <@hst.headContribution category="twitterMeta">
        <meta name="twitter:card" content="summary_large_image"/>
    </@hst.headContribution>

    <@hst.headContribution category="twitterMeta">
        <meta property="twitter:domain" content=""/>
    </@hst.headContribution>

    <@hst.headContribution category="twitterMeta">
        <meta property="twitter:url" content="${link}"/>
    </@hst.headContribution>

    <@hst.headContribution category="twitterMeta">
        <meta name="twitter:title" content="${pagetitle}"/>
    </@hst.headContribution>

    <#if contentBean?? && contentBean.metaDescription??>
        <@hst.headContribution category="twitterMeta">
        <meta name="twitter:description" content="${contentBean.metaDescription}"/>
        </@hst.headContribution>
    </#if>

    <#if image??>
        <@hst.headContribution category="twitterMeta">
        <meta name="twitter:image" content="<@hst.link hippobean=image.original/>" />
        </@hst.headContribution>
    </#if>

    <#if showTitle = true>
        <h1 class="ds_page-header__title">${pagetitle}</h1>
    </#if>

<#elseif editMode>
    <@hst.manageContent documentTemplateQuery="new-seo-document" parameterName="document" rootPath="seo"/>
    Click to edit SEO parameters
</#if>
