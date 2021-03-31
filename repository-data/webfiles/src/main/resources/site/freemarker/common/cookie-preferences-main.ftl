<#ftl output_format="HTML">
<#include "include/imports.ftl">

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
                <@hst.html hippohtml=document.content/>

                <div class="form-box" id="cookie-form-box">
                    <form id="cookie-preferences">
                        <fieldset>
                            <legend class="fully-hidden">Turn cookies on or off</legend>

                            <div class="ds_question  ds_no-margin--top">
                                <h3 class="beta  no-top-margin">Cookies needed for the website to work</h3>
                                <p>These cookies do things like keep the website secure. They always need to be on.</p>
                            </div>

                            <div class="ds_question">
                                <h3 class="beta">Cookies that remember your settings</h3>
                                <p>These cookies do things like remember pop-ups you’ve seen, so you're not shown them again.</p>
                                <div class="ds_field-group  ds_field-group--inline">
                                    <div class="ds_radio">
                                        <input id="preferences-yes" value="true" name="cookie-preferences" class="ds_radio__input" type="radio" checked="true">
                                        <label for="preferences-yes" class="ds_radio__label">On</label>
                                    </div>

                                    <div class="ds_radio">
                                        <input id="preferences-no" value="false" name="cookie-preferences" class="ds_radio__input" type="radio">
                                        <label for="preferences-no" class="ds_radio__label">Off</label>
                                    </div>
                                </div>
                            </div>

                            <div class="ds_question">
                                <h3 class="beta">Cookies that measure website use</h3>
                                <p>These cookies store information about how you use our website, such as what you click on.</p>
                                <div class="ds_field-group  ds_field-group--inline">
                                    <div class="ds_radio">
                                        <input id="statistics-yes" value="true" name="cookie-statistics" class="ds_radio__input" type="radio" checked="true">
                                        <label for="statistics-yes" class="ds_radio__label">On</label>
                                    </div>

                                    <div class="ds_radio">
                                        <input id="statistics-no" value="false" name="cookie-statistics" class="ds_radio__input" type="radio">
                                        <label for="statistics-no" class="ds_radio__label">Off</label>
                                    </div>
                                </div>
                            </div>

                            <button class="ds_button  ds_no-margin" type="submit">Save cookie preferences</button>
                        </fieldset>
                    </form>
                </div>

                <@hst.html hippohtml=document.additionalContent/>
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

<#include 'scripts.ftl'/>
