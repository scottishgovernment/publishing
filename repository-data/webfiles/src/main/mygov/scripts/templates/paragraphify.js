/* global module */

'use strict';

module.exports = function (input) {
    if (!input) {
        return;
    }

    var paras = input.split('\n\n');

    var html = '';

    for (var i = 0, il = paras.length; i < il; i++) {
        html+= '<p>' + paras[i] + '</p>';
    }

    return html;
};
