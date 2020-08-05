'use strict';

import feedback from './feedback';

const fairRentRegisterResult = {};

fairRentRegisterResult.init = function() {
    feedback.init();
};

window.format = fairRentRegisterResult;
window.format.init();

export default fairRentRegisterResult;
