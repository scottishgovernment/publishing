<#ftl output_format="HTML">
<#include "imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#assign term = ''/>
<#if hstRequestContext.servletRequest.getParameter("q")??>
    <#assign term = hstRequestContext.servletRequest.getParameter("q") />
</#if>

<div class="ds_site-search  ds_autocomplete" data-module="ds-autocomplete">
    <form role="search" class="ds_site-search__form" method="GET" action="<@hst.link path='/search/'/>">
        <label class="ds_label  visually-hidden" for="site-search">Search</label>
        <div id="autocomplete-status" class="visually-hidden"></div>
        <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
            <input
                aria-autocomplete="list"
                aria-expanded="false"
                aria-owns="autocomplete-suggestions"
                autocomplete="off"
                class="ds_input  ds_site-search__input  js-autocomplete-input"
                haspopup="true"
                id="site-search"
                name="q"
                placeholder="Search"
                required=""
                type="search"
                value="${term}"
            />

            <button type="submit" class="ds_button  ds_button--icon-only  js-site-search-button">
                <span class="visually-hidden">Search</span>
                <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#search"></use></svg>
            </button>

            <div id="autocomplete-suggestions" class="ds_autocomplete__suggestions">
                <ol class="ds_autocomplete__suggestions-list" role="listbox" aria-labelledby="site-search-label"></ol>
            </div>
        </div>
    </form>
</div>
