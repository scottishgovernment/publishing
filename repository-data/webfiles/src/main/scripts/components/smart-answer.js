/* global document, window, require */

import commonForms from '../tools/forms';

class SmartAnswer {
    constructor(container) {
        this.container = container;
        this.answersTemplate = require('../templates/smartanswer-answers');
        this.errorSummary = document.querySelector('.ds_error-summary');
        this.pageTitle = document.title;
        this.startPage = document.getElementById('start-page-link') ? document.getElementById('start-page-link').getAttribute('href') : '';
    }

    init() {
        this.overrideInPageLinks();

        this.initErrorSummary();

        // set the form state to match the initial URL
        this.interpretUrl(false);

        // listen for hashbang changes
        window.addEventListener('hashchange', () => {
            this.interpretUrl();
        });

        // identify the the question response that would bring the user to a particular step
        // this is used to build URLs for the form state
        const steps = this.container.querySelectorAll('.mg_smart-answer__step');
        steps.forEach(step => {
            if (this.container.querySelector(`[data-nextstep="${step.id}"]`)) {
                step.dataset.previousResponse = this.container.querySelector(`[data-nextstep="${step.id}"]`).id;
            }
        });

        this.container.addEventListener('click', (event) => {
            if (event.target.classList.contains('js-next-button')) {
                event.preventDefault();

                const stepContainer = event.target.closest('.mg_smart-answer__step');

                if (this.validateStep(stepContainer)) {
                    const checkedOption = stepContainer.querySelector("input[type='radio']:checked");
                    const next = this.container.querySelector('#' + checkedOption.dataset.nextstep);

                    this.updateUrl(next);
                } else {
                    this.showErrorSummary();
                }
            }

            if (event.target.classList.contains('js-change-answer')) {
                event.preventDefault();
                window.location.hash = event.target.dataset.path;
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
                }
            }
        }

        // show the target step
        this.showStep(step, focus);
        this.hideErrorSummary();

        // populate the answer list
        const answerListHtml = this.answersTemplate.render({
            startPageLink: this.startPage,
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

    /*
     * Build the hashbang URL from the answered questions
     * Use the URL to trigger a hashchange event & perform navigation
     */
    updateUrl(currentStep) {
        const urlBits = [];

        while (currentStep && this.container.querySelector('#' + currentStep.dataset.previousResponse)) {
            urlBits.push(this.container.querySelector('#' + currentStep.dataset.previousResponse).value);

            const prevStep = this.container.querySelector(`[data-nextstep="${currentStep.id}"]`);

            if (prevStep) {
                currentStep = prevStep.closest('.mg_smart-answer__question');
            } else {
                currentStep = null;
            }
        }

        window.location.hash = `#!/${urlBits.reverse().join('/')}`;
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
