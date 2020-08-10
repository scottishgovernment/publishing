/* global describe beforeEach it expect spyOn $ */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import tooltip from '../../../src/main/scripts/components/tooltip';

describe('tooltips', function () {
    beforeEach(function () {
        loadFixtures('tooltips.html');

        tooltip.init();
    });

    it('should generate expected markup for a tooltip', function () {
        var tooltipOne = $('#tooltipOne');

        var container = tooltipOne.find('.mgs-tooltip__container');
        var content = tooltipOne.find('.mgs-tooltip__content');
        var arrow = tooltipOne.find('.mgs-tooltip__arrow');

        expect(container.length).toEqual(1);
        expect(content.length).toEqual(1);
        expect(arrow.length).toEqual(1);
    });

    it('should show the tooltip on click of the tooltip span', function () {
        var tooltipOne = $('#tooltipOne');

        expect(tooltipOne.hasClass('active')).toBeFalsy();

        tooltipOne.click();

        expect(tooltipOne.hasClass('active')).toBeTruthy();
    });

    it('should hide a shown tooltip on click anywhere else on the page', function () {
        var tooltipOne = $('#tooltipOne');
        tooltipOne.click();

        expect(tooltipOne.hasClass('active')).toBeTruthy();

        // click elsewhere
        var ev = document.createEvent("MouseEvent");
        var el = document.elementFromPoint(100,100);
        ev.initMouseEvent(
            "click",
            true /* bubble */, true /* cancelable */,
            window, null,
            100, 100, 0, 0, /* coordinates */
            false, false, false, false, /* modifier keys */
            0 /*left*/, null
        );
        el.dispatchEvent(ev);

        expect(tooltipOne.hasClass('active')).toBeFalsy();
    });

    it('should allow only one tooltip to be visible at a time', function () {
        var tooltipOne = $('#tooltipOne');
        tooltipOne.click();

        expect(tooltipOne.hasClass('active')).toBeTruthy();

        var tooltipTwo = $('#tooltipTwo');
        tooltipTwo.click();

        expect(tooltipOne.hasClass('active')).toBeFalsy();
        expect(tooltipTwo.hasClass('active')).toBeTruthy();
    });

    it ('should have an explicit "close" button that closes the tooltip', function () {
        var tooltipOne = $('#tooltipOne');
        tooltipOne.click();

        expect(tooltipOne.hasClass('active')).toBeTruthy();

        var tooltipClose = tooltipOne.find('.mgs-tooltip__close');
        tooltipClose.click();

        expect(tooltipOne.hasClass('active')).toBeFalsy();
    });
});
