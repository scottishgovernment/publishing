/* global $ */
// CATEGORIES

'use strict';

import feedback from '../components/feedback';

const categories = {
    init: function () {
        feedback.init();
    }
};

window.format = categories;
window.format.init();

export default categories;
