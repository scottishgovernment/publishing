// ARTICLE

'use strict';

import feedback from '../components/feedback';

const article = {};

article.init = function() {
    feedback.init();
};

window.format = article;
window.format.init();

export default article;
