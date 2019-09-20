<#include "include/imports.ftl">

<header class="ds_site-header  ds_site-header--gradient" role="banner">
    <div class="ds_wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <a data-header="header-logo" class="ds_site-branding__logo  ds_site-branding__link" href="/">
                    <img class="ds_site-branding__logo-image" src="<@hst.webfile path="/assets/images/logos/scottish-government.svg"/>" alt="The Scottish Government" />
                </a>

                <div class="ds_site-branding__title">
                    <a data-header="header-title" class="ds_site-branding__link" href="/">A Trading Nation</a>
                </div>
            </div>

            <div class="ds_site-header__search  ds_site-search  ds_site-search--collapsible">

                <form role="search" class="ds_site-search__form">
                    <label class="ds_site-search__label visually-hidden" for="site-search">Search</label>

                    <div class="ds_site-search__input-group">
                        <input name="q" required="" id="site-search" class="ds_site-search__input" type="text" placeholder="Search" autocomplete="off" />

                        <button type="submit" title="search" class="ds_site-search__button  button  button--primary  js-site-search-button">
                            <svg class="ds_icon  ds_site-search__icon" role="img"><use xlink:href="/assets/images/icons/icons.stack.svg#search"></use></svg>
                            <span class="visually-hidden">Search mygov.scot</span>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</header>
