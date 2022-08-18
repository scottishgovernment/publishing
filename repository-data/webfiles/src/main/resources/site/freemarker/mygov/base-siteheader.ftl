<#ftl output_format="HTML">
<#include "../common/include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<@hst.link var="home" siteMapItemRefId="root" />
<header class="ds_site-header" role="banner">
    <div class="ds_wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <a class="ds_site-branding__logo  ds_site-branding__link" href="/">
                    <img width="300" height="58" class="ds_site-branding__logo-image" src="<@hst.webfile path="/assets/images/logos/mygov.svg" />" alt="mygov.scot">
                </a>
            </div>

            <#if hideSearch>
            <#else>
            <div class="ds_site-header__search">
                <div class="ds_site-search" data-module="ds-site-search">
                    <form role="search" class="ds_site-search__form" method="GET" action="<@hst.link path='/search/'/>">
                        <label class="ds_label  visually-hidden" for="site-search">Search</label>

                        <div class="ds_input__wrapper  ds_input__wrapper--has-icon  ds_no-margin">
                            <input name="q" required="" id="site-search" class="ds_input  ds_site-search__input" type="text" placeholder="Search mygov.scot" autocomplete="off">
                            <input type="hidden" value="sitesearch" name="cat">

                            <button type="submit" class="ds_button  js-site-search-button">
                                <span class="visually-hidden">Search</span>
                                <svg class="ds_icon" role="img"><use href="${iconspath}#search"></use></svg>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
            </#if>
        </div>
    </div>
</header>
