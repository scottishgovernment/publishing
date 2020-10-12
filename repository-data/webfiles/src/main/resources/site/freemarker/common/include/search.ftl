<#include "imports.ftl">

<div class="ds_site-search">
    <form role="search" class="ds_site-search__form" method="GET" action="<@hst.link path='/search/'/>">
        <label class="ds_label  visually-hidden" for="site-search">Search</label>

        <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
            <input name="q" required="" id="site-search" class="ds_input  ds_site-search__input" type="text" placeholder="Search" autocomplete="off" data-form="textinput-search-term" />

            <button type="submit" class="ds_button  ds_button--icon-only  js-site-search-button" data-button="button-search-submit">
                <span class="visually-hidden">Search</span>
                <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="/assets/images/icons/icons.stack.svg#search"></use></svg>
            </button>
        </div>
    </form>
</div>
