<#include "../common/include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
<div class="cms-editable">
    <@hst.manageContent hippobean=document />

    <div class="category-upper">
        <div class="ds_wrapper">
            <div class="ds_category-header  mg_hero">
                <header class="ds_category-header__header">
                    <h1 class="ds_category-header__title">Access to public services in Scotland</h1>
                    <div class="ds_site-search  " data-module="ds-site-search">
                        <form role="search" class="ds_site-search__form" method="GET" action="/search/">
                            <label class="ds_label  visually-hidden" for="site-search">Search</label>

                            <div class="ds_input__wrapper  ds_input__wrapper--has-icon  ds_no-margin">
                                <input name="q" required="" role="combobox" id="site-search" class="ds_input  ds_site-search__input" type="text" placeholder="Search mygov.scot" autocomplete="off" aria-owns="autocomplete-results" aria-autocomplete="list" aria-haspopup="true" aria-expanded="false" aria-activedescendant="" data-form="textinput-search">
                                <div id="autocomplete-status" role="status" aria-live="polite" class="hidden"></div>
                                <input type="hidden" value="sitesearch" name="cat">

                                <button type="submit" class="ds_button  ds_button--icon-only  js-site-search-button">
                                    <span class="visually-hidden">Search</span>
                                    <svg class="ds_icon" role="img"><use xlink:href="${iconspath}#search"></use></svg>
                                </button>
                            </div>
                        </form>
                    </div>
                </header>

                <div class="ds_category-header__aside  ds_category-header__media-container">
                    <div class="mg_hero-links">
                        <@hst.link var="about" path="/about-mygovscot"/>
                        <a class="mg_hero-links__link" href="${about}" data-gtm="link-hero">About</a>
                        <@hst.link var="feedback" path="/give-feedback"/>
                        <a class="mg_hero-links__link" href="${feedback}" data-gtm="link-hero">Feedback</a>
                    </div>

                    <p>
                        We’re working with over 150 organisations to deliver their services online.
                    </p>

                    <@hst.link var="organisations" path="/organisations"/>
                    <a class="ds_button  ds_button--has-icon  ds_button--max  mg_hero-az  ds_no-margin" href="${organisations}">
                        <span class="mg_hero-az__text">
                            <span class="large">A-Z</span>
                            <span class="small">of Scotland's<br>organisations</span>
                        </span>

                        <span class="ds_icon mg_hero-az__icon"></span>
                    </a>

                </div>
            </div>
        </div>
    </div>
</div>

<div class="category-lower">
    <div class="ds_wrapper">
        <#if document.prologue??>
            <@hst.html hippohtml=document.prologue/>
        </#if>
        <div class="ds_layout  ds_layout--category-list">
            <#if document.popularpanelitem?hasContent>
                <div class="ds_layout__content">
                    <div class="popular">
                        <h2 class="popular__title">Popular on mygov.scot:</h2>
                        <ul class="popular__items">
                            <#list document.popularpanelitem as items>
                                <li class="popular__item">
                                    <@hst.link var="link" hippobean=items.link/>
                                    <svg class="popular__icon"></svg>
                                    <a class="popular__link" href="${link}" data-popular="${items.title}">${items.title}</a>
                                </li>
                            </#list>
                        </ul>
                    </div>
                </div>
            </#if>
            <#if document.navigationType == "list">
            <div class="ds_layout__list">
            <#else>
            <div class="ds_layout__grid">
            </#if>
                <div class="ds_category-list-container">
                    <#if document.navigationType == "image-card">
                        <#include '../common/card-navigation--image.ftl'/>
                    </#if>
                    <#if document.navigationType == "card">
                        <#include '../common/card-navigation.ftl'/>
                    </#if>
                    <#if document.navigationType == "grid">
                        <#include '../common/grid-navigation.ftl'/>
                    </#if>
                    <#if document.navigationType == "list">
                        <#include '../common/list-navigation.ftl'/>
                    </#if>
                </div>
            </div>
        </div>

        <#if document.epilogue??>
            <@hst.html hippohtml=document.epilogue/>
        </#if>

        <div class="ds_layout__feedback">
            <#include '../common/feedback-wrapper.ftl'>
        </div>
    </div>
</div>
</#if>

<@hst.headContribution category="title">
    <#if document??>
        <title>${document.title}</title>
    </#if>
</@hst.headContribution>

<@hst.headContribution category="meta">
    <#if document??>
        <meta name="description" content="${document.metaDescription?html}"/>
    </#if>
</@hst.headContribution>

<#assign scriptName="home">
<#include '../common/scripts.ftl'/>
