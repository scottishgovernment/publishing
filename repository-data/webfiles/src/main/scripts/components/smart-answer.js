/* global document, window, require */

import commonForms from '../tools/forms';
import $ from 'jquery';

class SmartAnswer {
    constructor(container) {
        this.container = container;
        this.form = this.container.querySelector('form');
        this.answersTemplate = require('../templates/smartanswer-answers');
        this.errorSummary = document.querySelector('.ds_error-summary');
        this.pageTitle = document.title;
    }

    init() {
        this.overrideInPageLinks();

        this.initErrorSummary();

        // set the form state to match the initial URL
        this.interpretUrl(false);
        this.trackVirtualPageviews = true;

        // listen for hashbang changes
        window.addEventListener('hashchange', () => {
            this.interpretUrl();
        });

        this.container.addEventListener('click', (event) => {
            if (event.target.classList.contains('js-next-button')) {
                event.preventDefault();


            /// the user has presses next  ... either get the value from a list or a radio button
                const stepContainer = event.target.closest('.mg_smart-answer__step');

                if (this.validateStep(stepContainer)) {
                    var selectedOption = stepContainer.querySelector("input[type='radio']:checked");

                    if (selectedOption != null) {
                        this.updateUrl(selectedOption.value);
                    } else {
                        selectedOption = stepContainer.querySelector("select");
                        this.updateUrl(selectedOption[selectedOption.selectedIndex].value);
                    }
                } else {
                    this.showErrorSummary();
                }
            }

            if (event.target.classList.contains('js-change-answer')) {
                event.preventDefault();
                window.location.hash = event.target.dataset.path;
            }

            if (event.target.classList.contains('js-clear-answers')) {
                event.preventDefault();
                this.form.reset();
                window.location.hash = '';
            }
        });
    }

    initErrorSummary() {
        this.errorSummary.addEventListener('click', (event) => {
            if (event.target.classList.contains('ds_error-summary__link')) {
                const targetElement = document.querySelector(event.target.href.substring(event.target.href.indexOf('#')));

                switch (targetElement.nodeName) {
                    case 'FIELDSET':
                        const firstField = targetElement.querySelector('input, textarea, select');
                        firstField.focus();
                        break;
                    case 'INPUT':
                    case 'TEXTAREA':
                    case 'SELECT':
                        targetElement.focus();
                        break;
                }
            }
        });
    }

    /*
     * Need an alternative to using a fragment identifier, since that messes with the hashbang URLs
     */
    overrideInPageLinks() {
        document.addEventListener('click', (event) => {
            if (event.target.nodeName === 'A' && event.target.href.indexOf('#') > -1 && event.target.href.indexOf('#!') === -1) {
                event.preventDefault();

                const targetElement = document.querySelector(event.target.href.substring(event.target.href.indexOf('#')));
                if (targetElement) {
                    targetElement.scrollIntoView();
                }
            }
        });
    }

    /*
     * Examine the hashbang for responses to questions in the tree, and replay those answers to set the form state
     */
    interpretUrl(focus = true) {
        const answers = [];

        let step = this.container.querySelector('.mg_smart-answer__step');

        if (window.location.hash.indexOf('#!/') > -1) {
            const responses = window.location.hash.substring(window.location.hash.indexOf('#!/') + 3).split('/');

            let answerpath = '#!';

            // replay the responses from the URL through the form
            for (let i = 0, il = responses.length; i < il; i++) {
                const chosenAnswer = step.querySelector(`[value="${responses[i]}"]`);
                if (chosenAnswer) {
                    answers.push(
                        {
                            id: step.id,
                            title: step.querySelector('.mg_smart-answer__step-title').innerText,
                            value: step.querySelector(`[for="${chosenAnswer.id}"]`).innerText,
                            path: answerpath
                        }
                    );

                    chosenAnswer.checked = true;

                    // move to next step
                    step = this.container.querySelector('#' + chosenAnswer.dataset.nextstep);
                    answerpath += '/' + responses[i];
                } else {
                    // break;
                }
            }
            // clean the URL (removes answers found in the URL that are not in the page)
            window.history.replaceState({}, document.title, answerpath);
        }

        // trigger virtual pageview
        if (this.trackVirtualPageviews && window.ga && typeof window.ga === 'function') {
            ga('set', 'page', `${window.location.pathname}${window.location.hash}`);
            ga('send', 'pageview');
        }

        // show the target step
        this.showStep(step, focus);
        this.hideErrorSummary();

        // populate the answer list
        const answerListHtml = this.answersTemplate.render({
            answers: answers
        });
        document.getElementById('answered-questions').innerHTML = answerListHtml;
    }

