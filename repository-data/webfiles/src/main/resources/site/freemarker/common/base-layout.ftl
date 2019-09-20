<#assign hst=JspTaglibs["http://www.hippoecm.org/jsp/hst/core"] >
<!doctype html>
<html>
    <head>
        <meta charset="UTF-8">

        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" type="text/css" href="<@hst.webfile path="/assets/css/main.css"/>"/>
        <link href='https://fonts.googleapis.com/css?family=Roboto:100,300,400,500,700,900,400italic' rel='stylesheet' type='text/css'>

    </head>
    <body>
        <@hst.include ref="notifications"/>
        <@hst.include ref="siteheader"/>
        <@hst.include ref="breadcrumbs"/>
        <@hst.include ref="main"/>
        <@hst.include ref="footer"/>
    </body>
</html>
