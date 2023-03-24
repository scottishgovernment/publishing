// DEFAULT

'use strict';

import AsyncSearch from '../components/async-search';
import feedback from '../components/feedback';

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
