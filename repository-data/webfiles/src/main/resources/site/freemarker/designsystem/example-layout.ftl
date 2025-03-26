<#ftl output_format="HTML">
<#assign hst=JspTaglibs["http://www.hippoecm.org/jsp/hst/core"] >
<!doctype html>
<html lang="en" dir="ltr">
    <head>
        <@hst.headContributions categoryIncludes="dataLayer"/>
        <!-- dataLayer code MUST be higher than google tag manager code -->
        <@hst.headContributions categoryIncludes="googleTagManagerDataLayer"/>
        <@hst.headContributions categoryIncludes="googleTagManager"/>
        <@hst.include ref="plausibleanalytics"/>
        <@hst.headContributions categoryIncludes="resourcehints"/>
        <@hst.headContributions categoryIncludes="meta"/>
        <@hst.headContributions categoryIncludes="schema"/>
        <@hst.headContributions categoryIncludes="title"/>
        <@hst.headContributions categoryIncludes="openGraph"/>
        <@hst.headContributions categoryIncludes="noindex"/>

        <meta charset="UTF-8">

        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" type="text/css" href="<@hst.webfile path="${css}"/>"/>
        <link href='https://fonts.googleapis.com/css?family=Roboto:300,400,700,400italic&display=swap' rel='stylesheet' type='text/css'>

        <link rel="shortcut icon" href='<@hst.link path="favicon.ico" />' type="image/x-icon" />
        <link rel="apple-touch-icon" sizes="180x180" href='<@hst.link path="apple-touch-icon.png" />'>
        <link rel="icon" type="image/png" sizes="32x32" href='<@hst.link path="favicon-32x32.png" />'>
        <link rel="icon" type="image/png" sizes="16x16" href='<@hst.link path="favicon-16x16.png" />'>
        <link rel="mask-icon" href='<@hst.link path="safari-pinned-tab.svg" />' color="#0065bd">
        <meta name="msapplication-TileColor" content="#0065bd">
        <meta name="theme-color" content="#ffffff">
        <meta name="robots" content="noindex">

        <@hst.headContributions categoryIncludes="siteverification"/>
        <@hst.headContributions categoryIncludes="canonical"/>

        <script src='<@hst.webfile path="assets/scripts/js-enabled.js"/>'></script>
    </head>

    <@hst.include ref="seo"/>
    <body>
        <input type="hidden" id="br-webfile-path" value="<@hst.webfile path="/"/>" />
        <input type="hidden" id="site-root-path" value="<@hst.link path="/"/>" />

        <@hst.include ref="googletagmanager"/>

        <!--noindex-->
        <@hst.include ref="preview-indicator"/>
        <!--endnoindex-->

        <@hst.include ref="main"/>

        <@hst.headContributions categoryIncludes="footerScripts" xhtml=true/>
    </body>
</html>
