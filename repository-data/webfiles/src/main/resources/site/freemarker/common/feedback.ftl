<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#if isFeedbackEnabled = true>
    <!--noindex-->
    <div lang="en" class="ds_layout__feedback">
        <div class="mg_feedback">

            <#if feedbackDocument??>
                <h2>${feedbackDocument.title}</h2>
                <div class="ds_!_margin-bottom--6">
                    <@renderContentBlocks feedbackDocument.contentBlocks />
                </div>
            </#if>

            <#if document??>
                <#assign documentUuid = document.uuid/>
            <#else>
                <#assign documentUuid = ""/>
            </#if>
            <input id="documentUuid" type="hidden" name="uuid" value="${documentUuid}"/>
            <section id="feedback">
                <div class="js-error-summary-container  form-errors" role="alert"></div>

                <div class="js-confirmation-message  ds_confirmation-message  fully-hidden" aria-live="polite">
                    <svg class="ds_confirmation-message__icon  ds_icon  ds_icon--24" aria-hidden="true" role="img"><use href="${iconspath}#check_circle"></use></svg>
                    <div class="ds_confirmation-message__title  ds_h3  ds_no-margin">
                        Thanks for your feedback
                    </div>
                </div>

                <input id="page-category" type="hidden" value="${layoutName}" form=feedbackForm">

                <form id="feedbackForm">

                </form>
            </section>
        </div>
    </div>
    <!--endnoindex-->
</#if>

