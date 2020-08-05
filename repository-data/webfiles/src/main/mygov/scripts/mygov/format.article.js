// ARTICLE

'use strict';

import stickyBackToTop from './sticky-back-to-top';
import feedback from './feedback';

const article = {};

article.init = function() {
    feedback.init();
    stickyBackToTop.init();
};

window.format = article;
window.format.init();

export default article;
