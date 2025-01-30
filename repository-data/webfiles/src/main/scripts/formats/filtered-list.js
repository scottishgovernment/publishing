// FILTERED LIST PAGE FORMAT
// Contains functionality for the news landing page

/* global window */

'use strict';

import SearchWithFilters from '../components/landing-filters';

const filteredListPage = {
    init: function () {
        this.SearchWithFilters = new SearchWithFilters();
        this.SearchWithFilters.init();
    }
};

window.format = filteredListPage;
window.format.init();

export default filteredListPage;