/* global document, window, require */

import commonForms from '../tools/forms';
import temporaryFocus from '../../../../node_modules/@scottish-government/pattern-library/src/base/tools/temporary-focus/temporary-focus';
const PolyPromise = require('../vendor/promise-polyfill').default;

class SmartAnswer {
    constructor(container, _window = window) {
        this.container = container;
        this.rootUrl = container.dataset.rooturl ;
        this.form = this.container.querySelector('form');
        this.answersTemplate = require('../templates/smartanswer-answers');
        this.errorSummary = document.querySelector('.ds_error-summary');
        this.useHashBangs = true;
        this.window = _window;
    }

    init() {
        this.initErrorSummary();

        this.goToPageFromUrl(false);

        // only start tracking virtul page views after the initial "goToPage"
        this.trackVirtualPageviews = true;

        this.container.addEventListener('click', (event) => {

            if (event.target.classList.contains('js-next-button')) {
                event.preventDefault();

                /// the user has pressed next ... either get the value from a list or a radio button
                const stepContainer = event.target.closest('.mg_smart-answer__step');
                const responses = this.getResponsesFromUrl();

                let selectedOption;
                let newResponse;

                if (this.validateStep(stepContainer)) {
                    switch (stepContainer.dataset.type) {
                        case 'radiobuttons':
                            selectedOption = stepContainer.querySelector("input[type='radio']:checked");
                            newResponse = selectedOption.value;
                            break;
                        case 'checkboxes':
                            let selectedOptions = [].slice.call(stepContainer.querySelectorAll("input[type='checkbox']:checked"));
                            newResponse = selectedOptions.map(item => item.value).join(':');
                            break;
                        case 'dropdown':
                            selectedOption = stepContainer.querySelector("select");
                            newResponse = selectedOption[selectedOption.selectedIndex].value;
                            break;
                        case 'confirm':
                        case 'confirmcheckbox':
                            newResponse = 'confirm';
                            break;
                    }

                    responses.push(newResponse);

                    history.pushState('', '', this.buildUrl(responses));
                    this.goToPageFromUrl();
                } else {
                    this.showErrorSummary();
                }
            }

            if (event.target.classList.contains('js-change-answer')) {
                event.preventDefault();
                document.documentElement.classList.add('back-transition');

                history.pushState('', '', event.target.dataset.path);
                this.goToPageFromUrl();
            }

            if (event.target.classList.contains('js-clear-answers')) {
                event.preventDefault();
                document.documentElement.classList.add('back-transition');
                this.form.reset();

                history.pushState('', '', this.rootUrl);
                this.goToPageFromUrl();
            }
        });

        this.window.addEventListener('popstate', () => {
            this.goToPageFromUrl();
        });
    }

    buildUrl(answers) {
        let url;

        if (this.useHashBangs) {
            url = window.location.pathname + '#!/' + answers.join('/');
        } else {
            url = this.rootUrl + '/' + answers.join('/');
        }

        return url;
    }

    getResponsesFromUrl() {
        let responses = [];
        if (this.useHashBangs) {
            if (window.location.hash.indexOf('#!/') > -1) {
                responses = window.location.hash.substring(window.location.hash.indexOf('#!/') + 3).split('/');
            }
        } else {
            responses = window.location.pathname.replace(this.rootUrl, '').substring(1).split('/');
        }

        return responses.filter(response => response != ['']);
    }

    getStepFromPotentialNextStep(potentialNextStep) {
        let nextStep = potentialNextStep;

        if (potentialNextStep.dataset.ineligible) {
            // do we have other branches to explore?
            let otherBranchStepElement;

            for (let item of this.processedResponses.slice().reverse()) {
                const remainingOptions = Object.entries(item.responses).filter(item2 => item2[1] === false);
                if (remainingOptions.length) {
                    otherBranchStepElement = this.container.querySelector('#' + document.getElementById(item.stepId).querySelector(`[value="${remainingOptions[0][0]}"]`).dataset.nextstep);
                    this.markResponseAsProcessed(item.stepId, remainingOptions[0][0]);
                    break;
                }
            }

            if (!!otherBranchStepElement) {
                nextStep = this.getStepFromPotentialNextStep(otherBranchStepElement);
            }
        }

        return nextStep;
    }

    goToPageFromUrl(focus = true) {
        this.interpretUrl();

        const dynamicContentElements = [].slice.call(this.currentStepElement.querySelectorAll('.mg_smart-answer__dynamic-result'));

        // trigger virtual pageview
        if (this.trackVirtualPageviews && window.ga && typeof window.ga === 'function') {
            ga('set', 'page', `${window.location.pathname}${window.location.hash}`);
            ga('send', 'pageview');
        }

        this.hideErrorSummary();

        // this check isn't really necessary but it simplifies unit tests
        if (dynamicContentElements.length) {
            const dynamicContentPromises = [];
            const responses = [];

            dynamicContentElements.forEach(element => {
                const location = element.getAttribute('data-location');
                const question = element.getAttribute('data-question');

                const answerForQuestion = this.currentAnswers.find(answer => answer.id === question);

                if (answerForQuestion) {
                    const responsesForQuestion = this.processedResponses.find(item => item.stepId === 'step-' + answerForQuestion.id).responses;

                    let tag = '';
                    Object.entries(responsesForQuestion).forEach(item => {
                        if (item[1]) {
                            tag = item[0];
                        }
                    });

                    const promiseRequest = commonForms.promiseRequest(`${location}?tag=${tag}`);

                    promiseRequest
                    .then(value => {
                        if (!responses.includes(value.responseText)) {
                            element.innerHTML = value.responseText;
                            window.DS.tracking.init(element);
                            responses.push(value.responseText);
                        }
                    })
                    .catch(error => {
                        console.log('failed to fetch dynamic content ', error);
                    });

                    dynamicContentPromises.push(promiseRequest);
                } else {
                    // clear any current content
                    element.innerHTML = '';
                }
            });

            PolyPromise.all(dynamicContentPromises)
                .then(() => this.showStep(focus));
        } else {
            this.showStep(focus);
        }
    }

