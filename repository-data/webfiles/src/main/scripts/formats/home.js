// home

'use strict';

import feedback from '../components/feedback';

const home = {};

home.init = function () {
    feedback.init();
};

window.format = home;
window.format.init();

export default home;
