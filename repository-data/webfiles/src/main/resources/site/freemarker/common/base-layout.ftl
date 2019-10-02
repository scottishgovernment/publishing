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


        <style type="text/css">
            html {
                min-height: 100%;
                position: relative;
            }

            body {
                min-height: 100vh;
                margin-bottom: 350px;
            }

            .ds_site-footer {
                position: absolute;
                bottom: 0;
                left: 0;
                right: 0;
            }

            @supports (display: grid) {
                body {
                    display: grid;
                    margin-bottom: 0;
                    min-height: 100vh;
                    grid-template-rows: auto 1fr auto;
                }

                .ds_site-footer {
                    position: static;
                }
            }
        </style>

        <style>
            .ds_page__main {
                display: grid;
                grid-template-rows: auto 1fr;
                position: relative;
            }

            .category-lower {
                padding-bottom: 70px;
                background: #fafafa;
                border-top: 1px solid #ebebeb;
            }
        </style>
    </body>
</html>
