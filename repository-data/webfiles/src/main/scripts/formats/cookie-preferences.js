// COOKIE PREFERENCES

'use strict';

import cookieForm from '../components/cookie-form';
import feedback from '../components/feedback';

const cookiePreferences = {};

cookiePreferences.init = function() {
    cookieForm.init();
    feedback.init();
};

window.format = cookiePreferences;
window.format.init();

export default cookiePreferences;
