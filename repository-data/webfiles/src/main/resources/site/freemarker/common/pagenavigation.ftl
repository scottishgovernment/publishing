<#ftl output_format="HTML">
<#include "include/imports.ftl">
<#-- this component has no visible output on the live site - it only sets which
     top level navigation item this page should be considered part of, see
     PageNavigationComponent and site-navigation.ftl -->
<#if editMode>
    <div class="ds_wrapper  cms-visible-if-show-components" style="padding: 5px 0; position: relative;">
        <small>Top level navigation item: <#if topLevelNavItem?has_content>${topLevelNavItem}<#else>(not set)</#if></small>
    </div>
</#if>
