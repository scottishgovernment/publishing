/* global jasmine, describe, beforeEach, loadFixtures, ducoment, it, expect */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import ContentSelect from '../../../src/main/scripts/components/content-select';

const testObj = {};

describe('content select', function () {
    describe('button', () => {
        beforeEach(() => {
            loadFixtures('content-select.html');

            testObj.contentSelectEl = document.querySelector('#button');
            testObj.contentSelectModule = new ContentSelect(testObj.contentSelectEl);
        });

        it('should show the relevant button when an item is selected', () => {
            testObj.contentSelectModule.init();

            const selectElement = testObj.contentSelectEl.querySelector('.ds_select');
            const optionToSelect = testObj.contentSelectEl.querySelector('option:nth-child(5)');

            optionToSelect.selected = true;

            const event = new Event('change');
            selectElement.dispatchEvent(event);

            const linkToShow = testObj.contentSelectEl.querySelector('a:nth-of-type(4)');
            expect(linkToShow.classList.contains('fully-hidden')).toBeFalse();
        });

        it('should show nothing if no/null item is selected', () => {
            testObj.contentSelectModule.init();

            const selectElement = testObj.contentSelectEl.querySelector('.ds_select');
            const optionToSelect = testObj.contentSelectEl.querySelector('option:nth-child(1)');

            optionToSelect.selected = true;

            const event = new Event('change');
            selectElement.dispatchEvent(event);

            const linksToShow = [].slice.call(testObj.contentSelectEl.querySelectorAll('a:not(.fully-hidden)'));
            expect(linksToShow.length).toEqual(0);
        });
    });


    describe('block', () => {
        beforeEach(() => {
            loadFixtures('content-select.html');

            testObj.contentSelectEl = document.querySelector('#block');
            testObj.contentSelectModule = new ContentSelect(testObj.contentSelectEl);
        });

        it('should show the relevant block when an item is selected', () => {
            testObj.contentSelectModule.init();

            const selectElement = testObj.contentSelectEl.querySelector('.ds_select');
            const optionToSelect = testObj.contentSelectEl.querySelector('option:nth-child(5)');

            optionToSelect.selected = true;

            const event = new Event('change');
            selectElement.dispatchEvent(event);

            const blockToShow = testObj.contentSelectEl.querySelector('a:nth-of-type(4)');

            expect(blockToShow.classList.contains('fully-hidden')).toBeFalse();
        });
    });

    describe('invalid case: no type set', () => {
        beforeEach(() => {
            loadFixtures('content-select.html');

            testObj.contentSelectEl = document.querySelector('#block');
            testObj.contentSelectEl.dataset.type = '';
            testObj.contentSelectModule = new ContentSelect(testObj.contentSelectEl);
        });

        it('should do nothing on init', () => {
            testObj.contentSelectModule.init();

            expect(testObj.contentSelectModule.type).toBeUndefined();
        });

        it('should do nothing on selecting an item', () => {
            testObj.contentSelectModule.init();

            const selectElement = testObj.contentSelectEl.querySelector('.ds_select');
            const optionToSelect = testObj.contentSelectEl.querySelector('option:nth-child(5)');

            optionToSelect.selected = true;

            const event = new Event('change');
            selectElement.dispatchEvent(event);
        });
    });
});
