/* global sa_event */

(function (_window) {
    'use strict';

    const options = {
        // What to collect
        outbound: true,
        emails: true,
        downloads: true,
        // Downloads: enter file extensions you want to collect
        downloadsExtensions: ['.csv', '.doc', '.docx', '.obr', '.pdf', '.ppt', '.pptx', '.rtf', '.txt', '.xls', '.xlsm', '.xlsx', '.xml', '.zip'],

        // All: use title attribute if set for event name (for all events)
        // THIS TAKES PRECEDENCE OVER OTHER SETTINGS BELOW
        title: true,
        // Outbound: use full URL of the links? false for just the hostname
        outboundFullUrl: false,
        // Downloads: if taking event name from URL, use full URL or just filename (default)
        downloadsFullUrl: false,

        // classNames to exclude
        exclusions: ['js-hide-page']
    };

    function logmessage (message, loglevel) {
        return ('warn' === loglevel ? console.warn : console.log)('Simple Analytics automated events: ' + message);
    }

    function hasClass(target, className) {
        return new RegExp('(\\s|^)' + className + '(\\s|$)').test(target.className);
    }

    function isExcluded(link, exclusions) {
        var exclude = false;
        for (var i = 0, il = exclusions.length; i < il; i++) {
            if (hasClass(link, exclusions[i])) {
                exclude = true;
            }
        }

        return exclude;
    }

    function addAutomatedLinkAttribute() {
        try {
            for (var element, elements = document.getElementsByTagName('a'), i = 0; i < elements.length; i++) {

                if (options.exclusions && isExcluded(elements[i], options.exclusions)) {
                    continue;
                }

                if (!(element = elements[i]).getAttribute('onclick')) {
                    var linktype;
                    if (
                        (options.downloads && /^https?:\/\//i.test(element.href) && new RegExp('.(' + (options.downloadsExtensions || []).join('|') + ')', 'i').test(element.pathname) ?
                            (linktype = 'download') :
                            options.outbound && /^https?:\/\//i.test(element.href) && element.hostname !== _window.location.hostname ?
                                (linktype = 'outbound') :
                                options.emails && /^mailto:/i.test(element.href) ? (linktype = 'email') :
                                    linktype = false,
                        linktype)
                    ) {
                        var attributeValue = 'saAutomatedLink(this, "' + linktype + '");';
                        (element.hasAttribute('target') && '_self' !== element.hasAttribute('target')) || (attributeValue += ' return false;'), element.setAttribute('onclick', attributeValue);
                    }
                }
            }
        } catch (error) {
            logmessage(error.message, 'warn');
        }
    }
    if ('undefined' !== typeof _window) {
        void 0 === options && logmessage('options object not found, please specify', 'warn'),
        (_window.saAutomatedLink = function (linkelement, linktype) {
            try {
                if (!linkelement) {
                    return logmessage('no element found');
                }

                var linkfound = false,
                    o = function () {
                        linkfound || linkelement.hasAttribute('target') || (document.location = linkelement.getAttribute('href')), (linkfound = true);
                    };
                if (_window.sa_event) {
                    var title,
                        hostname = linkelement.hostname,
                        pathname = linkelement.pathname,
                        hasTitle = false;
                    if (options.title && linkelement.hasAttribute('title')) {
                        var trimmedTitle = linkelement.getAttribute('title').trim();
                        '' !== trimmedTitle && (hasTitle = true);
                    }
                    if (hasTitle) {
                        title = trimmedTitle;
                    }
                    else {
                        switch (linktype) {
                        case 'outbound':
                            title = hostname + (options.outboundFullUrl ? pathname : '');
                            break;
                        case 'download':
                            title = options.downloadsFullUrl ? hostname + pathname : pathname.split('/').pop();
                            break;
                        case 'email':
                            title = (linkelement.getAttribute('href').split(':')[1] || '').split('?')[0];
                        }
                    }

                    var titleWithType = linktype + '_' + title.replace(/[^a-z0-9]+/gi, '_').replace(/(^_+|_+$)/g, '');

                    return sa_event(titleWithType, o), logmessage('collected ' + titleWithType), _window.setTimeout(o, 5e3);
                }
                return logmessage('sa_event is not defined', 'warn'), o();
            } catch (error) {
                logmessage(error.message, 'warn');
            }
        }),
        _window.addEventListener('DOMContentLoaded', addAutomatedLinkAttribute);
    }
})(window);
