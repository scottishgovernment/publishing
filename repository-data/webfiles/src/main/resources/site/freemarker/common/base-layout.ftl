<#assign hst=JspTaglibs["http://www.hippoecm.org/jsp/hst/core"] >
<!doctype html>
<html>
    <head>
        <@hst.headContributions categoryIncludes="resourcehints"/>
        <@hst.headContributions categoryIncludes="title"/>

        <meta charset="UTF-8">

        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" type="text/css" href="<@hst.webfile path="/assets/css/main.css"/>"/>
        <link href='https://fonts.googleapis.com/css?family=Roboto:100,300,400,500,700,900,400italic' rel='stylesheet' type='text/css'>
    </head>

    <body>

        <style type="text/css">
            body,
            html {
                height: 100%;
                margin-bottom: 0 !important;
            }

            body {
                display: grid;
                min-height: 100%;
                grid-template-rows: auto 1fr auto;
            }
        </style>

        <style>
            .layout-main {
                display: grid;
                grid-template-rows: auto 1fr;
            }

            .category-lower {
                padding-top: 28px;
                padding-bottom: 70px;
                background: #fafafa;
                border-top: 1px solid #ebebeb;
            }
        </style>



        <div class="layout-top">
            <@hst.include ref="notifications"/>
            <@hst.include ref="siteheader"/>
        </div>
        <div class="layout-main">
            <@hst.include ref="main"/>
        </div>
        <div class="layout-footer">
            <@hst.include ref="footer"/>
        </div>
    </body>
</html>
