/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import orglist from '../../../src/main/scripts/formats/organisation-list';

describe('the organisation list page', function () {

    beforeEach(function () {
        window.ga = jasmine.createSpy('ga');
        loadFixtures('orglistpage.html');
    });

    describe('the organisation list tabs', function () {

        it('should switch to the relevant content on when a tab is activated', function () {
            orglist.init();

            var $tabsContainer = $('.org-view-tabs');

            // find an inactive tab and click on it
            var $targetTab = $tabsContainer.find('.active').siblings().find('a'),
                $targetContent = $($targetTab.attr('href'));

            $targetTab.trigger('click');

            expect($targetTab.parent().hasClass('active')).toBeTruthy();
            expect($targetContent.hasClass('active')).toBeTruthy();
        });

        it('should store a tab choice in sessionStorage', function () {
            orglist.init();

            sessionStorage.removeItem('tabs-state');

            var $tabsContainer = $('.org-view-tabs');

            // find an inactive tab and click on it
            var $targetTab = $tabsContainer.find('.active').siblings().find('a');

            $targetTab.trigger('click');

            var expectedValue = {
                "orgview": $targetTab.attr('href')
            };

            expect(sessionStorage.getItem('tabs-state')).toEqual(JSON.stringify(expectedValue));
        });

        it('should show a specific tab if a stored tab is set in sessionStorage', function() {
            var inputValue = {
                "orgview": '#bysector'
            };

            sessionStorage.setItem('tabs-state', JSON.stringify(inputValue));

            orglist.init();

            expect($('#bysector').hasClass('active')).toBeTruthy();
        });

        it('should show the first tab if no stored tab is set in sessionStorage', function () {
            sessionStorage.removeItem('tabs-state');

            orglist.init();

            expect($('#atoz').hasClass('active')).toBeTruthy();
        });

    });
});
