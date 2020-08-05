/* global module */

'use strict';

module.exports = function (a, b, options) {
    if (a.match(b)) {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
};
