/* global module */

'use strict';

module.exports = function (valueArray, value) {
    for (var i = 0, il = valueArray.length; i < il; i++) {
        if (valueArray[i].value.toString() === value.toString()) {
            return valueArray[i].displayName;
        }
    }
    return '';
};
