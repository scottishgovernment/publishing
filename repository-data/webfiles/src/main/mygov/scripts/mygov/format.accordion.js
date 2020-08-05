// ACCORDION
/*
 Contains functionality for the different accordion
 */

'use strict';

import stickyBackToTop from './sticky-back-to-top';
import feedback from './feedback';

const accordion = {};

accordion.init = function() {
    //Initialise the on page feedback widget
    feedback.init();
    stickyBackToTop.init();
};

window.format = accordion;
window.format.init();

export default accordion;
