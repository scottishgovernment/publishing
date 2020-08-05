//global.js
/*
 Contains functionality required for all pages
 */

'use strict';

import $ from 'jquery';
import storage from './storage';
import gup from './gup';
import autoComplete from '../vendor/jquery.auto-complete.es6';
import './component.tooltip';
import './component.brexit-form';
import './component.disclosure-pvg-form';
import './component.disclosure-bdo-form';
import './component.letting-agent-registration-form';
import NotificationBanner from './component.notification';

import {initAll} from '../../../../../node_modules/@scottish-government/pattern-library/src/all';
initAll();

$.fn.autoComplete = autoComplete;

const slugify = function(string) {

    return string
        // Make lower-case
        .toLowerCase()
        // Remove misc punctuation
        .replace(/['"’‘”“`]/g, '')
        // Replace non-word characters with dashes
        .replace(/[\W|_]+/g, '-')
        // Remove starting and trailing dashes
        .replace(/^-+|-+$/g, '');

};

const global = {
    notes: [],

    init: function() {
        let that = this;

        this.initMourningBanner();
        this.initDecommissionedSiteNotice();
        this.initCookieNotice();
        this.initResponsiveSearchBar();
        this.validateSearchForm();
        this.initSearchAutocomplete();
        this.lafinderHandler();
        this.setInitialCookiePermissions();
        this.initNotifications();

        if ($('#leave-this-page').length > 0) {
            this.initSensitiveContent();

            $(document).on('keyup',function(evt) {
                if (evt.keyCode === 27) {
                    that.goToBaseURL();
                }
            });
        }
    },

    initNotifications: function () {
        const notificationBanners = [].slice.call(document.querySelectorAll('[data-module="ds-notification"]'));
        notificationBanners.forEach(notificationBanner => new NotificationBanner(notificationBanner).init());
    },

    setInitialCookiePermissions: function () {
        const permissionsString = storage.getCookie('cookiePermissions') || '';

        if (!storage.isJsonString(permissionsString)) {
            const permissions = {};
            permissions.statistics = true;
            permissions.preferences = true;

            storage.setCookie(storage.categories.necessary,
                'cookiePermissions',
                JSON.stringify(permissions)
            );
        }
    },

    lafinderHandler: function(){
        const laFinders = $('.dd.finder-hero');
        const laSelects = laFinders.find('select');

        const showButton = function(selectElement){
            const finder = selectElement.closest('.dd.finder-hero');
            const linkElement = finder.find('#dd-' + selectElement.find('option:selected').data('id'));

            linkElement.removeClass('fully-hidden').attr('aria-hidden', false);
            linkElement.siblings('a').addClass('fully-hidden').attr('aria-hidden', true);
        };

        // on loading or returning to the page, any dropdowns with a LA selected will show a link button
        window.setTimeout(function () {
            for (let i = 0; i < laSelects.length; i++){
                if (laSelects[i].value !== ''){
                    showButton($(laSelects[i]));
                }
            }
        }, 0);

        // when any dropdown changes, loads a new button to link to that LA
        laFinders.on('change', 'select', function () {
            const selectElement = $(this);
            showButton(selectElement);
        });
    },

    validateSearchForm: function() {
        $('#searchForm').submit(function() {
            const term = $('#search-box').val();
            if (term === undefined || term === '') {
                return false;
            }
        });
    },

    /**
     * handles changes to search bar behaviour based on responsive breakpoints
     */
    initResponsiveSearchBar: function() {
        const $sb = $('.header-bar .search-box');
        $('button', $sb).on('click', function(ev) {
            $('#search-box').focus();
            if (!$('.search-box input').val()) {
                ev.preventDefault();
                $sb.toggleClass('search-box--expanded');
                $('#search-box').focus();
            }
        });
    },

    /**
     * Checks whether to display the cookie notice
     * Binds click handler to the cookie notice close button
     */
    initCookieNotice: function() {
        const cookieNotice = $('#cookie-notice');

        // check whether we need to display the cookie notice
        if (!storage.get({type: storage.types.cookie, name: 'cookie-notification-acknowledged'})) {
            cookieNotice.removeClass('fully-hidden');
            this.notes.push(cookieNotice);
        }

        // bind a click handler to the close button
        cookieNotice.on('click', '.js-accept-cookies', function(event) {
            event.preventDefault();

            const cookiePermissions = JSON.parse(JSON.stringify(storage.categories));
            for (const key in cookiePermissions) {
                if (!cookiePermissions.hasOwnProperty(key)) {continue;}

                cookiePermissions[key] = true;
            }

            storage.setCookie(
                storage.categories.necessary,
                'cookiePermissions',
                JSON.stringify(cookiePermissions),
                365
            );

            storage.setCookie(
                storage.categories.necessary,
                'cookie-notification-acknowledged',
                'yes',
                365
            );

            document.querySelector('.js-initial-cookie-content').classList.add('fully-hidden');
            document.querySelector('.js-confirm-cookie-content').classList.remove('fully-hidden');
        });
    },

    inArrayOfObjects: function(array, property){
        let inArray = false;
        array.map(function(i){
            if (i.selector === property) {
                inArray = true;
                return inArray;
            }
        });
        return inArray;
    },

    initMourningBanner : function () {
        $.ajax({
            type: 'GET',
            url: '/banners.json'
        })
            .done(function(data) {
                if (data.mourning.display === true) {
                    const bannerHtml = '<div id="mourning-banner" class="notification-mourning-banner">' +
                            '<div class="wrapper"><div class="notification__main-content"></div></div>' +
                            '</div>';
                    $('#notifications-wrapper').append(bannerHtml);
                }
            });
    },

    /**
     * Examine headers to see if this page comes from a decommissioned site
     **/
    initDecommissionedSiteNotice: function() {
        const that = this;
        const decommissionedUrl = gup('via');
        if (decommissionedUrl) {
            const link = document.createElement('a');
            link.href = decommissionedUrl;
            $('#decomissioned-site-host').text(link.hostname);

            const notice = $('#decommissioned-site-notice');
            notice.removeClass('fully-hidden');
            this.notes.push(notice);

            // bind a click handler to the close button
            notice.on('click', '.close-notification', function(event) {
                event.preventDefault();
                that.repositionContent(event);
                notice.addClass('fully-hidden');
            });
        }
    },

    /**
     * Set up search autocomplete
     */
    initSearchAutocomplete: function () {

        $('#search-box').autoComplete({
            minChars: 3,
            cache: false,
            after: '#autocomplete-status',
            source: function (term, response) {
                $.getJSON(
                    '/service/search/complete',
                    {
                        q: term
                    },
                    function (data) {
                        let i, item, suggestions=[];
                        const root = data.suggest.complete[0];
                        if (root.options.length > 0) {
                            for(i=0; i<root.options.length; i++) {
                                item = root.options[i];
                                suggestions.push([
                                    item._source.title,
                                    item._source.url
                                ]);
                            }
                        }

                        response(suggestions);
                    }
                );
            },
            renderItem: function (item, search){
                search = search.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
                const re = new RegExp('(' + search.split(' ').join('|') + ')', 'gi');
                return `<li role="option" id="${slugify(item[0])}" class="autocomplete-suggestion" tabindex="-1" aria-selected="false">
                    <a class="autocomplete__link" data-val="${search}" href="${item[1]}">${item[0].replace(re, '<b class="autocomplete__highlight">$1</b>')}</a>
                </li>`;
            },
            onSelect: function (ev, term, item) {
                // const redirectUrl = $(item).attr('data-url');

                // Log the event to analytics.
                window.dataLayer = window.dataLayer || [];
                window.dataLayer.push({
                    'term' : term,
                    'target' : item.href,
                    'event' : 'search-auto'
                });

                // // Prevent pressing 'enter' from submitting the search form.
                ev.preventDefault();
                // window.location = redirectUrl;
            }
        });
    },

    /**
     * goToBaseURL - Function used by 'hide this page' button and ESC hotkey to return to base URL Page
     */
    goToBaseURL: function() {
        // this clears the DOM, hiding the content immediately even if
        // the page we're navigating to is slow to load
        $('body').empty();
        $('head').empty();

        // hide this page from browser history
        if (window.history.replaceState) {
            window.history.replaceState({}, '', '/');
        }

        window.setTimeout(function () {
            if (!window.location.origin) {
                window.location.href= window.location.protocol + '//' + window.location.hostname + (window.location.port ? ':' + window.location.port: '') +'/';
            } else {
                window.location.href = window.location.origin + '/';
            }
        }, 10);
    }
};

global.init();

export default global;
