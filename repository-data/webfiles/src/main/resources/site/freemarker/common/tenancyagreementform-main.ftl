<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">

<#if document??>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
    <div class="cms-editable">
        <@hst.manageContent hippobean=document />

        <div class="ds_wrapper">
            <#if document.startpage??>
                <a class="ds_back-link" href="<@hst.link hippobean=document.startpage/>">Back <span class="visually-hidden">to '${document.startpage.title}'</span></a>
            </#if>

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

            <input type="hidden" id="recaptchaSitekey" value="${recaptchaSitekey}"/>
            <input type="hidden" id="recaptchaEnabled" value="${recaptchaEnabled?c}"/>

            <main id="main-content" class="ds_layout  mg_layout--form">
                <div class="ds_layout__error  js-error-summary-container">
                    <div class="ds_error-summary  fully-hidden  client-error" aria-labelledby="error-summary-title" role="alert" aria-live="assertive">
                        <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>

                        <p>There were some errors found on this page:</p>

                        <div class="form-errors">

                        </div>
                    </div>
                </div>

                <div class="ds_layout__content">
                    <div id="form-content" class="mg_display-contents"></div>
                </div>

                <div class="ds_layout__sidebar">
                    <div id="form-progress">
                    </div>

                    <!--noindex-->
                    <#if document.relateditems?has_content >
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
                    </#if>
                    <!--endnoindex-->
                </div>
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
</#if>

<#assign scriptName="model-tenancy-form-2025">
<#include 'scripts.ftl'/>
