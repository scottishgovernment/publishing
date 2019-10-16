<!doctype html>
<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%@ page isErrorPage="true" %>
<% response.setStatus(404); %>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <title>404 error</title>
</head>
<body>
<div class="cms-editable">
  <div class="ds_wrapper">
    <main class="ds_layout  ds_layout--tn-article">
      <div class="ds_layout__header">
        <header class="ds_page-header">
          <h1 class="ds_page-header__title">404 - not found</h1>
        </header>
      </div>

      <div class="ds_layout__content">
        <p>
          Sorry, but the page you were trying to view does not exist.
        </p>

        <p>
          This could be the result of either:

        <ul>
          <li>a mistyped address</li>
          <li>an out of date link</li>
        </ul>
        </p>
      </div>
    </main>
  </div>
</div>
</body>
</html>
