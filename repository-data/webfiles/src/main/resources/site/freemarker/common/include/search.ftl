<#include "imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#assign term = ''/>
<#if hstRequestContext.servletRequest.getParameter("q")??>
    <#assign term = hstRequestContext.servletRequest.getParameter("q")?j_string />
</#if>

<div class="ds_site-search">
    <form role="search" class="ds_site-search__form" method="GET" action="<@hst.link path='/search/'/>">
        <label class="ds_label  visually-hidden" for="site-search">Search</label>

        <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
            <input value="${term}" name="q" required="" id="site-search" class="ds_input  ds_site-search__input" type="text" placeholder="Search" autocomplete="off" />

            <button type="submit" class="ds_button  ds_button--icon-only  js-site-search-button">
                <span class="visually-hidden">Search</span>
                <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#search"></use></svg>
            </button>
        </div>
    </form>
</div>
