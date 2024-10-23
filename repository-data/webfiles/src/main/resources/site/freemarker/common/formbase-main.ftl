<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">

<#if document??>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

    <div class="cms-editable">
        <@hst.manageContent hippobean=document />

        <@hst.include ref="breadcrumbs"/>

        <div class="ds_wrapper">
            <main id="main-content" class="ds_layout  mg_layout--paged-form">
                <div class="ds_layout__header">
                    <div class="ds_error-summary  fully-hidden  client-error" id="feedback-box" aria-labelledby="error-summary-title" role="alert" aria-live="assertive">
                        <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>

                        <p>There were some errors found on this page:</p>

                        <div class="form-errors">

                        </div>
                    </div>

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

                <div class="ds_layout__section-progress">
                    <div id="section-progess-indicator"></div>
                </div>

                <div class="ds_layout__form-content">
                    <div class="multi-page-form" name="${document.formtype}">


                        <div id="form-container">
                            <input type="hidden" id="recaptchaSitekey" value="${recaptchaSitekey}"/>
                            <input type="hidden" id="recaptchaEnabled" value="${recaptchaEnabled?c}"/>

                            <#if document.contentBlocks??>
                                <@renderContentBlocks document.contentBlocks />
                            </#if>
                        </div>

                        <div id="cms-additional-content-source" class="fully-hidden">
                            <#if document.additionalContentBlocks??>
                                <@renderContentBlocks document.additionalContentBlocks />
                            </#if>
                        </div>
                    </div>

                    <noscript>
                        <div class="ds_warning-text">
                            <strong class="ds_warning-text__icon" aria-hidden="true"></strong>
                            <strong class="visually-hidden">Warning</strong>
                            <div class="ds_warning-text__text">
                                We've detected from your browser that JavaScript is disabled.
                                Please enable JavaScript to use this page.
                            </div>
                        </div>
                    </noscript>
                </div>

                <div class="ds_layout__subsection-progress">
                    <div id="subsection-progess-indicator"></div>
                </div>

                <#if document.relateditems?has_content >
                    <!--noindex-->
                    <aside class="ds_layout__sidebar">
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
                    </aside>
                    <!--endnoindex-->
                </#if>

                <#include 'feedback-wrapper.ftl'>
            </main>
        </div>
    </div>
</@hst.messagesReplace>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Form"/>
</@hst.headContribution>

</#if>


<#if document.formdisabled?? && document.formdisabled>
<#else>
    <script
        src="https://code.jquery.com/jquery-3.5.1.min.js"
        integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
        crossorigin="anonymous"></script>
    <#assign scriptName="${document.formtype}">
</#if>
<#include 'scripts.ftl'/>
