<#ftl output_format="HTML">
<#assign hst=JspTaglibs["http://www.hippoecm.org/jsp/hst/core"] >
<!doctype html>
<html lang="en">
    <head>
        <meta name="google-site-verification" content="vSrfYRrCbfMqGoggpdhADL9rvDQm70LtAHalmFmZ4wE"/>

        <@hst.headContributions categoryIncludes="dataLayer"/>
        <!-- dataLayer code MUST be higher than google tag manager code -->
        <@hst.headContributions categoryIncludes="googleTagManager"/>
        <@hst.headContributions categoryIncludes="resourcehints"/>
        <@hst.headContributions categoryIncludes="title"/>
        <@hst.headContributions categoryIncludes="schema"/>
        <@hst.headContributions categoryIncludes="meta"/>
        <@hst.headContributions categoryIncludes="facebookMeta"/>
        <@hst.headContributions categoryIncludes="twitterMeta"/>

        <meta charset="UTF-8">

        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" type="text/css" href="<@hst.webfile path="${css}"/>"/>
        <link href='https://fonts.googleapis.com/css?family=Roboto:100,300,400,500,700,900,400italic' rel='stylesheet' type='text/css'>


    <link rel="shortcut icon" href='<@hst.link path="favicon.ico" />' type="image/x-icon" />
    <link rel="apple-touch-icon" sizes="180x180" href='<@hst.link path="apple-touch-icon.png" />'>
    <link rel="icon" type="image/png" sizes="32x32" href='<@hst.link path="favicon-32x32.png" />'>
    <link rel="icon" type="image/png" sizes="16x16" href='<@hst.link path="favicon-16x16.png" />'>
    <link rel="mask-icon" href='<@hst.link path="safari-pinned-tab.svg" />' color="#0065bd">
    <meta name="msapplication-TileColor" content="#0065bd">
    <meta name="theme-color" content="#ffffff">
    <@hst.headContributions categoryIncludes="facebookVerification"/>

    <@hst.headContributions categoryIncludes="canonical"/>

    <script
  src="https://code.jquery.com/jquery-3.5.1.min.js"
  integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
  crossorigin="anonymous"></script>

    <script>
        var htmlClass = document.documentElement.getAttribute('class') || '';
        document.documentElement.setAttribute('class', (htmlClass ? htmlClass + ' ' : '') + 'js-enabled');
    </script>
    </head>

    <@hst.include ref="seo"/>
    <body <#if document?? && document.sensitive?? &&document.sensitive>class="ds_has-hide-page"</#if>>
        <input type="hidden" id="br-webfile-path" value="<@hst.webfile path="/"/>" />

        <@hst.include ref="googletagmanager"/>

        <#include "skip-links.ftl">

        <@hst.include ref="preview-indicator"/>

        <span id="page-top"></span>

        <#if document?? && document.sensitive?? &&document.sensitive>
            <div class="visually-hidden  ds_hide-page">
                <p>To leave the page quickly, press the escape key.</p>
            </div>
        </#if>

        <div class="ds_page">
            <div class="ds_page__top">
                <@hst.include ref="notifications"/>
                <@hst.include ref="siteheader"/>
                <@hst.include ref="menu"/>
            </div>

            <div class="ds_page__middle">
                <@hst.include ref="main"/>
            </div>

            <#include "back-to-top.ftl">

            <div class="ds_page__bottom">
                <@hst.include ref="footer"/>
            </div>
        </div>

        <#include "breakpoint-tests.ftl">

        <@hst.headContributions categoryIncludes="footerScripts" xhtml=true/>

        <script async defer src="https://sa.mygov.scot/app.js"></script>
        <noscript><img src="https://sa.mygov.scot/image.gif" alt=""></noscript>
    </body>
</html>
