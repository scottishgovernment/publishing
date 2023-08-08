<#if hideSearch>
<#elseif searchEnabled>
<div class="ds_site-header__search">
    <#if autoCompleteEnabled>
        <#assign ds_autocomplete = true />
    </#if>
    <#assign searchcategory = "sitesearch" />
    <#include '../common/include/search.ftl'/>
</div>
</#if>
