// DEFAULT

'use strict';

import feedback from '../components/feedback';

import AsyncSearch from '../components/async-search';

const searchPage = {
    init: function () {
        this.asyncSearch = new AsyncSearch();
        this.asyncSearch.init();
        feedback.init();
    }
};

window.format = searchPage;
window.format.init();

export default searchPage;
