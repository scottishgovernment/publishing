/* global module */

'use strict';

module.exports = function (number) {
    if (!number || isNaN(number)) {
        return null;
    }
    return '£' + (number * 1).toFixed(2);
};
