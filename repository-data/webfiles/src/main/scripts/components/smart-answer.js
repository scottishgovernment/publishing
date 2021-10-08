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
        this.pageTitle = document.title;
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

                if (this.validateStep(stepContainer)) {
                    let selectedOption = stepContainer.querySelector("input[type='radio']:checked");

                    if (selectedOption != null) {
                        responses.push(selectedOption.value);
                    } else {
                        selectedOption = stepContainer.querySelector("select");
                        responses.push(selectedOption[selectedOption.selectedIndex].value);
                    }

                    history.pushState('', '', this.buildUrl(responses));
                    this.goToPageFromUrl();
                } else {
                    this.showErrorSummary();
                }
            }

            if (event.target.classList.contains('js-change-answer')) {
                event.preventDefault();

                history.pushState('', '', event.target.dataset.path);
                this.goToPageFromUrl();
            }

            if (event.target.classList.contains('js-clear-answers')) {
                event.preventDefault();
                this.form.reset();

                history.pushState('', '', this.rootUrl);
                this.goToPageFromUrl();
            }
        });

        this.window.addEventListener('popstate', () => {
            this.goToPageFromUrl();
        });
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

            dynamicContentElements.forEach(element => {
                const location = element.getAttribute('data-location');
                const question = element.getAttribute('data-question');
                const answerForQuestion = this.currentAnswers.find(answer => answer.id === question);

                if (answerForQuestion) {
                    const promiseRequest = commonForms.promiseRequest(`${location}?tag=${answerForQuestion.value}`);

                    promiseRequest
                        .then(value => {
                            element.innerHTML = value.responseText;
                            window.DS.tracking.init(element);
                        })
                        .catch(error => {
                            console.log('failed to fetch dynamic content ', error);
                        });

                    dynamicContentPromises.push(promiseRequest);
                }
            });

            PolyPromise.all(dynamicContentPromises)
                .then(() => this.showStep(focus));
        } else {
            this.showStep(focus);
        }
    }

    showStep(focus) {
        // show step, deal with error summary etc, set up answer list
        this.hideErrorSummary();

        const oldStep = this.container.querySelector('.mg_smart-answer__step--current');
        const step = this.currentStepElement;

        if (oldStep) {
            oldStep.classList.remove('mg_smart-answer__step--current');
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
            temporaryFocus(step);
        }

        this.stepTitle = step.querySelector('.js-question-title').innerText;

        // populate the answer list
        const answerListHtml = this.answersTemplate.render({
            answers: this.currentAnswers
        });

        const answerContainer = document.getElementById('answered-questions');
        answerContainer.innerHTML = answerListHtml;
        window.DS.tracking.init(answerContainer);
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

        for (let i = 0, il = responses.length; i < il; i++) {
            const chosenAnswer = this.currentStepElement.querySelector(`[value="${responses[i]}"]`);
            if (chosenAnswer) {
                let answerData = {
                    id: this.currentStepElement.id.replace('step-',''),
                    title: this.currentStepElement.querySelector('.mg_smart-answer__step-title').innerText,
                    path: answerpath
                };

                if (chosenAnswer.nodeName === 'OPTION') {
                    answerData.text = chosenAnswer.innerText;
                    answerData.value = chosenAnswer.value;
                    chosenAnswer.selected = true;
                } else {
                    answerData.text = this.currentStepElement.querySelector(`[for="${chosenAnswer.id}"]`).innerText;
                    answerData.value = this.currentStepElement.querySelector('#' + chosenAnswer.id).value;
                    chosenAnswer.checked = true;
                }

                this.currentAnswers.push(answerData);

                // move to next step
                this.currentStepElement = this.container.querySelector('#' + chosenAnswer.dataset.nextstep);
                answerpath += '/' + responses[i];
            } else {
                // break;
            }
        }

        // clean the URL (removes answers found in the URL that are not in the page)
        window.history.replaceState({}, '', answerpath);
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
                validationChecks.push(commonForms[validations[i]]);
            }
            commonForms.validateInput(item, validationChecks);
        });

        const invalidFields = [].slice.call(stepContainer.querySelectorAll('[aria-invalid="true"]'));

        return invalidFields.length === 0;
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

        return responses;
    }
}

export default SmartAnswer;
