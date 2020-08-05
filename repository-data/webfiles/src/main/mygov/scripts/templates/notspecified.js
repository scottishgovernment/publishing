/* global module */

'use strict';

module.exports = function (value) {
    if (value) {
        return value;
    } else {
        return '<span class="summary__blank" title="not specified"></span>';
    }
};
