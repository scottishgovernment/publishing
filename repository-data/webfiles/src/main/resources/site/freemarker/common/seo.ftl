<#ftl output_format="HTML">
<#include "include/imports.ftl">


<#if contentBean??>
<#assign variables = hstRequestContext.getAttribute("variables")/>

    <#if contentBean.title??>
        <@hst.headContribution category="meta">
            <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
            <meta name="dc.title" content="${contentBean.title}"/>
            </@hst.messagesReplace>
        </@hst.headContribution>
    </#if>

    <@hst.headContribution category="meta">
        <#if contentBean.summary?has_content>
            <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
            <meta name="dc.description" content="${contentBean.summary}"/>
            </@hst.messagesReplace>
        <#elseif contentBean.metaDescription?has_content>
            <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
            <meta name="dc.description" content="${contentBean.metaDescription}"/>
            </@hst.messagesReplace>
        </#if>
    </@hst.headContribution>

    <#if contentBean.tags??>
        <@hst.headContribution category="meta">
            <meta name="dc.subject" content="<#list contentBean.tags as tag>${tag}<#sep>, </#sep></#list>"/>
        </@hst.headContribution>
    </#if>
    <#if contentBean.lastUpdatedDate??>
        <@hst.headContribution category="meta">
            <meta name="dc.date.modified" content="<@fmt.formatDate value=contentBean.lastUpdatedDate.time type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
        </@hst.headContribution>
    <#elseif date??>
        <@hst.headContribution category="meta">
            <meta name="dc.date.modified" content="<@fmt.formatDate value=date type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
        </@hst.headContribution>
    </#if>
    <#if contentBean?? && contentBean.metaDescription??>
        <@hst.headContribution category="meta">
        <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
        <meta name="description" content="${contentBean.metaDescription}"/>
        </@hst.messagesReplace>
        </@hst.headContribution>
    </#if>
    <#if contentBean?? && contentBean.noindex?? && contentBean.noindex = true>
        <@hst.headContribution category="noindex">
        <meta name="robots" content="noindex"/>
        </@hst.headContribution>
    </#if>
    <#if contentBean?? && contentBean.title??>
        <@hst.headContribution category="meta">
        <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
        <meta name="dc.title" content="${contentBean.title}"/>
        </@hst.messagesReplace>
        </@hst.headContribution>
    </#if>

    <@hst.link var="sitelink" hippobean=baseBean canonical=true fullyQualified=true/>
    <@hst.headContribution category="canonical">
        <link rel="canonical"  href="${canonical}" />
    </@hst.headContribution>
    <#-- Facebook meta tags: only show if a card image is available -->
    <#if cardImage??>
        <@hst.headContribution category="facebookMeta">
            <meta property="og:url" content="${canonical}" />
        </@hst.headContribution>
        <@hst.headContribution category="facebookMeta">
            <meta property="og:type" content="website" />
        </@hst.headContribution>
        <@hst.headContribution category="facebookMeta">
            <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
            <meta property="og:title" content="${pagetitle}" />
            </@hst.messagesReplace>
        </@hst.headContribution>
        <#if contentBean?? && contentBean.metaDescription??>
            <@hst.headContribution category="facebookMeta">
            <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
            <meta property="og:description" content="${contentBean.metaDescription}" />
            </@hst.messagesReplace>
            </@hst.headContribution>
        </#if>
        <@hst.headContribution category="facebookMeta">
        <meta property="og:image" content="<@hst.link hippobean=cardImage.original fullyQualified=true />"/>
        </@hst.headContribution>
    </#if>
    <#-- Twitter Meta Tags: only show if a card image is available -->
    <#if cardImage??>
        <@hst.headContribution category="twitterMeta">
            <meta name="twitter:card" content="summary_large_image"/>
        </@hst.headContribution>
        <@hst.headContribution category="twitterMeta">
            <meta property="twitter:domain" content=""/>
        </@hst.headContribution>
        <@hst.headContribution category="twitterMeta">
            <meta property="twitter:url" content="${canonical}"/>
        </@hst.headContribution>
        <@hst.headContribution category="twitterMeta">
            <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
            <meta name="twitter:title" content="${pagetitle}"/>
            </@hst.messagesReplace>
        </@hst.headContribution>
        <#if contentBean?? && contentBean.metaDescription??>
            <@hst.headContribution category="twitterMeta">
            <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
            <meta name="twitter:description" content="${contentBean.metaDescription}"/>
            </@hst.messagesReplace>
            </@hst.headContribution>
        </#if>
        <@hst.headContribution category="twitterMeta">
        <meta name="twitter:image" content="<@hst.link hippobean=cardImage.original fullyQualified=true />" />
        </@hst.headContribution>
    </#if>

    <@hst.headContribution category="title">
    <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
    <title>${titletag}</title>
    </@hst.messagesReplace>
    </@hst.headContribution>

    <@hst.headContribution category="title">
        <script type="application/ld+json">
            {
              "@context" : "https://schema.org",
              "@type" : "WebSite",
              "name" : "${sitetitle}",
              "url" : "${sitelink}"<#if isSearchEnabled>,
              "potentialAction": {
                "@type": "SearchAction",
                "target": {
                    "@type": "EntryPoint",
                    "urlTemplate": "${sitelink}search?q={search_term_string}"
                },
                "query-input": "required name=search_term_string"
              }
              <#else>

              </#if>
            }
        </script>
    </@hst.headContribution>

    <#if hstRequestContext.preview>
        <div class="ds_wrapper  cms-visible-if-show-components" style="padding: 5px 0; position: relative;">
            <img class="ds_icon ds_icon--40" ng-src="/site/icons/seo.svg" src="/site/icons/seo.svg"> <small class="">Click to edit SEO parameters</small>
            <@hst.manageContent hippobean=document documentTemplateQuery="new-seo-document" parameterName="contentBean" rootPath="seo"/>
        </div>
    </#if>
<#elseif editMode>
    <div class="ds_wrapper  cms-visible-if-show-components" style="padding: 5px 0; position: relative;">
        <img class="ds_icon ds_icon--40" ng-src="/site/icons/seo.svg" src="/site/icons/seo.svg"> <small class="">Click to edit SEO parameters</small>

        <@hst.manageContent documentTemplateQuery="new-seo-document" parameterName="document" rootPath="seo"/>
    </div>
</#if>
