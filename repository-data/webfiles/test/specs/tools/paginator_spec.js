/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import Paginator from '../../../src/main/scripts/tools/paginator';

describe('a paginator', function () {

    beforeEach(function() {
        // load your fixtures
        loadFixtures('pagination.html');
    });

    it('should be able to store some parameters necessary for population of pagination', function () {
        var fakeCaller = {
            doSearch: function() {},
            searchParams: {}
        };

        var paginator = new Paginator(document.getElementById('pagination'), 3, fakeCaller);

        paginator.setParams(40, 20, 300);

        var paginatorData = paginator.getData();

        expect(paginatorData.itemsPerPage).toEqual(20);
        expect(paginatorData.hits).toEqual(300);
        expect(paginatorData.currentPage).toEqual(2);
        expect(paginatorData.numberOfPages).toEqual(15);
    });

    describe('getPages', function () {
        // case 1: fewer pages than the padding limit
        describe('when there are fewer pages (5) than the padding limit', function () {
            var paginator;

            beforeEach(function () {
                var fakeCaller = {
                    doSearch: function() {},
                    searchParams: {},
                    settings: {
                        name: 'fake-caller'
                    }
                };

                paginator = new Paginator(document.getElementById('pagination'), 3, fakeCaller);
            });

            // 1a. first page
            it('should display five pages and no "prev" link for page one', function () {
                paginator.setParams(0, 10, 50);

                var pages = paginator.getPages(0);

                expect(pages.length).toEqual(6); // 5 pages plus next link
                expect(pages[0].displayText).toEqual(1);
                expect(pages[5].displayText).toEqual('Next');
            });

            // 1b. middle page
            it('should display five pages, a "prev" link, and a "next" link for page two', function () {
                paginator.setParams(10, 10, 50);

                var pages = paginator.getPages(2);

                expect(pages.length).toEqual(7); // 5 pages plus next link plus prev link
                expect(pages[0].displayText).toEqual('Prev');
                expect(pages[6].displayText).toEqual('Next');
            });

            // 1c. last page
            it('should display five pages and no "prev" link for the last page', function () {
                paginator.setParams(40, 10, 50);

                var pages = paginator.getPages(4);

                expect(pages.length).toEqual(6); // 5 pages plus prev link
                expect(pages[0].displayText).toEqual('Prev');
                expect(pages[5].displayText).toEqual(5);
            });
        });

        // case 2: more pages than the padding limit
        describe('when there are more than the padding limit', function () {
            var paginator;

            beforeEach(function () {
                var fakeCaller = {
                    doSearch: function() {},
                    searchParams: {},
                    settings: {
                        name: 'fake-caller'
                    }
                };

                paginator = new Paginator(document.getElementById('pagination'), 3, fakeCaller);
            });

            // 1a. first page
            it('should display seven pages, no "prev" link, and an ellipsis indicating more pages for page one', function () {
                paginator.setParams(0, 10, 100);

                var pages = paginator.getPages(0);

                expect(pages.length).toEqual(9); // 7 pages plus next link plus ellipsis
                expect(pages[0].displayText).toEqual(1);
                expect(pages[7].displayText).toEqual('&hellip;');
                expect(pages[8].displayText).toEqual('Next');
            });

            // 1b. middle page
            it('should display seven pages, no "next" link, and an ellipsis indicating more pages for the last page', function () {
                paginator.setParams(5, 10, 100);

                var pages = paginator.getPages(5);

                expect(pages.length).toEqual(11); // 7 pages plus next link plus ellipsis x 2 plus prev link
                expect(pages[0].displayText).toEqual('Prev');
                expect(pages[1].displayText).toEqual('&hellip;');
                expect(pages[4].displayText).toEqual(5);
                expect(pages[10].displayText).toEqual('Next');
            });

            // 1c. last page
            it('should display seven pages, no "next" link, and an ellipsis indicating more pages for the last page', function () {
                paginator.setParams(9, 10, 100);

                var pages = paginator.getPages(9);

                expect(pages.length).toEqual(9); // 7 pages plus next link plus ellipsis
                expect(pages[0].displayText).toEqual('Prev');
                expect(pages[1].displayText).toEqual('&hellip;');
                expect(pages[8].displayText).toEqual(10);
            });

            // 1d. special case -- don't replace "1" with ellipsis
            it('should not show an ellipsis if the ellipsis would take the place of the "page 1" link', function () {
                paginator.setParams(4, 10, 100);

                var pages = paginator.getPages(4);

                expect(pages.length).toEqual(11); // 7 pages plus next link plus ellipsis
                expect(pages[0].displayText).toEqual('Prev');
                expect(pages[1].displayText).toEqual(1);
                expect(pages[8].displayText).toEqual(8);
            });
        });
    });

    describe('renderPages', function () {
        var containerElement,
            paginator;

        beforeEach(function () {
            var fakeCaller = {
                doSearch: function() {},
                searchParams: {},
                settings: {
                    name: 'fake-caller'
                }
            };

            containerElement = $('#pagination');

            paginator = new Paginator(document.getElementById('pagination'), 3, fakeCaller);

        });

        it('should empty the container element if there are no pages', function () {
            paginator.setParams(0, 10, 0);

            paginator.renderPages();

            expect(containerElement.html()).toEqual('');
        });
    });

    describe('pagination navigation events', function () {

        var containerElement,
            paginator,
            fakeCaller;

        beforeEach(function () {
            fakeCaller = {
                doSearch: function() {},
                searchParams: {
                    size: 10
                },
                settings: {
                    name: 'fake-caller'
                }
            };

            containerElement = $('#pagination');

            paginator = new Paginator(document.getElementById('pagination'), 3, fakeCaller);

            paginator.setParams(0, 10, 100);
            paginator.renderPages();

        });

        xit('should update the page URL param with the destination page on click of a page link', function () {
            // 1. with no existing querystring

            var linkToClick = containerElement.find('li:nth-child(3) button');
            linkToClick.trigger('click');

            expect(window.location.search).toEqual('?page=3');

            // 2. with a search term
            window.history.pushState('','','?q=searchTerm');

            linkToClick = containerElement.find('li:nth-child(2) button');
            linkToClick.trigger('click');
            expect(window.location.search).toEqual('?q=searchTerm&page=2');

            // 3. with a page already set
            window.history.pushState('','','?q=searchTerm&page=234');
            linkToClick.trigger('click');

            expect(window.location.search).toEqual('?q=searchTerm&page=2');
        });

        xit('should navigate to a new page on click of a page link', function () {
            spyOn(fakeCaller, 'doSearch');

            var linkToClick = containerElement.find('li:nth-child(3) a');
            linkToClick.trigger('click');

            expect(fakeCaller.searchParams.from).toEqual(20);

            expect(fakeCaller.doSearch).toHaveBeenCalled();
            expect(fakeCaller.doSearch).toHaveBeenCalledWith(fakeCaller.searchParams, false);
        });

    });
});
