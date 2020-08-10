/**
 * Reveals a warning about using old browsers.
 * Currently based on a browser list from the QA docs:  Prefer feature-sniffing (which could also
 * let us have more detailed warnings for the user).
 */

'use strict';

var isSupportedBrowser = function() {
    var userAgentId = window.navigator.userAgent;
    var isSupported = false;

    // Early versions of IE8 were marked as Mozilla-4-compatible; let them through.
    // Reject outdated IE browser versions.
    var notMozCompatible = (userAgentId.match(/^Mozilla\/5.0/) === null) &&
        ((userAgentId.match(/^Mozilla\/4.0/) !== null) && userAgentId.match(/MSIE 8/gi) === null);
    var tooOldIe = userAgentId.match(/MSIE [765]/gi) !== null;
    var notOpera = userAgentId.match(/^Opera/) === null;

    if (!(notMozCompatible && notOpera) && !(tooOldIe)) {
        // The browser is marked as compatible with Mozilla 5, or is Opera, and isn't an old IE, and
        // therefore might be in the supported list.
        isSupported = true;
    }

    return isSupported;
};

var addEvent = function (evnt, elem, func) {
   if (elem.addEventListener) {
      elem.addEventListener(evnt,func,false);
   }
   else if (elem.attachEvent) {
      elem.attachEvent('on'+evnt, func);
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
