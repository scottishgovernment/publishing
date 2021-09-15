//global.js
/*
 Contains functionality required for all pages
 */

'use strict';

import $ from 'jquery';
import './usertype';
import storage from './tools/storage';
import NotificationBanner from './components/notification';
import finders from './components/content-select';
import './vendor/polyfills';

import '../../../node_modules/@scottish-government/pattern-library/src/all';

const global = {
    notes: [],

    init: function() {
        this.initCookieNotice();
        this.validateSearchForm();
        this.setInitialCookiePermissions();
        this.initNotifications();
        this.initDesignSystemComponents();
        this.addTracking();

        finders.init();
    },

    addTracking: function () {
        if (window.DS.tracking) {
            window.DS.tracking.add.backtotop = function (scope = document) {
                const backToTops = [].slice.call(scope.querySelectorAll('.ds_back-to-top__button'));
                backToTops.forEach(backToTop => {
                    if (!backToTop.classList.contains('js-has-tracking-event')) {
                        backToTop.addEventListener('click', () => {
                            window.dataLayer = window.dataLayer || [];
                            window.dataLayer.push({
                                event: 'backToTop',
                                scrollDepthAbs: window.scrollY,
                                scrollDepthRel: +(window.scrollY / window.innerHeight).toFixed(3)
                            });
                        });
                        backToTop.classList.add('js-has-tracking-event');
                    }
                });
                window.DS.tracking.add.backToTop(scope);
            };

            window.DS.tracking.init();
        }
    },

    initNotifications: function () {
        const notificationBanners = [].slice.call(document.querySelectorAll('[data-module="ds-notification"]'));
        notificationBanners.forEach(notificationBanner => new NotificationBanner(notificationBanner).init());
    },

    initDesignSystemComponents: function () {
        const backToTopEl = document.querySelector('[data-module="ds-back-to-top"]');
        if (backToTopEl) {
            const backToTop = new window.DS.components.BackToTop(backToTopEl);
            backToTop.init();
        }

        // need to preprocess accordion items to group them
        const accordionItems = [].slice.call(document.querySelectorAll('.ds_accordion-item'));
        const groups = [];
        let groupItems = [];
        accordionItems.forEach(accordionItem => {
            if(accordionItem.parentNode.classList.contains('ds_accordion')) {
                return;
            }
            groupItems.push(accordionItem);
            if (!(accordionItem.nextElementSibling && accordionItem.nextElementSibling.classList.contains('ds_accordion-item'))) {
                groups.push(groupItems);
                groupItems = [];
            }
        });
        groups.forEach(groupItems => {
            const wrapper = document.createElement('div');
            wrapper.classList.add('ds_accordion');
            wrapper.dataset.module = 'ds-accordion';

            if (groupItems.length > 1) {
                const openAllButton = document.createElement('button');
                openAllButton.setAttribute('class', 'ds_link  ds_accordion__open-all  js-open-all');
                openAllButton.innerHTML = 'Open all <span class="visually-hidden">sections</span>';
                wrapper.appendChild(openAllButton);
            }

            groupItems[0].parentNode.insertBefore(wrapper, groupItems[0]);
            groupItems.forEach(item => {
                wrapper.appendChild(item);
            });
        });

        const accordions = [].slice.call(document.querySelectorAll('[data-module="ds-accordion"]'));

        // filter out some we don't want to process yet
        const filteredAccordions = accordions.filter(accordion => {
            return !accordion.closest('.multi-page-form');
        });

        filteredAccordions.forEach(accordion => new window.DS.components.Accordion(accordion).init());

        const cookieNotificationEl = document.querySelector('[data-module="ds-cookie-notification"]');
        if (cookieNotificationEl) {
            const cookieNotification = new window.DS.components.CookieNotification(cookieNotificationEl);
            cookieNotification.init();
        }

        // this one is handled differently because it applies an event to the whole body and we only want that event once
        const hidePageButtons = [].slice.call(document.querySelectorAll('.ds_hide-page'));
        if (hidePageButtons.length) {
            const hidePage = new window.DS.components.HidePage();
            hidePage.init();
        }

        const mobileMenus = [].slice.call(document.querySelectorAll('[data-module="ds-mobile-navigation-menu"]'));
        mobileMenus.forEach(mobileMenu =>  new window.DS.components.MobileMenu(mobileMenu).init());

        const sideNavigations = [].slice.call(document.querySelectorAll('[data-module="ds-side-navigation"]'));
        sideNavigations.forEach(sideNavigation => new window.DS.components.SideNavigation(sideNavigation).init());

        const tables = [].slice.call(document.querySelectorAll('table[data-smallscreen]'));
        if (tables.length) {
            const mobileTables = new window.DS.components.MobileTables();
            mobileTables.init();
        }

        const tabSets = [].slice.call(document.querySelectorAll('[data-module="ds-tabs"]'));
        tabSets.forEach(tabSet => new window.DS.components.Tabs(tabSet).init());

        window.DS.components.skipLinks.init();
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
    }
};

global.init();

export default global;
