/* global jasmine, window, describe, fdescribe, beforeEach, afterEach, loadFixtures, expect, it, fit, document, spyOn */

'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import SmartAnswer from '../../../src/main/scripts/components/smart-answer';
import commonForms from '../../../src/main/scripts/tools/forms';

const testObj = {};
const rootUrl = window.location.pathname;

// mock window
const windowObj = {
    addEventListener: function () { }
};

describe('smart-answer', function () {
    beforeEach(() => {
        loadFixtures('smart-answer.html');
        testObj.smartAnswer = new SmartAnswer(document.querySelector('[data-module="smartanswer"]'), windowObj);
        testObj.smartAnswer.rootUrl = rootUrl;
    });

    afterEach(() => {
        testObj.smartAnswer.interpretUrl = () => { };
        window.location.hash = '';
        window.history.replaceState('', '', rootUrl);
    });

    describe('init (hashbang)', () => {
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

        it('should direct you to the relevant page: 4. unknown url params will abort at the last known step', () => {
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

    describe('init (pushstate)', () => {
        beforeEach(() => {
            testObj.smartAnswer.useHashBangs = false;
        });

        it('should direct you to the relevant page: 1. no answers, go to first page', () => {
            testObj.smartAnswer.init();

            expect(document.querySelector('#step-under-16').classList.contains('mg_smart-answer__step--current')).toBeTruthy();
        });

        it('should direct you to the relevant page: 2. a page in mid-flow', () => {
            window.history.pushState('', '', window.location.pathname + '/over-16/yes');
            testObj.smartAnswer.init();

            expect(document.querySelector('#step-client-receipt-afip').classList.contains('mg_smart-answer__step--current')).toBeTruthy();
        });

        it('should direct you to the relevant page: 3. nonsense url goes to first page', () => {
            window.history.pushState('', '', window.location.pathname + '/a/nonsense/url');
            testObj.smartAnswer.init();

            expect(document.querySelector('#step-under-16').classList.contains('mg_smart-answer__step--current')).toBeTruthy();
        });

        it('should direct you to the relevant page: 4. unknown url params will abort at the last known step', () => {
            window.history.pushState('', '', window.location.pathname + '/over-16/bananas');
            testObj.smartAnswer.init();

            expect(document.querySelector('#step-residency-question-one').classList.contains('mg_smart-answer__step--current')).toBeTruthy();
            expect(window.location.pathname).toEqual(testObj.smartAnswer.rootUrl + '/over-16');
        });

        it('should pre-fill values from the URL params', () => {
            window.history.pushState('', '', window.location.pathname + '/over-16/yes/yes');
            testObj.smartAnswer.init();

            expect(document.querySelector('#step-under-16-over-16').checked).toBeTruthy();
            expect(document.querySelector('#step-residency-question-one-yes').checked).toBeTruthy();
            expect(document.querySelector('#step-client-receipt-afip-yes').checked).toBeTruthy();
        });
    });

    describe('step navigation', () => {
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
            testObj.smartAnswer.goToPageFromUrl('#!/over-16');

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

        it('should clear errors from the step being navigated to, if present', () => {
            testObj.smartAnswer.init();

            const step = document.querySelector('#step-under-16');

            //force error state
            const button = step.querySelector('.js-next-button');
            button.click();

            expect(step.querySelectorAll('.ds_question--error').length).toEqual(1);

            // try to show that step again
            testObj.smartAnswer.showStep(true);
            expect(step.querySelectorAll('.ds_question--error').length).toEqual(0);
        });
    });

    describe('answer navigation', () => {
        it('should go to the selected step if an answer\'s "change" button is clicked', () => {
            window.location.hash = '#!/over-16/yes/yes';
            testObj.smartAnswer.init();

            const button = document.querySelector('[data-questionid="residency-question-one"]');
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

            expect(window.location.hash).toEqual('#!');
            expect(formElement.reset).toHaveBeenCalled();
        });
    });

    describe('input types', () => {
        it('should get the selected answer from a radio button group', () => {
            const radioStep = document.querySelector('.js-test-step__RADIO');
            const selectStep = document.querySelector('.js-test-step__SELECT');
            const checkboxStep = document.querySelector('.js-test-step__CHECKBOX');
            const confirmStep = document.querySelector('.js-test-step__CONFIRM');

            selectStep.parentNode.removeChild(selectStep);
            checkboxStep.parentNode.removeChild(checkboxStep);
            confirmStep.parentNode.removeChild(confirmStep);

            testObj.smartAnswer.init();

            // select option
            let selectedRadio = radioStep.querySelector('#step-under-16-over-16');
            selectedRadio.checked = true;

            // move to step
            let button = document.querySelector('.js-next-button');
            button.click();

            expect(testObj.smartAnswer.getResponsesFromUrl()[0]).toEqual(['over-16']);
        });

        it('should get the selected answers (plural) from a checkbox question', () => {
            const radioStep = document.querySelector('.js-test-step__RADIO');
            const selectStep = document.querySelector('.js-test-step__SELECT');
            const checkboxStep = document.querySelector('.js-test-step__CHECKBOX');
            const confirmStep = document.querySelector('.js-test-step__CONFIRM');

            radioStep.parentNode.removeChild(radioStep);
            selectStep.parentNode.removeChild(selectStep);
            confirmStep.parentNode.removeChild(confirmStep);

            testObj.smartAnswer.init();

            // select options
            const checkboxes = [].slice.call(checkboxStep.querySelectorAll('.ds_checkbox__input'));
            checkboxes[0].checked = true;
            checkboxes[1].checked = true;

            // move to step
            let button = document.querySelector('.js-next-button');
            button.click();

            expect(testObj.smartAnswer.getResponsesFromUrl()[0]).toEqual(['universal-credit', 'pension-credit']);
        });

        it('should get the selected answer from a SELECT element', () => {
            const radioStep = document.querySelector('.js-test-step__RADIO');
            const selectStep = document.querySelector('.js-test-step__SELECT');
            const checkboxStep = document.querySelector('.js-test-step__CHECKBOX');
            const confirmStep = document.querySelector('.js-test-step__CONFIRM');

            radioStep.parentNode.removeChild(radioStep);
            checkboxStep.parentNode.removeChild(checkboxStep);
            confirmStep.parentNode.removeChild(confirmStep);

            testObj.smartAnswer.init();
            spyOn(testObj.smartAnswer, 'goToPageFromUrl');

            // select option
            let selectedOption = document.querySelector('#step-age-select option:nth-child(3)');
            selectedOption.selected = true;

            // move to step
            let button = selectStep.querySelector('.js-next-button');
            button.click();

            expect(testObj.smartAnswer.getResponsesFromUrl()[0]).toEqual(['over-16']);
        });

        it('should get the selected answer from a CONFIRM question', () => {
            const radioStep = document.querySelector('.js-test-step__RADIO');
            const selectStep = document.querySelector('.js-test-step__SELECT');
            const checkboxStep = document.querySelector('.js-test-step__CHECKBOX');
            const confirmStep = document.querySelector('.js-test-step__CONFIRM');

            radioStep.parentNode.removeChild(radioStep);
            selectStep.parentNode.removeChild(selectStep);
            checkboxStep.parentNode.removeChild(checkboxStep);

            testObj.smartAnswer.init();
            spyOn(testObj.smartAnswer, 'goToPageFromUrl');

            let checkbox = confirmStep.querySelector('.ds_checkbox__input');

            // try to move to step
            let button = confirmStep.querySelector('.js-next-button');
            let responsesPreClick = testObj.smartAnswer.getResponsesFromUrl();
            button.click();
            expect(testObj.smartAnswer.getResponsesFromUrl()).toEqual(responsesPreClick);

            // move to step
            checkbox.checked = true;
            button.click();

            expect(testObj.smartAnswer.getResponsesFromUrl()[0]).toEqual(['confirm']);
        });

        it('should get the selected answer from a CONFIRM question without checkbox', () => {
            const radioStep = document.querySelector('.js-test-step__RADIO');
            const selectStep = document.querySelector('.js-test-step__SELECT');
            const checkboxStep = document.querySelector('.js-test-step__CHECKBOX');
            const confirmStep = document.querySelector('.js-test-step__CONFIRM');

            radioStep.parentNode.removeChild(radioStep);
            selectStep.parentNode.removeChild(selectStep);
            checkboxStep.parentNode.removeChild(checkboxStep);

            confirmStep.dataset.type = 'confirm';
            let checkboxContainer = confirmStep.querySelector('.ds_checkbox');
            checkboxContainer.parentNode.removeChild(checkboxContainer);

            testObj.smartAnswer.init();
            spyOn(testObj.smartAnswer, 'goToPageFromUrl');

            // move to step
            let button = confirmStep.querySelector('.js-next-button');
            button.click();

            expect(testObj.smartAnswer.getResponsesFromUrl()[0]).toEqual(['confirm']);
        });

        it('should set a pre-filled answer in a radio button group', () => {
            const radioStep = document.querySelector('.js-test-step__RADIO');
            const selectStep = document.querySelector('.js-test-step__SELECT');
            selectStep.parentNode.removeChild(selectStep);

            window.location.hash = '#!/over-16/';
            testObj.smartAnswer.init();

            expect(radioStep.querySelector('[name="question-under-16"]:checked').value).toEqual('over-16');
        });

        it('should set a pre-filled answer in a SELECT element', () => {
            const radioStep = document.querySelector('.js-test-step__RADIO');
            const selectStep = document.querySelector('.js-test-step__SELECT');
            radioStep.parentNode.removeChild(radioStep);

            window.location.hash = '#!/over-16/';
            testObj.smartAnswer.init();

            const selectElement = selectStep.querySelector("select");
            expect(selectElement[selectElement.selectedIndex].value).toEqual('over-16');
        });
    });

    // error summary interaction
    describe('error summary interaction', () => {
        it('should show and focus on the error summary when a step does not validate', () => {
            spyOn(testObj.smartAnswer.errorSummary, 'focus');

            testObj.smartAnswer.init();

            const button = document.querySelector('#step-under-16 .js-next-button');
            button.click();

            expect(testObj.smartAnswer.errorSummary.classList.contains('fully-hidden')).toBeFalsy();
            expect(document.title.match(/^Error:/).length).toEqual(1);
            expect(testObj.smartAnswer.errorSummary.focus).toHaveBeenCalled();
        });

        it('should focus on an invalid field when its associated error message is clicked in the summary: 1. radio', () => {
            testObj.smartAnswer.init();

            const button = document.querySelector('#step-under-16 .js-next-button');
            button.click();

            const errorLink = testObj.smartAnswer.errorSummary.querySelector('.ds_error-summary__link');
            const expectedFocusField = document.querySelector('#step-under-16 input[type="radio"]');

            spyOn(expectedFocusField, 'focus');

            errorLink.click();
            expect(expectedFocusField.focus).toHaveBeenCalled();
        });

        it('should focus on an invalid field when its associated error message is clicked in the summary: 2. select', () => {
            const radioStep = document.querySelector('.js-test-step__RADIO');
            const selectStep = document.querySelector('.js-test-step__SELECT');
            radioStep.parentNode.removeChild(radioStep);

            testObj.smartAnswer.init();

            const button = document.querySelector('#step-under-16__SELECT .js-next-button');
            button.click();

            const errorLink = testObj.smartAnswer.errorSummary.querySelector('.ds_error-summary__link');
            const expectedFocusField = selectStep.querySelector('select');
            testObj.smartAnswer.errorSummary.click();

            spyOn(expectedFocusField, 'focus');

            errorLink.click();
            expect(expectedFocusField.focus).toHaveBeenCalled();
        });
    });

    // promise request/dynamic step
    describe('dynamic content', () => {
        beforeEach(() => {
            const dynamicElement = document.createElement('div');
            dynamicElement.setAttribute('data-location', 'foo');
            dynamicElement.setAttribute('data-question', 'under-16');
            dynamicElement.classList.add('mg_smart-answer__dynamic-result');
            document.getElementById('step-final-result').appendChild(dynamicElement);
        });

        afterEach(() => {
            const dynamicElement = document.getElementById('step-final-result').querySelector('.mg_smart-answer__dynamic-result');
            dynamicElement.parentNode.removeChild(dynamicElement);
        });

        // disabled -- intermittent fails (race?)
        xit('should load dynamic content if there is a dynamic content element', (done) => {
            window.location.hash = '#!/over-16/yes/no';

            commonForms.promiseRequest = function (url, method = 'GET') {
                return Promise.resolve({ responseText: 'message' });
            };

            testObj.smartAnswer.init();

            window.setTimeout(function () {
                const dynamicElement = testObj.smartAnswer.currentStepElement.querySelector('.mg_smart-answer__dynamic-result');
                expect(dynamicElement.innerText).toEqual('message');
                done();
            }, 0);
        });
    });
});
