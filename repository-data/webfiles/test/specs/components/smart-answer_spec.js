/* global jasmine, window, describe, fdescribe, beforeEach, afterEach, loadFixtures, expect, it, fit, document, spyOn */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import SmartAnswer from '../../../src/main/scripts/components/smart-answer';

const testObj = {};

describe('smart-answer', function () {
    beforeEach(() => {
        loadFixtures('smart-answer.html');
        testObj.smartAnswer = new SmartAnswer(document.querySelector('[data-module="smartanswer"]'));
    });

    afterEach(() => {
        testObj.smartAnswer.interpretUrl = () => { };
        window.location.hash = '';
    });

    describe('init', () => {
        it('should direct you to the relevant page: 1. no hash, go to first page', () => {
            testObj.smartAnswer.init();

            expect(document.querySelector('#step-under-16').classList.contains('mg_smart-answer__step--current')).toBeTruthy();
        });

        it('should direct you to the relevant page: 2. a page in mid-flow', () => {
            window.location.hash = '#!/over-16/yes';
            testObj.smartAnswer.init();

            expect(document.querySelector('#step-client-receipt-afip').classList.contains('mg_smart-answer__step--current')).toBeTruthy();
        });

        it('should direct you to the relevant page: 3. nonsense url goes to first page', () => {
            window.location.hash = '#!/a/nonsense/url';
            testObj.smartAnswer.init();

            expect(document.querySelector('#step-under-16').classList.contains('mg_smart-answer__step--current')).toBeTruthy();
        });

        it('should direct you to the relevant page: 3. unknown url params will abort at the last known step', () => {
            window.location.hash = '#!/over-16/bananas';
            testObj.smartAnswer.init();

            expect(document.querySelector('#step-residency-question-one').classList.contains('mg_smart-answer__step--current')).toBeTruthy();
            expect(window.location.hash).toEqual('#!/over-16');
        });

        it('should pre-fill values from the URL params', () => {
            window.location.hash = '#!/over-16/yes/yes';
            testObj.smartAnswer.init();

            expect(document.querySelector('#step-under-16-over-16').checked).toBeTruthy();
            expect(document.querySelector('#step-residency-question-one-yes').checked).toBeTruthy();
            expect(document.querySelector('#step-client-receipt-afip-yes').checked).toBeTruthy();
        });
    });

    describe('step navigation', () => {
        it('should show the error summary if the step is not valid', () => {
            spyOn(testObj.smartAnswer.errorSummary, 'focus');

            testObj.smartAnswer.init();

            const button = document.querySelector('#step-under-16 .js-next-button');
            button.click();

            expect(testObj.smartAnswer.errorSummary.classList.contains('fully-hidden')).toBeFalsy();
            expect(document.title.match(/^Error:/).length).toEqual(1);
            expect(testObj.smartAnswer.errorSummary.focus).toHaveBeenCalled();
        });

        it('should move to the next step if the current step is valid', () => {
            testObj.smartAnswer.init();

            let selectedRadio = document.querySelector('#step-under-16-over-16');
            selectedRadio.checked = true;

            let button = document.querySelector('#step-under-16 .js-next-button');
            button.click();

            expect(window.location.hash).toEqual('#!/over-16');
            // have to force this -- jasmine not liking hashchange
            testObj.smartAnswer.interpretUrl();

            expect(document.querySelectorAll('.mg_smart-answer__step--current').length).toEqual(1);
            expect(document.querySelector('.mg_smart-answer__step--current').id).toEqual(selectedRadio.dataset.nextstep);

            // and we'll do that again just to make sure the URL is updating
            selectedRadio = document.querySelector('#step-residency-question-one-yes');
            selectedRadio.checked = true;

            button = document.querySelector('#step-residency-question-one .js-next-button');
            button.click();

            expect(window.location.hash).toEqual('#!/over-16/yes');
        });

        it('should trigger a virtual pageview when moving to a new step', () => {
            testObj.smartAnswer.init();

            let selectedRadio = document.querySelector('#step-under-16-over-16');
            selectedRadio.checked = true;

            let button = document.querySelector('#step-under-16 .js-next-button');
            button.click();

            // mock window.ga
            window.ga = function (command, ...fields) {};

            expect(window.location.hash).toEqual('#!/over-16');
            // have to force this -- jasmine not liking hashchange
            spyOn(window, 'ga');
            testObj.smartAnswer.interpretUrl();

            expect(window.ga).toHaveBeenCalledWith('set', 'page', `${window.location.pathname}${window.location.hash}`);
            expect(window.ga).toHaveBeenCalledWith('send', 'pageview');
        });

        it('should NOT track virtual pageview if user comes in from a deep link', () => {
            // mock window.ga
            window.ga = function (command, ...fields) {};
            spyOn(window, 'ga');

            window.location.hash = '#!/over-16/yes';
            testObj.smartAnswer.init();

            expect(window.ga).not.toHaveBeenCalled();
        });

        it('should clear errors from the step beign navigated to, if present', () => {
            testObj.smartAnswer.init();

            const step = document.querySelector('#step-under-16');

            //force error state
            const button = step.querySelector('.js-next-button');
            button.click();

            expect(step.querySelectorAll('.ds_question--error').length).toEqual(1);

            // try to show that step again
            testObj.smartAnswer.showStep(step);
            expect(step.querySelectorAll('.ds_question--error').length).toEqual(0);
        });
    });

    describe('answer navigation', () => {
        it('should go to the selected step if an answer\'s "change" button is clicked', () => {
            window.location.hash = '#!/over-16/yes/yes';
            testObj.smartAnswer.init();

            const button = document.querySelector('[data-questionid="step-residency-question-one"]');
            button.click();

            expect(window.location.hash).toEqual('#!/over-16');
        });

        it('should clear the form and return to the first question when the "clear form" button is clicked', () => {
            window.location.hash = '#!/over-16/yes/yes';
            testObj.smartAnswer.init();
            const formElement = document.querySelector('.mg_smart-answer__form');
            spyOn(formElement, 'reset');

            const button = document.querySelector('.js-clear-answers');
            button.click();

            expect(window.location.hash).toEqual('');
            expect(formElement.reset).toHaveBeenCalled();
        });
    });
});
