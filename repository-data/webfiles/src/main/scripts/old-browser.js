/**
 * Reveals a warning about using old browsers.
 * Currently based on a browser list from the QA docs:  Prefer feature-sniffing (which could also
 * let us have more detailed warnings for the user).
 */

'use strict';

var browser = {
    isIe: function () {
        return navigator.appVersion.indexOf('MSIE') != -1;
    },
    navigator: navigator.appVersion,
    getVersion: function() {
        var version = 11; // we assume a sane browser
        if (navigator.appVersion.indexOf('MSIE') !== -1) {
            // bah, IE again, lets downgrade version number
            version = parseFloat(navigator.appVersion.split('MSIE')[1]);
        }
        return version;
    }
};

var isSupportedBrowser = function () {
    return (!(browser.isIe() && browser.getVersion() < 11));
};

var addEvent = function (evnt, elem, func) {
    if (elem.addEventListener) {
        elem.addEventListener(evnt,func,false);
    }
    else if (elem.attachEvent) {
        elem.attachEvent('on' + evnt, func);
    }
    else {
        elem[evnt] = func;
    }
};

var browserNotice = document.getElementById('browser-notice');
var showBrowserNotice = false;

if (!isSupportedBrowser()) {
    if (typeof sessionStorage !== 'undefined' && typeof sessionStorage.getItem === 'function') {
        if (sessionStorage.getItem('browser-notification-acknowledged') !== 'true') {
            browserNotice.removeClass('fully-hidden');

            showBrowserNotice = true;
        }
    } else {
        showBrowserNotice = true;
    }
}

if (showBrowserNotice) {
    browserNotice.className = browserNotice.className.replace(new RegExp('(^|\\b)' + 'fully-hidden'.split(' ').join('|') + '(\\b|$)', 'gi'), ' ');
}

// bind a click handler to the close button
addEvent('click', browserNotice, function (event) {
    event.preventDefault();

    browserNotice.className += ' hidden';

    if (typeof sessionStorage !== 'undefined' && typeof sessionStorage.setItem === 'function') {
        sessionStorage.setItem('browser-notification-acknowledged', 'true');
    }
});
