<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<#include "../common/macros/lang-attributes.ftl">
<#assign variables = hstRequestContext.getAttribute("variables")/>

<!--noindex-->
<#if document??>
    <@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">

    <#if document.includeheaderfooter?? && document.includeheaderfooter>
    <div class="<#if document.cssclass??>${document.cssclass}</#if>">
    <div class="ds_skip-links">
        <ul class="ds_skip-links__list">
            <li class="ds_skip-links__item"><a class="ds_skip-links__link" href="#main-content">Skip to main content</a></li>
        </ul>
    </div>
    <span id="page-top"></span>
    <div class="ds_page">

        <div class="ds_page__top">
            <header class="ds_site-header">
                <div class="ds_wrapper">
                    <div class="ds_site-header__content">
                        <div class="ds_site-branding">
                            <a class="ds_site-branding__logo ds_site-branding__link" href="#">
                                <#if document.headerlogo?has_content>
                                    <#if document.headerlogo == "Mygov">
                                <img class="ds_site-branding__logo-image" src="/assets/images/logos/mygov.svg" alt="Mygov.scot" width="300" height="58" />
                                    <#else>
                                <img class="ds_site-branding__logo-image" src="/assets/images/logos/scottish-government.svg" alt="The Scottish Government" width="300" height="45" />
                                    </#if>
                                <#else>
                                <img class="ds_site-branding__logo-image" src="/assets/images/logos/scottish-government.svg" alt="The Scottish Government" width="300" height="45" />
                                </#if>
                            </a>
                            <#if document.headertitle?has_content>
                            <div class="ds_site-branding__title">
                                ${document.headertitle}
                            </div>
                            </#if>
                        </div>
                    </div>
                </div>
            </header>

        </div>

        <div class="ds_page__middle">
    <#else>
    <div class="example-frame__intro">
        <h1 class="visually-hidden">
            ${document.title}
        </h1>
    </div>
    <div class="example-frame__content  <#if document.cssclass??>${document.cssclass}</#if>">
    </#if>

        ${document.code?no_esc}

    <#if document.includeheaderfooter?? && document.includeheaderfooter>

        </div>

        <div class="ds_back-to-top" data-module="ds-back-to-top">
            <a href="#page-top" class="ds_back-to-top__button">Back to top <svg class="ds_icon  ds_back-to-top__icon" aria-hidden="true" role="img"><use href="/assets/images/icons/icons.stack.svg#arrow_upward"></use></svg></a>
        </div>

        <div class="ds_page__bottom">
            <footer class="ds_site-footer">
                <div class="ds_wrapper">
                    <div class="ds_site-footer__content">
                        <ul class="ds_site-footer__site-items">
                            <li class="ds_site-items__item">
                                <a href="#">Privacy</a>
                            </li>
                            <li class="ds_site-items__item">
                                <a href="#">Cookies</a>
                            </li>
                            <li class="ds_site-items__item">
                                <a href="#">Accessibility</a>
                            </li>
                        </ul>
                        <div class="ds_site-footer__copyright">
                            <span class="ds_site-footer__copyright-logo"><img src="/assets/images/logos/ogl.svg" alt="Open Government License" width="41" height="17" loading="lazy"/></span>
                            <p>All content is available under the <a href="https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/">Open Government Licence v3.0</a>, except for graphic assets and where otherwise stated</p>
                            <p>&copy; Crown Copyright</p>
                        </div>

                        <div class="ds_site-footer__org">
                            <a class="ds_site-footer__org-link" title="The Scottish Government" href="https://www.gov.scot/">
                                <img loading="lazy" width="300" height="57" class="ds_site-footer__org-logo" src="/assets/images/logos/scottish-government--min.svg" alt="gov.scot" />
                            </a>
                        </div>
                    </div>
                </div>
            </footer>
        </div>

    </div>

    </#if>

    </div>
    </@hst.messagesReplace>

    <#if document.script?has_content>
        <@hst.headContribution category="footerScripts">
            <script type="module" src='<@hst.webfile path="assets/scripts/${document.script}.js"/>'></script>
        </@hst.headContribution>

        <@hst.headContribution category="footerScripts">
            <script nomodule="true" src='<@hst.webfile path="assets/scripts/${document.script}.es5.js"/>'></script>
        </@hst.headContribution>
    </#if>

    <#if document.includedsscript>
        <#assign scriptName="ds-example">
        <#assign noGlobal=true>
        <#include '../common/scripts.ftl'/>
    </#if>

</#if>

<@hst.headContribution category="meta">
    <meta name="dc.format" content="Article"/>
</@hst.headContribution>

<@hst.headContribution category="resourcehints">
    <#if nextlink??>
        <link rel="prerender" href="${nextlink}"/>
    </#if>
</@hst.headContribution>
<!--endnoindex-->
