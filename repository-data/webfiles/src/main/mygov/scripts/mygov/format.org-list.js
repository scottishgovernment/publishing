// ORG LIST
/**
 * Contains functions for the Organisation List Page
 */

'use strict';

import stickyBackToTop from './sticky-back-to-top';
import $ from 'jquery';

const orgListPage = {
    init: function () {
        orgListPage.setupTabs();

        //Now that we have clicked the correct tab, show the page
        $('.org-tab-container').show();

        stickyBackToTop.init();

        // go to relevant tab & hash
        let hash;
        if (window.location.hash.indexOf('?') > -1) {
            hash = window.location.hash.substring(0, window.location.hash.indexOf('?'));
        } else {
            hash = window.location.hash;
        }

        let targetElement = $(hash);
        if (targetElement.length) {
            let targetTab = $('a[href="#' + targetElement.closest('.tab-pane').attr('id') + '"]');
            targetTab.trigger('click');
            window.setTimeout(function () {
                targetElement[0].scrollIntoView();
            }, 500);
        }
    },

    setupTabs: function () {
        // get tab info from sessionStorage variable
        const tabsState = sessionStorage.getItem('tabs-state'),
            json = JSON.parse(tabsState || '{}');

        // bind tab click event
        $('.org-view-tabs').on('click', 'a', function (e) {
            e.preventDefault();

            const parentID = $(e.target).parents('ul.org-view-tabs').attr('id'),
                href = $(e.target).attr('href');

            // update JSON
            json[parentID] = href;

            // update sessionStorage
            sessionStorage.setItem('tabs-state', JSON.stringify(json));

            // change tab
            $(e.target).parent().addClass('active').siblings().removeClass('active');

            // change tab content
            $(href).addClass('active').siblings().removeClass('active');
        });

        // set active tab
        const tabLink = $('a[href=' + json.orgview + ']');
        tabLink.click();
    }
};

window.format = orgListPage;
window.format.init();

export default orgListPage;
