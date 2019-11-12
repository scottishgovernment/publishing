<#include "include/imports.ftl">

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <@hst.include ref="breadcrumbs"/>

    <div class="ds_wrapper">
        <main class="ds_layout  ds_layout--tn-article">
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

                            <div class="form-group  no-top-margin">
                                <h3 class="beta  no-top-margin">Cookies needed for the website to work</h3>
                                <p>These cookies do things like keep the website secure. They always need to be on.</p>
                            </div>

                            <div class="form-group">
                                <h3 class="beta">Cookies that remember your settings</h3>
                                <p>These cookies do things like remember pop-ups you’ve seen, so you're not shown them again.</p>
                                <div class="input-wrapper">
                                    <input id="preferences-yes" value="true" name="cookie-preferences" class="fancy-radio" type="radio" checked="true">
                                    <label for="preferences-yes" class="inline fancy-radio">On</label>

                                    <input id="preferences-no" value="false" name="cookie-preferences" class="fancy-radio" type="radio">
                                    <label for="preferences-no" class="inline fancy-radio">Off</label>
                                </div>
                            </div>

                            <div class="form-group">
                                <h3 class="beta">Cookies that measure website use</h3>
                                <p>These cookies store information about how you use our website, such as what you click on.</p>
                                <div class="input-wrapper">
                                    <input id="statistics-yes" value="true" name="cookie-statistics" class="fancy-radio" type="radio" checked="true">
                                    <label for="statistics-yes" class="inline fancy-radio">On</label>

                                    <input id="statistics-no" value="false" name="cookie-statistics" class="fancy-radio" type="radio">
                                    <label for="statistics-no" class="inline fancy-radio">Off</label>
                                </div>
                            </div>

                            <button class="button  button--primary  no-bottom-margin" type="submit">Save cookie preferences</button>
                        </fieldset>
                    </form>
                </div>
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

<@hst.headContribution>
    <#if document??>
        <meta name="description" content="${document.metaDescription?html}"/>
    </#if>
</@hst.headContribution>
