<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<@hst.link var="home" siteMapItemRefId="root" />
<header class="ds_site-header" role="banner">
    <div class="ds_wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <a class="ds_site-branding__logo  ds_site-branding__link" href="/">
                    <img class="ds_site-branding__logo-image" src="<@hst.webfile path="/assets/images/logos/mygov.svg" />" alt="mygov.scot">
                </a>
            </div>

            <#if hideSearch>
            <#else>
            <div class="ds_site-header__search">
                <div class="ds_site-search  ds_autocomplete" data-module="ds-autocomplete">
                    <form role="search" class="ds_site-search__form" method="GET" action="<@hst.link path='/search/'/>">
                        <label class="ds_label  visually-hidden" for="site-search">Search</label>
                        <div id="autocomplete-status" class="visually-hidden"></div>
                        <div class="ds_input__wrapper  ds_input__wrapper--has-icon  ds_no-margin">
                            <input
                                aria-autocomplete="list"
                                aria-expanded="false"
                                aria-owns="autocomplete-suggestions"
                                autocomplete="off"
                                class="ds_input  ds_site-search__input  js-autocomplete-input"
                                haspopup="true"
                                id="site-search"
                                name="q"
                                placeholder="Search mygov.scot"
                                required=""
                                type="search"
                                value="${term}"
                            >
                            <input type="hidden" value="sitesearch" name="cat">

                            <button type="submit" class="ds_button  js-site-search-button">
                                <span class="visually-hidden">Search</span>
                                <svg class="ds_icon" role="img"><use href="${iconspath}#search"></use></svg>
                            </button>

                            <div id="autocomplete-suggestions" class="ds_autocomplete__suggestions">
                                <ol class="ds_autocomplete__suggestions-list" role="listbox" aria-labelledby="site-search-label"></ol>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            </#if>
        </div>
    </div>
</header>
