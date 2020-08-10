// GUIDE

'use strict';

import feedback from '../components/feedback';

const guide = {
    init: function () {
        //Initialise the feedback handlers
        feedback.init();
    }
};

window.format = guide;
window.format.init();

export default guide;
