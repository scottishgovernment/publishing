<#ftl output_format="HTML">
<#include "include/imports.ftl">

<#if document??>

<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">

            <#if document.sensitive?? && document.sensitive>
                <!--noindex-->
                <#include "hide-this-page.ftl">
                <!--endnoindex-->
            </#if>

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                    <dl class="ds_page-header__metadata  ds_metadata">
                        <#if document.lastUpdatedDate??>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Last updated</dt>
                                <dd class="ds_metadata__value"><@fmt.formatDate value=document.lastUpdatedDate.time type="both" pattern="d MMM yyyy"/></dd>
                            </div>
                        </#if>
                    </dl>
                </header>
            </div>

            <#if document.logo??>
                <div class="ds_layout__partner  mg_partner-logo">
                    <img alt="" src="<@hst.link hippobean=document.logo/>" />
                </div>
            </#if>

            <div class="ds_layout__content">
                <@hst.html hippohtml=document.content/>

                <#if sequenceable?? && sequenceable == true>
                    <!--noindex-->
                    <nav class="ds_sequential-nav" aria-label="Article navigation">
                        <#if prev??>
                            <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                                <@hst.link var="prevlink" hippobean=prev/>
                                <a  title="Previous section" href="${prevlink}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                                    <span class="ds_sequential-nav__text" data-label="previous">
                                        ${prev.title}
                                    </span>
                                </a>
                            </div>
                        </#if>
                        <#if next??>
                            <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
                                <@hst.link var="nextlink" hippobean=next/>
                                <a  title="Next section" href="${nextlink}" class="ds_sequential-nav__button  ds_sequential-nav__button--right">
                                    <span class="ds_sequential-nav__text" data-label="next">
                                        ${next.title}
                                    </span>
                                </a>
                            </div>
                        </#if>
                    </nav>
                    <!--endnoindex-->
                </#if>
            </div>

            <#if document.relateditems?has_content >
                <!--noindex-->
                <div class="ds_layout__sidebar">
                    <aside class="ds_article-aside">
                        <h2 class="gamma">Related content</h2>
                        <ul class="ds_no-bullets">
                            <#list document.relateditems as item>
                                <#list item.relatedItem as link>
                                    <@hst.link var="url" hippobean=link/>
                                    <li>
                                        <a href="${url}">${link.title}</a>
                                    </li>
                                </#list>
                            </#list>
                        </ul>
                    </aside>

                    <#if document.sensitive>
                        <aside class="ds_article-aside" id="stay-safe-online">
                            <h3>Stay safe online &hellip;</h3>
                            <ul class="ds_no-bullets">
                                <li>
                                    <a href="/staying-safe-online/deleting-your-browser-history/" data-navigation="staysafe-yes">Deleting your history and staying safe online</a>
                                </li>
                            </ul>
                        </aside>
                    </#if>
                </div>
                <!--endnoindex-->
            </#if>

            <#include 'feedback-wrapper.ftl'>

        </main>
    </div>
</div>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Article"/>
</@hst.headContribution>

</#if>

<@hst.headContribution category="resourcehints">
    <#if nextlink??>
        <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>



<#assign scriptName="article">
<#include 'scripts.ftl'/>
