<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#include "macros/content-blocks.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <#if document.contentBlocks??>
                    <@renderContentBlocks document.contentBlocks />
                </#if>

                <div class="ds_!_margin-top--4  ds_!_margin-bottom--8" id="cookie-form-box">
                    <form id="cookie-preferences">
                        <h3>Cookies needed for the website to work</h3>
                        <p>These cookies do things like keep the website secure. They always need to be on.</p>

                        <fieldset class="ds_question" aria-describedby="explanatory-text-preferences">
                            <legend>Cookies that remember your settings</legend>
                            <p id="explanatory-text-preferences">These cookies do things like remember pop-ups you’ve seen, so you're not shown them again.</p>
                            <div class="ds_field-group">
                                <div class="ds_radio">
                                    <input id="preferences-yes" value="true" name="cookie-preferences" class="ds_radio__input" type="radio" checked="true">
                                    <label for="preferences-yes" class="ds_radio__label">On</label>
                                </div>

                                <div class="ds_radio">
                                    <input id="preferences-no" value="false" name="cookie-preferences" class="ds_radio__input" type="radio">
                                    <label for="preferences-no" class="ds_radio__label">Off</label>
                                </div>
                            </div>
                        </fieldset>

                        <fieldset class="ds_question" aria-describedby="explanatory-text-statistics">
                            <legend>Cookies that measure website use</legend>
                            <p id="explanatory-text-statistics">These cookies store information about how you use our website, such as what you click on.</p>
                            <div class="ds_field-group">
                                <div class="ds_radio">
                                    <input id="statistics-yes" value="true" name="cookie-statistics" class="ds_radio__input" type="radio" checked="true">
                                    <label for="statistics-yes" class="ds_radio__label">On</label>
                                </div>

                                <div class="ds_radio">
                                    <input id="statistics-no" value="false" name="cookie-statistics" class="ds_radio__input" type="radio">
                                    <label for="statistics-no" class="ds_radio__label">Off</label>
                                </div>
                            </div>
                        </fieldset>

                        <fieldset class="ds_question" aria-describedby="explanatory-text-marketing">
                            <legend>Cookies that help with our communications and marketing</legend>
                            <p id="explanatory-text-marketing">These cookies may be set by third party websites and do things like measure how you view YouTube videos that are on this website.</p>
                            <div class="ds_field-group">
                                <div class="ds_radio">
                                    <input id="marketing-yes" value="true" name="cookie-marketing" class="ds_radio__input" type="radio" checked="true">
                                    <label for="marketing-yes" class="ds_radio__label">On</label>
                                </div>

                                <div class="ds_radio">
                                    <input id="marketing-no" value="false" name="cookie-marketing" class="ds_radio__input" type="radio">
                                    <label for="marketing-no" class="ds_radio__label">Off</label>
                                </div>
                            </div>
                        </fieldset>

                        <div id="cookie-success-message" class="ds_confirmation-message  fully-hidden" aria-atomic="true" aria-live="polite">
                            <svg class="ds_confirmation-message__icon  ds_icon  ds_icon--24" aria-hidden="true" role="img"><use href="${iconspath}#check_circle"></use></svg>
                            <div class="ds_confirmation-message__title  ds_h3  ds_no-margin">
                                Success
                            </div>
                            <div class="js-cookie-confirmation-content" data-content="Your cookie preferences have been saved"></div>
                        </div>

                        <button class="ds_button  ds_no-margin" type="submit">Save cookie preferences</button>
                    </form>
                </div>

                <#if document.additionalContentBlocks??>
                    <@renderContentBlocks document.additionalContentBlocks />
                </#if>
            </div>
        </main>
    </div>
</div>
</#if>

<#assign scriptName="cookie-preferences">
<#include 'scripts.ftl'/>
