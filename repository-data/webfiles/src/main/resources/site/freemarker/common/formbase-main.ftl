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

                <div class="ds_layout__feedback">

                </div>
            </main>
        </div>
    </div>
</#if>

<@hst.headContribution category="title">
<#if document??>
    <title>${document.title} - A Trading Nation</title>
</#if>
</@hst.headContribution>

<@hst.headContribution category="meta">
<#if document??>
    <meta name="description" content="${document.metaDescription?html}"/>
</#if>
</@hst.headContribution>

<#assign scriptName="${document.formtype}">
<#include 'scripts.ftl'/>
