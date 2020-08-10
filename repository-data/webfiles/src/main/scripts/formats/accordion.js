// ACCORDION
/*
 Contains functionality for the different accordion
 */

'use strict';

import feedback from '../components/feedback';

const accordion = {};

accordion.init = function() {
    //Initialise the on page feedback widget
    feedback.init();
};

window.format = accordion;
window.format.init();

export default accordion;
