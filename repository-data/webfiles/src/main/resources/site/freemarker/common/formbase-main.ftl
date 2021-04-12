<#ftl output_format="HTML">
<#include "include/imports.ftl">

<#if document??>
    <div class="cms-editable">
        <@hst.manageContent hippobean=document />

        <@hst.include ref="breadcrumbs"/>

        <div class="ds_wrapper">
            <main id="main-content" class="ds_layout  mg_layout--paged-form">
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

                <div class="ds_layout__section-progress">
                    <div id="section-progess-indicator"></div>
                </div>

                <div class="ds_layout__form-content">
                    <div class="multi-page-form" name="${document.formtype}">
                        <div class="ds_error-summary  fully-hidden  client-error" id="feedback-box" aria-labelledby="error-summary-title" role="alert" aria-live="assertive">
                            <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>

                            <p>There were some errors found on this page:</p>

                            <div class="form-errors">

                            </div>
                        </div>

                        <script src="https://www.google.com/recaptcha/api.js"></script>
                        <div id="form-container">
                            <@hst.html hippohtml=document.content/>
                        </div>
                    </div>
                </div>

                <div class="ds_layout__subsection-progress">
                    <div id="subsection-progess-indicator"></div>
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
                                            <a href="${url}">${link.title}</a>
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

<@hst.headContribution category="meta">
<#if document??>
    <meta name="description" content="${document.metaDescription}"/>
</#if>
</@hst.headContribution>

<script>
window.renderCaptcha = function () {
    if (document.getElementById('recaptcha')) {
        grecaptcha.render(
            document.getElementById('recaptcha')
        );
    }
}
</script>

<script src="https://www.google.com/recaptcha/api.js?onload=renderCaptcha&render=explicit" async defer></script>

<#assign scriptName="${document.formtype}">
<#include 'scripts.ftl'/>
