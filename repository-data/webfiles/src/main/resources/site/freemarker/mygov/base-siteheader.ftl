<#include "../common/include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<@hst.link var="home" siteMapItemRefId="root" />
<header class="ds_site-header" role="banner">
    <div class="ds_wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <a class="ds_site-branding__logo  ds_site-branding__link" href="/">
                    <div class="ds_site-branding__logo-image">
                        <img class="ds_site-branding__logo-image" src="<@hst.webfile path="/assets/images/logos/mygov.svg" />" alt="mygov.scot">
                    </div>
                </a>
            </div>

            <#if hideSearch>
            <#else>
            <div class="ds_site-header__search">
                <div class="ds_site-search" data-module="ds-site-search">
                    <form role="search" class="ds_site-search__form" method="GET" action="/search/">
                        <label class="ds_label  visually-hidden" for="site-search">Search</label>

                        <div class="ds_input__wrapper  ds_input__wrapper--has-icon  ds_no-margin">
                            <input name="q" required="" id="site-search" class="ds_input  ds_site-search__input" type="text" placeholder="Search mygov.scot" autocomplete="off">
                            <input type="hidden" value="sitesearch" name="cat">

                            <button type="submit" class="ds_button  ds_button--icon-only  js-site-search-button">
                                <span class="visually-hidden">Search</span>
                                <svg class="ds_icon" role="img"><use xlink:href="${iconspath}#search"></use></svg>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
            </#if>
        </div>

        <#if hideSearch>
        <#else>
        <nav data-module="ds-mobile-navigation-menu">
            <button class="js-toggle-menu  ds_mobile-navigation__button" aria-expanded="false" aria-controls="mobile-navigation-menu">
                <span class="ds_site-header__control-text">Search</span>

                <svg class="ds_icon  ds_site-header__control-icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#search"></use></svg>
                <svg class="ds_icon  ds_site-header__control-icon--close  ds_site-header__control-icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#close-21"></use></svg>
            </button>

            <div class="ds_mobile-navigation" id="mobile-navigation-menu" data-offsetselector=".ds_site-header">
                <div class="ds_mobile-navigation__content">

                    <div class="ds_mobile-navigation__block">
                        <div class="ds_site-search" data-module="ds-site-search">
                            <form role="search" class="ds_site-search__form" method="GET" action="/search/">
                                <label class="ds_label  visually-hidden" for="site-search">Search</label>

                                <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
                                    <input name="q" required="" id="site-search-mobile" class="ds_input  ds_site-search__input" type="text" placeholder="Search mygov.scot" autocomplete="off">
                                    <input type="hidden" value="sitesearch" name="cat">

                                    <button type="submit" class="ds_button  ds_button--icon-only  js-site-search-button">
                                        <span class="visually-hidden">Search</span>
                                        <svg class="ds_icon" role="img"><use xlink:href="${iconspath}#search"></use></svg>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <button type="button" class="ds_mobile-navigation__backdrop  js-close-menu" aria-expanded="false" aria-controls="mobile-navigation-menu">
                        <span class="visually-hidden">Close search</span>
                    </button>
                </div>
            </div>
        </nav>
        </#if>
    </div>
</header>