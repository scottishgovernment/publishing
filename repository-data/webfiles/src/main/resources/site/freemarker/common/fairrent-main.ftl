<#ftl output_format="HTML">
<#include "include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>

<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">

            <div class="ds_layout__header">
                <div id="fair-rent-register-header">
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
            </div>

            <div class="ds_layout__content">

                <style>
                    @media print {
                        #staging-notice,
                        .ds_site-header__search,
                        #site-footer,
                        #notifications-wrapper,
                        .header-bar,
                        [aria-label="Breadcrumb"],
                        .ds_layout__feedback
                        {display: none;}

                        #page-content {
                            display: block;
                        }

                        .ds_layout__content {
                            margin-top: 1em;
                        }

                        .ds_accordion-item__body {
                            display: block !important;
                            padding-bottom: 32px !important;
                            padding-top: 24px !important;
                            max-height: unset !important;
                        }

                        .ds_accordion-item__indicator {
                            display: none;
                        }
                    }
                </style>

                <section id="fair-rent-introduction" class="js-fair-rent-section  fully-hidden">
                        <@hst.html hippohtml=document.content/>

                        <div class="ds_site-header__search  ds_site-search">
                            <form role="search" class="ds_site-search__form" method="GET" action="">
                                <label class="ds_label  visually-hidden" for="site-search">Search</label>

                                <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
                                    <input name="query" id="fair-rent-search-box" class="ds_input" type="text" placeholder="Search the Fair Rent Register">

                                    <button type="submit" class="ds_button  ds_button--icon-only  js-site-search-button">
                                        <span class="visually-hidden">Search the Fair Rent Register</span>
                                        <svg class="ds_icon" role="img"><use xlink:href="${iconspath}#search"></use></svg>
                                    </button>
                                </div>
                            </form>
                        </div>

                        <@hst.html hippohtml=document.additionalContent/>
                    </section>

                <section id="fair-rent-list" class="js-fair-rent-section  fully-hidden">
                    <div class="ds_search-results">
                        <h2 class="visually-hidden">Search results</h2>

                        <div id="results-top"></div>

                        <p id="result-count" class="ds_search-results__count  js-search-results-count"></p>

                        <ol id="search-results-list" class="ds_search-results__list"></ol>

                        <div id="pagination"></div>
                    </div>
                </section>

                <section id="fair-rent-property" class="js-fair-rent-section  fully-hidden">

                </section>
            </div>

            <#if document.relateditems?has_content >
                <aside class="ds_layout__sidebar">
                    <aside class="ds_article-aside">
                        <h2 class="gamma">Related content</h2>
                        <ul class="ds_no-bullets">
                            <#list document.relateditems as item>
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
    <meta name="description" content="${document.metaDescription}"/>
    </#if>
</@hst.headContribution>

<#assign scriptName="fair-rent-register">
<#include 'scripts.ftl'/>
