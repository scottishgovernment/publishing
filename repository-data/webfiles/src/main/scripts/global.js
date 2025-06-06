/* global document, window */
/*
 Contains functionality required for all pages
 */

'use strict';

import BloomreachWebfile from './tools/bloomreach-webfile';
import feedback from './components/feedback';
import NotificationBanner from './components/notification';
import ServiceFinder from './components/service-finder';
import ToggleLink from './components/toggle-link';
import UpdateHistory from './components/update-history';
import storage from '../../../node_modules/@scottish-government/design-system/src/base/tools/storage/storage';

import './vendor/polyfills';
import '../../../node_modules/@scottish-government/design-system/src/all';

const global = {
    notes: [],

    init: function () {
        this.initCookieNotice();
        this.setInitialCookiePermissions();
        this.initNotifications();
        this.initDesignSystemComponents();
        this.initPublishingComponents();
        this.addTracking();
        this.checkVideoConsent();

        feedback.init();

        const hasCodeExamples = document.querySelectorAll('pre > code[class*="language-"]').length > 0;
        if (hasCodeExamples) {
            const script = document.createElement('script');
            script.src = BloomreachWebfile('/assets/scripts/syntax-highlight.js');
            document.head.appendChild(script);
        }
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
            };

            window.DS.tracking.init();
        }
    },

    checkVideoConsent: function () {
        const videoElements = [].slice.call(document.querySelectorAll('.youtube-embed-wrapper'));

        videoElements.forEach(videoElement => {

            if (storage.hasPermission(storage.categories.marketing)) {
                videoElement.innerHTML = `
                <iframe width="${videoElement.dataset.width}" height="${videoElement.dataset.height}" src="${videoElement.dataset.src}" frameborder="0" allowfullscreen></iframe>`;
            } else {
                videoElement.innerHTML = `
                    <div class="youtube-embed-wrapper__consent">
                        <h2 class="ds_h3">Permission required to view YouTube content</h2>
                        <p>This video content is hosted on YouTube and we require permission before loading as there may be cookies and/or other technologies used not covered by <a href="/cookies">this site's cookie preferences</a>.</p>
                        <p>We advise reading Google's <a href="https://policies.google.com/privacy?hl=en">privacy policy</a> before accepting.</p>
                        <p>To consent to display this content click the ‘Accept and continue’ button below.</p>
                        <button class="ds_button  ds_!_margin-bottom--2  js-video-opt-in" type="button">Accept and continue</button>
                    </div>
                `;

                videoElement.addEventListener('click', event => {
                    if (event.target.classList.contains('js-video-opt-in')) {

                        const permissionsString = storage.getCookie('cookiePermissions') || '';

                        let permissions;

                        if (!storage.isJsonString(permissionsString)) {
                            permissions = {};


                            storage.setCookie(storage.categories.necessary,
                                'cookiePermissions',
                                JSON.stringify(permissions)
                            );
                        } else {
                            permissions = JSON.parse(permissionsString);
                        }

                        permissions.marketing = true;

                        storage.setCookie(storage.categories.necessary,
                            'cookiePermissions',
                            JSON.stringify(permissions)
                        );

                        this.checkVideoConsent();
                    }
                });
            }
        });
    },

    initNotifications: function () {
        const notificationBanners = [].slice.call(document.querySelectorAll('[data-module="ds-notification"]'));
        notificationBanners.forEach(notificationBanner => new NotificationBanner(notificationBanner).init());
    },

    initDesignSystemComponents: function () {
        window.DS.base.page.init();

        const backToTopEl = document.querySelector('[data-module="ds-back-to-top"]');
        if (backToTopEl) {
            const backToTop = new window.DS.components.BackToTop(backToTopEl);
            backToTop.init();
        }

        const accordionItems = [].slice.call(document.querySelectorAll('.ds_accordion-item'));

        const parentlessAccordionItems = accordionItems.filter(item => !item.closest('.ds_accordion'));

        // add indicator element to each accordion item
        parentlessAccordionItems.forEach(accordionItem => {
            if (!accordionItem.querySelector('.ds_accordion-item__indicator')) {
                const indicatorElement = document.createElement('span');
                const titleElement = accordionItem.querySelector('.ds_accordion-item__title');
                indicatorElement.classList.add('ds_accordion-item__indicator');
                titleElement.after(indicatorElement);
            }
        });

        // need to preprocess accordion items to group them
        const groups = [];
        let groupItems = [];

        parentlessAccordionItems.forEach(accordionItem => {
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

        const aspectBoxes = [].slice.call(document.querySelectorAll('.ds_aspect-box:not(.ds_aspect-box--fallback)'));
        aspectBoxes.forEach(aspectBox => new window.DS.components.AspectBox(aspectBox).init());

        const autocompletes = [].slice.call(document.querySelectorAll('[data-module="ds-autocomplete"]'));
        autocompletes.forEach(autocomplete => {
            let url = window.location.origin + document.getElementById('site-root-path').value;
            if (!url.endsWith('/')) {
                url += '/';
            }
            url += 'search/suggestions?q=';
            const mapping = ppp => {
                const suggestionsObj = JSON.parse(ppp.responseText);
                return suggestionsObj.map(suggestionsObj => ({
                    key: suggestionsObj,
                    displayText: suggestionsObj,
                    weight: null,
                    type: null,
                    category: null
                }));
            };
            const autocompleteModule = new window.DS.components.Autocomplete(autocomplete, url, { suggestionMappingFunction: mapping });
            autocompleteModule.init();
        });

        const checkboxesModules = [].slice.call(document.querySelectorAll('[data-module="ds-checkboxes"]'));
        checkboxesModules.forEach(checkboxes => new window.DS.components.Checkboxes(checkboxes).init());

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

        const stepNavigations = [].slice.call(document.querySelectorAll('[data-module="ds-step-navigation"]'));
        stepNavigations.forEach(stepNavigation => new window.DS.components.StepNavigation(stepNavigation).init());

        const tables = [].slice.call(document.querySelectorAll('table[data-smallscreen]'));
        if (tables.length) {
            const mobileTables = new window.DS.components.MobileTables();
            mobileTables.init();
        }

        const tabSets = [].slice.call(document.querySelectorAll('[data-module="ds-tabs"]'));
        tabSets.forEach(tabSet => new window.DS.components.Tabs(tabSet).init());

        window.DS.components.skipLinks.init();

        const toggleLinks = [].slice.call(document.querySelectorAll('[data-module="ds-toggle-link"]'));
        toggleLinks.forEach(toggleLink => new ToggleLink(toggleLink).init());

        const updateHistory = document.querySelector('[data-module="ds-update-history"]');
        if (updateHistory) {
            const updateHistoryModule = new UpdateHistory(updateHistory);
            updateHistoryModule.init();
        }
    },

    initPublishingComponents: function () {
        const serviceFinderEls = [].slice.call(document.querySelectorAll('.js-service-finder'));
        serviceFinderEls.forEach(serviceFinder => new ServiceFinder(serviceFinder).init());
    },

    setInitialCookiePermissions: function () {
        const permissionsString = storage.getCookie('cookiePermissions') || '';

        if (!storage.isJsonString(permissionsString)) {
            const permissions = {};
            permissions.statistics = false;
            permissions.preferences = true;

            storage.setCookie(storage.categories.necessary,
                'cookiePermissions',
                JSON.stringify(permissions)
            );
        }
    },

    /**
     * Checks whether to display the cookie notice
     * Binds click handler to the cookie notice close button
     */
    initCookieNotice: function() {
        const cookieNotice = document.getElementById('cookie-notice');

        // check whether we need to display the cookie notice
        if (!storage.get({type: storage.types.cookie, name: 'cookie-notification-acknowledged'})) {
            cookieNotice.classList.remove('fully-hidden');
            this.notes.push(cookieNotice);
        }

        // bind a click handler to the close button
        cookieNotice.addEventListener('click', function (event) {
            if (event.target.classList.contains('js-accept-all-cookies')) {
                event.preventDefault();
                window.initGTM();
            }
        });
    }
};

global.init();

export default global;
