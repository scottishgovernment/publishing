// DEFAULT

'use strict';

import AsyncSearch from '../components/async-search';

const searchPage = {
    init: function () {
        this.asyncSearch = new AsyncSearch();
        this.asyncSearch.init();
    }
};

window.format = searchPage;
window.format.init();

export default searchPage;
