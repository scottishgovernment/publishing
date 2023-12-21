// PAGINATOR

/**
 * Displays markup for pagination. Events for changing pages are not handled here.
 */

'use strict';

import bloomreachWebfile from '../tools/bloomreach-webfile';
const iconSpritePath = bloomreachWebfile('/assets/images/icons/icons.stack.svg');
const paginationTemplate = require('../templates/mygov/paginator');

/**
 * Constructor, builds a pagination object and inserts it into the current page
 * @param {object} $container - jQuery object for the pagination container
 * @param {number} pagePadding - number of pages to display either side of the current page
 * @param {object} caller - object calling the paginator
 *
 * @returns {{getPages: getPages, renderPages: renderPages, init: init, setParams: setParams, getData: getData}}
 * @constructor
 */
const Paginator = function (container, pagePadding, caller) {

    const paginatorData = {};

    const paginatorFunctions = {

        /**
         * Creates an array of pages to be passed to the template
         * Array contains only pages within "pagePadding" tolerance
         * Array includes ellipses and prev/next as appropriate
         *
         * @param currentPage
         * @returns {Array} pages to display
         */
        getPages: function (currentPage) {
            const maxPadding = pagePadding * 2 + 1;

            const firstPage = Math.max(0, Math.min(currentPage - pagePadding, paginatorData.numberOfPages - maxPadding)),
                lastPage = Math.min(paginatorData.numberOfPages, Math.max(currentPage + pagePadding, maxPadding -1)),
                pages = [];

            // a little clunky, but walk through the pagination

            // check for 'prev' need
            if (currentPage > 0) {
                pages.push({
                    isPrevious: true,
                    displayText: 'Previous',
                    start: (currentPage - 1) * paginatorData.itemsPerPage,
                    isLink: true,
                    iconPath: iconSpritePath
                });
            }

            // check for start ellipsis need
            if (firstPage > 0) {
                if (firstPage === 1) {
                    pages.push(
                        {
                            displayText: 1,
                            start: 0,
                            isLink: true
                        });
                } else if (firstPage >= pagePadding) {
                    pages.push(
                        {
                            displayText: 1,
                            start: 0,
                            isLink: true
                        });
                    pages.push({
                        displayText: '&hellip;',
                        isLink: false,
                        isEllipsis: true
                    });
                }
            }

            // loop through pages to pick out the relevant ones.
            for(let i = 0, il = paginatorData.numberOfPages; i < il; i++) {
                if (i >= firstPage && i <= lastPage) {
                    const page = {
                        displayText: i + 1,
                        start: i * paginatorData.itemsPerPage,
                        isLink: true
                    };

                    if (i === paginatorData.currentPage) {
                        page.isCurrentPage = true;
                    }

                    pages.push(page);
                }
            }

            // check for end ellipsis need
            if (lastPage < paginatorData.numberOfPages-1) {
                if (lastPage === paginatorData.numberOfPages - 2) {
                    pages.push(
                        {
                            displayText: paginatorData.numberOfPages,
                            start: (paginatorData.numberOfPages - 1) * paginatorData.itemsPerPage,
                            isLink: true
                        });
                } else {
                    pages.push({
                        displayText: '&hellip;',
                        isLink: false,
                        isEllipsis: true
                    });
                    pages.push(
                        {
                            displayText: paginatorData.numberOfPages,
                            start: (paginatorData.numberOfPages - 1) * paginatorData.itemsPerPage,
                            isLink: true,
                            isLast: true
                        });
                }

            }

            // check for 'next' need
            if (currentPage + 1 < paginatorData.numberOfPages) {
                pages.push({
                    displayText: 'Next',
                    isNext: true,
                    start: (currentPage + 1) * paginatorData.itemsPerPage,
                    isLink: true,
                    iconPath: iconSpritePath
                });
            }

            // add a lowercase version of the displayText for use by data attributes
            for (let p = 0, pl = pages.length; p < pl; p++) {
                pages[p].dataName = ('' + pages[p].displayText).toLowerCase();
            }

            return pages;
        },

        /**
         * Renders the pagination into a supplied container
         */
        renderPages: function() {
            if (paginatorData.numberOfPages > 1) {
                const paginationData = {
                    callerName: caller.settings.name,
                    firstStart: 0,
                    lastStart: paginatorData.numberOfPages * paginatorData.itemsPerPage,
                    prevStart: (paginatorData.currentPage - 1) * paginatorData.itemsPerPage,
                    nextStart: (paginatorData.currentPage + 1) * paginatorData.itemsPerPage,
                    pages: this.getPages(paginatorData.currentPage)
                };

                if (paginationData.nextStart < paginatorData.hits) {
                    paginationData.canLoadMore = true;
                }

                container.innerHTML = paginationTemplate.render(paginationData);
            } else {
                container.innerHTML = '';
            }
        },

        /**
         * Initialises the paginator, binding events to the links/buttons
         */
        init: function() {
            const that = this;

            if (!container) {
                return;
            }

            container.addEventListener('click', function (event) {
                // quickie polyfill for Element.matches
                if (!Element.prototype.matches) {
                    Element.prototype.matches = Element.prototype.msMatchesSelector || Element.prototype.webkitMatchesSelector;
                }

                if (event.target.matches('a') || event.target.matches('#load-more')) {
                    event.preventDefault();

                    caller.searchParams.from = parseInt(event.target.getAttribute('data-start'), 10);

                    if (window.history.pushState && this.id !== '#load-more') {
                        window.history.pushState(
                            '',
                            '',
                            that.getNewUrlWithPage(caller.searchParams)
                        );
                    }

                    let append = false;

                    if (event.target.id === 'load-more') {
                        append = true;
                    }

                    caller.doSearch(caller.searchParams, append);
                }
            });
        },

        getNewUrlWithPage: function (searchParams) {
            const pageNumber = parseInt(parseInt(searchParams.from / searchParams.size, 10) + 1, 10);
            let newUrlWithPage =  window.location.href.replace(/&page=\d{1,3}/g, '').replace(/\?page=\d{1,3}/g, '');

            if (newUrlWithPage.indexOf('?') > 0) {
                newUrlWithPage += `&page=${pageNumber}`;
            } else {
                newUrlWithPage += `?page=${pageNumber}`;
            }

            return newUrlWithPage;
        },

        /**
         * Sets/updates search parameters from the page which influence the pagination display
         *
         * @param {int} from
         * @param {int} itemsPerPage
         * @param {int} hits
         */
        setParams: function (from, itemsPerPage, hits) {
            paginatorData.itemsPerPage = itemsPerPage;
            paginatorData.hits = hits;

            // calculate current page
            paginatorData.currentPage = parseInt(from, 10) / parseInt(paginatorData.itemsPerPage, 10);

            // calculate number of pages
            paginatorData.numberOfPages = Math.ceil(paginatorData.hits / paginatorData.itemsPerPage);
        },

        /**
         * useful for debugging/test
         */
        getData: function () {
            return paginatorData;
        }
    };

    // initialise the paginator
    paginatorFunctions.init();

    return paginatorFunctions;
};

export default Paginator;
