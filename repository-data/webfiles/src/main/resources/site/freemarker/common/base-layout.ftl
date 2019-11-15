<#assign hst=JspTaglibs["http://www.hippoecm.org/jsp/hst/core"] >
<!doctype html>
<html>
    <head>
        <meta name="google-site-verification" content="vSrfYRrCbfMqGoggpdhADL9rvDQm70LtAHalmFmZ4wE"/>

        <@hst.headContributions categoryIncludes="dataLayer"/>
        <!-- dataLayer code MUST be higher than google tag manager code -->
        <@hst.headContributions categoryIncludes="googleTagManager"/>
        <@hst.headContributions categoryIncludes="resourcehints"/>
        <@hst.headContributions categoryIncludes="title"/>
        <@hst.headContributions categoryIncludes="schema"/>
        <@hst.headContributions categoryIncludes="meta"/>

        <!-- Older browsers load these files -->
        <!-- (and module-supporting browsers know *not* to load these files) -->
        <script nomodule="" src="<@hst.webfile path='/assets/tradingnation/scripts/usertype.es5.js'/>"></script>

        <!-- Browsers with ES module support load these files -->
        <script type="module" src="<@hst.webfile path='/assets/tradingnation/scripts/usertype.js'/>"></script>

        <meta charset="UTF-8">

        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" type="text/css" href="<@hst.webfile path="/assets/tradingnation/css/main.css"/>"/>
        <link href='https://fonts.googleapis.com/css?family=Roboto:100,300,400,500,700,900,400italic' rel='stylesheet' type='text/css'>

    </head>

    <body>
        <@hst.include ref="googletagmanager"/>

        <div class="ds_page">
            <div class="ds_page__top">
                <@hst.include ref="notifications"/>
                <@hst.include ref="siteheader"/>
            </div>

            <div class="ds_page__main">
                <@hst.include ref="main"/>
            </div>

            <div class="ds_page__footer">
                <@hst.include ref="footer"/>
            </div>
        </div>

        <#include "breakpoint-tests.ftl">

        <!-- Older browsers load these files -->
        <!-- (and module-supporting browsers know *not* to load these files) -->
        <script nomodule src="<@hst.webfile path='/assets/tradingnation/scripts/site.es5.js'/>"></script>
        <script nomodule src="<@hst.webfile path='/assets/tradingnation/scripts/pattern-library.es5.js'/>"></script>

        <!-- Browsers with ES module support load these files -->
        <script type="module" src="<@hst.webfile path='/assets/tradingnation/scripts/site.js'/>"></script>
        <script type="module" src="<@hst.webfile path='/assets/tradingnation/scripts/pattern-library.js'/>"></script>
    </body>
</html>
