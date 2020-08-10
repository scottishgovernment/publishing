/* global module */

'use strict';

module.exports = function (slug) {
    return slug.replace(/(\d+)$/, function (match, $1){return parseInt($1, 10) + 1;});
};
