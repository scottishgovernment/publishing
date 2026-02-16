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

    <#if subjects?has_content>
        <@hst.headContribution category="meta">
            <meta name="dc.subject" content="<#list subjects as subject>${subject}<#sep>, </#sep></#list>"/>
        </@hst.headContribution>
    <#else>

    </#if>
    <#if contentBean.lastUpdatedDate??>
        <@hst.headContribution category="meta">
            <meta name="dc.date.modified" content="<@fmt.formatDate value=contentBean.lastUpdatedDate.time type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
        </@hst.headContribution>
    <#elseif date??>
        <@hst.headContribution category="meta">
            <meta name="dc.date.modified" content="<@fmt.formatDate value=date.time type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
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
    <#if canonical?has_content>
        <@hst.headContribution category="canonical">
            <link rel="canonical"  href="${canonical}" />
        </@hst.headContribution>
        <@hst.headContribution category="openGraph">
            <meta property="og:url" content="${canonical}" />
        </@hst.headContribution>
    </#if>
    <@hst.headContribution category="openGraph">
        <meta property="og:type" content="website" />
    </@hst.headContribution>
    <@hst.headContribution category="openGraph">
        <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
        <meta property="og:title" content="${pagetitle}" />
        </@hst.messagesReplace>
    </@hst.headContribution>
    <#if contentBean?? && contentBean.metaDescription??>
        <@hst.headContribution category="openGraph">
        <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
        <meta property="og:description" content="${contentBean.metaDescription}" />
        </@hst.messagesReplace>
        </@hst.headContribution>
    </#if>
    <#if cardImage??>
        <@hst.headContribution category="openGraph">
        <meta property="og:image" content="<@hst.link hippobean=cardImage.original fullyQualified=true />"/>
        </@hst.headContribution>
    </#if>

    <@hst.headContribution category="title">
    <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
    <title>${titletag}</title>
    </@hst.messagesReplace>
    </@hst.headContribution>

    <@hst.headContribution category="title">
        <script type="application/ld+json" nonce="${nonce}">
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
