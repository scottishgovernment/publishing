//global.js
/*
 Contains functionality required for all pages
 */

'use strict';

import $ from 'jquery';
import usertype from './usertype';
import storage from './tools/storage';
import gup from './tools/gup';
import './components/tooltip';
import NotificationBanner from './components/notification';
import cookies from './components/cookies';
import finders from './components/finders';

import {initAll} from '../../../node_modules/@scottish-government/pattern-library/src/all';
initAll();

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
        this.validateSearchForm();
        this.setInitialCookiePermissions();
        this.initNotifications();

        finders.init();
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

    validateSearchForm: function() {
        $('#searchForm').submit(function() {
            const term = $('#search-box').val();
            if (term === undefined || term === '') {
                return false;
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
    }
};

global.init();

export default global;