    showErrorSummary() {
        this.errorSummary.classList.remove('fully-hidden');
        document.title = `Error: ${this.stepTitle} - Mygov`;
        this.errorSummary.focus();
    }

    hideErrorSummary() {
        this.errorSummary.classList.add('fully-hidden');
        document.title = `${this.pageTitle}`;
    }

    /*
     * Show a specified step, set focus on it by default for accessibility (focus management)
     */
    showStep (step, focus = true) {

        const currentStep = this.container.querySelector('.mg_smart-answer__step--current');

        this.loadDynamicResults(step);

        if (currentStep) {
            currentStep.classList.remove('mg_smart-answer__step--current');
        }
        // reset errors
        const errorQuestions = [].slice.call(step.querySelectorAll('.ds_question--error'));
        errorQuestions.forEach(question => {
            const errorMessage = question.querySelector('.ds_question__error-message');
            errorMessage.parentNode.removeChild(errorMessage);

            const errorInputs = [].slice.call(question.querySelectorAll('.ds_input--error'));
            errorInputs.forEach(input => {
                input.classList.remove('ds_input--error');
                input.removeAttribute('aria-invalid');
            });

            question.classList.remove('ds_question--error');
        });

        step.classList.add('mg_smart-answer__step--current');

        if (focus) {
            step.focus();
        }

        this.stepTitle = step.querySelector('.js-question-title').innerText;
    }

    loadDynamicResults(step) {
        const dynamicResults = step.querySelectorAll('.mg_smart-answer__dynamic-result');

        if (dynamicResults !== null) {
            dynamicResults.forEach(dynamicResult => this.loadDynamicResult(step, dynamicResult));
        }
    }

    loadDynamicResult(step, dynamicResult) {
        const dynamicFolder = dynamicResult.getAttribute('data-dynamic-result-folder');
        const dynamicQuestion = dynamicResult.getAttribute('data-dynamic-result-question');
        const answerStep = this.container.querySelector(`#step-${dynamicQuestion}.mg_smart-answer__step`);
        const responses = window.location.hash.substring(window.location.hash.indexOf('#!/') + 3).split('/');
        const stepIndex = Array.prototype.indexOf.call(answerStep.parentNode.children, answerStep);
        const tag = responses[stepIndex];

        $.ajax(`${dynamicFolder}?tag=${tag}`)
            .done(data => dynamicResult.innerHTML = data)
            .fail(response => console.log('failed to fetch dynamic content ', response));
    }

    /*
     * Build the hashbang URL from the answered questions
     * Use the URL to trigger a hashchange event & perform navigation
     */
    updateUrl(value) {
        let values;
        if (window.location.hash.indexOf('/') > -1) {
            values = window.location.hash.substring(window.location.hash.indexOf('/') + 1).split('/');
        } else {
            values = [];
        }
        values.push(value);
        window.location.hash = `!/${values.join('/')}`;
    }

    validateStep () {
        /*
         * look for data-validation attributes in current step & PERFORM VALIDATION
         * do not allow progress if invalid
         */

        const stepContainer = document.querySelector('.mg_smart-answer__step--current');
        const itemsThatNeedToBeValidated = [].slice.call(stepContainer.querySelectorAll('[data-validation]')).filter(item => item.offsetParent);

        itemsThatNeedToBeValidated.forEach(item => {
            const validations = item.getAttribute('data-validation').split(' ');
            const validationChecks = [];
            for (let i = 0, il = validations.length; i < il; i++) {
                if (commonForms[validations[i]]) {
                    validationChecks.push(commonForms[validations[i]]);
                }
            }
            commonForms.validateInput(item, validationChecks);
        });

        const invalidFields = [].slice.call(stepContainer.querySelectorAll('[aria-invalid="true"]'));

        return invalidFields.length === 0;
    }
}

export default SmartAnswer;
