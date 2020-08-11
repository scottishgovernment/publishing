<#include "imports.ftl">
<div class="search-box welcome__search-box ">
    <form role="search" class="search-box__form" method="GET" action="<@hst.link path='/search/'/>">
        <label class="search-box__label hidden" for="search-box">Search</label>
        <input name="q" required="" id="search-box" role="combobox" class="search-box__input" type="text" placeholder="Search mygov.scot" autocomplete="off">
        <input name="cat" value="sitesearch" hidden>
        <button type="submit" title="search" class="search-box__button button button--primary">
            <span class="icon icon--search-white"></span>
            <span class="hidden">Search mygov.scot</span>
        </button>
    </form>
</div>