    hideErrorSummary() {
        this.errorSummary.classList.add('fully-hidden');
        document.title = `${this.stepTitle}`;
    }

    initErrorSummary() {
        this.errorSummary.addEventListener('click', (event) => {
            if (event.target.classList.contains('ds_error-summary__link')) {
                const targetElement = document.querySelector(event.target.href.substring(event.target.href.indexOf('#')));

                if (targetElement.nodeName === 'FIELDSET') {
                    const firstField = targetElement.querySelector('input, textarea, select');
                    firstField.focus();
                } else {
                    targetElement.focus();
                }
            }
        });
    }

    /*
     * Examine the URL for responses to questions in the tree, and replay those answers to set the form state
     */
    interpretUrl() {
        this.currentAnswers = [];

        this.currentStepElement = this.container.querySelector('.mg_smart-answer__step');

        let answerpath;
        let responses = this.getResponsesFromUrl();
        if (this.useHashBangs) {
            answerpath = '#!';
        } else {
            answerpath = this.rootUrl;
        }
        responses = responses.map(currentResponse => currentResponse.split(':'));

        this.processedResponses = [];

        responses.forEach(items => {
            this.processedResponses.push({
                stepId: this.currentStepElement.id,
                responses: items.reduce((accumulated, value) => (accumulated[value] = false, accumulated), {})
            });

            if (items.length === 0) { return; }

            let chosenAnswer;

            // mark the chosen answer(s)
            let firstChosenAnswer = this.currentStepElement.querySelector(`[value="${items[0]}"]`);

            if (!!firstChosenAnswer) {
                let answerData = {
                    id: this.currentStepElement.id.replace('step-',''),
                    title: this.currentStepElement.querySelector('.mg_smart-answer__step-title').innerText.trim(),
                    path: answerpath,
                    type: this.currentStepElement.dataset.type,
                    responses: []
                };

                items.forEach(item => {
                    chosenAnswer = this.currentStepElement.querySelector(`[value="${item}"]`);

                    if (chosenAnswer.nodeName === 'OPTION') {
                        chosenAnswer.selected = true;
                    } else if (chosenAnswer.nodeName === 'INPUT' && (chosenAnswer.type === 'radio' || chosenAnswer.type === 'checkbox')) {
                        chosenAnswer.checked = true;
                    }

                    if (chosenAnswer.nodeName === 'OPTION') {
                        answerData.responses.push({
                            text: chosenAnswer.innerText,
                            value: chosenAnswer.value
                        });
                    } else if (chosenAnswer.nodeName === 'INPUT' && (chosenAnswer.type === 'radio' || chosenAnswer.type === 'checkbox')) {
                        answerData.responses.push({
                            text: this.currentStepElement.querySelector(`[for="${chosenAnswer.id}"]`).innerHTML,
                            value: this.currentStepElement.querySelector('#' + chosenAnswer.id).value
                        });
                    }
                });

                this.currentAnswers.push(answerData);
                answerpath += '/' + items.join(':');

                // mark this answer as processed
                this.markResponseAsProcessed(this.currentStepElement.id, items[0]);

                // go to the next step
                const potentialNextStep = this.container.querySelector('#' + firstChosenAnswer.dataset.nextstep);

                this.currentStepElement = this.getStepFromPotentialNextStep(potentialNextStep);
            }
        });

        window.history.replaceState({}, '', answerpath);
    }

    markResponseAsProcessed(stepId, response) {
        this.processedResponses.forEach(item => {
            if (item.stepId === stepId) {
                item.responses[response] = true;
            }
        });
    }

    showErrorSummary() {
        this.errorSummary.classList.remove('fully-hidden');
        document.title = `Error: ${this.stepTitle} - Mygov`;
        this.errorSummary.focus();
    }

    showStep(focus) {
        // show step, deal with error summary etc, set up answer list
        this.hideErrorSummary();

        const oldStep = this.container.querySelector('.mg_smart-answer__step--current');
        const step = this.currentStepElement;

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

        this.stepTitle = step.querySelector('.js-question-title').innerText;
        document.title = `${this.stepTitle}`;

        // populate the answer list
        const answerListHtml = this.answersTemplate.render({
            answers: this.currentAnswers
        });

        const answerContainer = document.getElementById('answered-questions');
        answerContainer.innerHTML = answerListHtml;
        window.DS.tracking.init(answerContainer);

        this.doPageTransition(oldStep, step, focus);
    }

    validateStep() {
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
               validationChecks.push(commonForms[validations[i]]);
            }
            commonForms.validateInput(item, validationChecks);
        });

        const invalidFields = [].slice.call(stepContainer.querySelectorAll('[aria-invalid="true"]'));

        return invalidFields.length === 0;
    }

    doPageTransition(oldStep, newStep, focus = true) {
        function updateDOM() {
            if (oldStep) { oldStep.classList.remove('mg_smart-answer__step--current'); }
            newStep.classList.add('mg_smart-answer__step--current');

            if (focus) {
                temporaryFocus(newStep);
            }
        }

        // Fallback for browsers that don't support this API:
        if (!document.createDocumentTransition) {
            updateDOM();
            return;
        }

        // With a transition:
        const transition = document.createDocumentTransition();
        transition.start(() => {
                updateDOM();
            })
            .then(() => {
                document.documentElement.classList.remove('back-transition');
            });
    }
}

export default SmartAnswer;
