<#include "include/imports.ftl">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main class="ds_layout  ds_layout--guide">
            <#if document.sensitive>
                <div class="ds_hide-page">
                    <a href="http://bbc.co.uk/weather" data-altlink="https://www.google.co.uk" class="ds_hide-page__button  ds_button  js-hide-page"><strong>Hide this page</strong> <span class="visually-hidden  js-enabled-text">Or press escape key to hide this page</span></a>
                    <p class="ds_hide-page__text  js-enabled-text">(Or press Esc key)</p>
                </div>
            </#if>

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <span class="ds_page-header__label  ds_content-label">${guide.title}</span>
                    <h1 class="ds_page-header__title">${document.title}</h1>
                    <#if document.lastUpdatedDate??>
                    <div class="ds_category-header__meta">
                        <small>Last updated: <b><@fmt.formatDate value=document.lastUpdatedDate.time type="both" pattern="d MMM yyyy"/></b></small>
                    </div>
                    </#if>
                </header>
            </div>

            <div class="ds_layout__navigation">
                <nav role="navigation" class="ds_contents-nav" aria-label="Sections">
                    <h2 class="gamma">Contents</h2>

                    <ul class="ds_contents-nav__list">
                        <#list children as child>
                        <li class="ds_contents-nav__item">
                            <#if child.bean == document>
                                <span class="ds_contents-nav__link  ds_current">
                                    ${child.bean.title}
                                </span>
                            <#else>
                                <@hst.link var="link" hippobean=child.bean/>
                                <a href="${link}" class="ds_contents-nav__link">
                                    ${child.bean.title}
                                </a>
                            </#if>
                        </li>
                        </#list>
                    </ul>
                </nav>
            </div>

            <div class="ds_layout__content">
                <@hst.html hippohtml=document.content/>

                <nav class="ds_sequential-nav" aria-label="Article navigation">
                    <#if prev??>
                        <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                            <@hst.link var="prevlink" hippobean=prev/>
                            <a data-navigation="sequential-previous" title="Previous section" href="${prevlink}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                                <span class="ds_sequential-nav__text" data-label="previous">
                                ${prev.title?html}
                                </span>
                            </a>
                        </div>
                    </#if>
                    <#if next??>
                        <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
                            <@hst.link var="nextlink" hippobean=next/>
                            <a data-navigation="sequential-next" title="Next section" href="${nextlink}" class="ds_sequential-nav__button  ds_sequential-nav__button--right">
                                <span class="ds_sequential-nav__text" data-label="next">
                                ${next.title?html}
                                </span>
                            </a>
                        </div>
                    </#if>
                </nav>
            </div>

            <#if guide.relateditems?has_content >
                <aside class="ds_layout__sidebar">
                    <aside class="ds_article-aside">
                        <h2 class="gamma">Related content</h2>
                        <ul class="ds_no-bullets">
                            <#list guide.relateditems as item>
                                <#list item.relatedItem as link>
                                    <@hst.link var="url" hippobean=link/>
                                    <li>
                                        <a href="${url}" data-gtm="link-related-${link?index}">${link.title}</a>
                                    </li>
                                </#list>
                            </#list>
                        </ul>
                    </aside>
                </aside>
            </#if>

            <div class="ds_layout__feedback">
                <#include 'feedback-wrapper.ftl'>
            </div>
        </main>
    </div>
</div>
</#if>

<@hst.headContribution category="resourcehints">
    <#if nextlink??>
    <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>

<@hst.headContribution category="meta">
    <#if document??>
    <meta name="description" content="${document.metaDescription?html}"/>
    </#if>
</@hst.headContribution>

<#assign scriptName="guide">
<#include 'scripts.ftl'/>
