/* global document, window, require */

import commonForms from '../tools/forms';
const PolyPromise = require('../vendor/promise-polyfill').default;

class SmartAnswer {
    constructor(container) {
        this.container = container;
        this.rooturl = container.dataset.rooturl;
        this.form = this.container.querySelector('form');
        this.answersTemplate = require('../templates/smartanswer-answers');
        this.errorSummary = document.querySelector('.ds_error-summary');
        this.pageTitle = document.title;
        this.useHashBangs = true;
    }

    init() {
        this.initErrorSummary();

        this.trackVirtualPageviews = true;
        const responses = this.getResponsesFromUrl();
        this.goToPage(this.buildUrl(responses), false);

        this.container.addEventListener('click', (event) => {
            let url;

            if (event.target.classList.contains('js-next-button')) {
                event.preventDefault();

                /// the user has pressed next  ... either get the value from a list or a radio button
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

                    this.goToPage(this.buildUrl(responses));
                } else {
                    this.showErrorSummary();
                }
            }

            if (event.target.classList.contains('js-change-answer')) {
                event.preventDefault();

                url = event.target.dataset.path;
                this.goToPage(url);
            }

            if (event.target.classList.contains('js-clear-answers')) {
                event.preventDefault();
                this.form.reset();

                url = this.rooturl;
                this.goToPage(url);
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

    goToPage(url, focus = true) {
        history.pushState('', '', url);

        this.interpretUrl();

        const dynamicContentElements = [].slice.call(this.currentStepElement.querySelectorAll('.mg_smart-answer__dynamic-result'));
        const responses = this.getResponsesFromUrl();

        // trigger virtual pageview
        if (this.trackVirtualPageviews && window.ga && typeof window.ga === 'function') {
            ga('set', 'page', `${window.location.pathname}${window.location.hash}`);
            ga('send', 'pageview');
        }

        this.hideErrorSummary();

        const dynamicContentPromises = dynamicContentElements.map(element => {
            const dynamicFolder = element.getAttribute('data-dynamic-result-folder');
            const dynamicQuestion = element.getAttribute('data-dynamic-result-question');
            const answerStep = this.container.querySelector(`#step-${dynamicQuestion}.mg_smart-answer__step`);
            const stepIndex = Array.prototype.indexOf.call(answerStep.parentNode.children, answerStep);
            const tag = responses[stepIndex];
            return this.promiseRequest(`${dynamicFolder}?tag=${tag}`);
        });

        PolyPromise.all(dynamicContentPromises)
            .then(values => values.forEach((value, i) => dynamicContentElements[i].innerHTML = value.responseText))
            .then(() => this.showStep(focus))
            .catch(error => console.log('failed to fetch dynamic content ', error));
    }

    showStep(focus = true) {
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
            step.focus();
        }

        this.stepTitle = step.querySelector('.js-question-title').innerText;

        // populate the answer list
        const answerListHtml = this.answersTemplate.render({
            answers: this.currentAnswers
        });

        document.getElementById('answered-questions').innerHTML = answerListHtml;
    }

    /*
     * Examine the URL for responses to questions in the tree, and replay those answers to set the form state
     */
    interpretUrl(focus = true) {
        this.currentAnswers = [];

        this.currentStepElement = this.container.querySelector('.mg_smart-answer__step');

        let answerpath;
        let responses = this.getResponsesFromUrl();

        if (this.useHashBangs) {
            answerpath = '#!';
        } else {
            answerpath = this.rooturl;
        }

        for (let i = 0, il = responses.length; i < il; i++) {
            const chosenAnswer = this.currentStepElement.querySelector(`[value="${responses[i]}"]`);
            if (chosenAnswer) {
                let answerValue;

                switch (chosenAnswer.nodeName) {
                    case 'OPTION':
                        answerValue = chosenAnswer.innerText;
                        break;
                    default:
                        answerValue = this.currentStepElement.querySelector(`[for="${chosenAnswer.id}"]`).innerText;
                        break;
                }

                this.currentAnswers.push({
                    id: this.currentStepElement.id,
                    title: this.currentStepElement.querySelector('.mg_smart-answer__step-title').innerText,
                    value: answerValue,
                    path: answerpath
                });

                chosenAnswer.checked = true;

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
                if (commonForms[validations[i]]) {
                    validationChecks.push(commonForms[validations[i]]);
                }
            }
            commonForms.validateInput(item, validationChecks);
        });

        const invalidFields = [].slice.call(stepContainer.querySelectorAll('[aria-invalid="true"]'));

        return invalidFields.length === 0;
    }

    buildUrl(answers) {
        let url = window.location.pathname;

        if (this.useHashBangs) {
            url += '#!/' + answers.join('/');
        } else {
            url += '/' + answers.join('/');
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
            responses = window.location.pathname.replace(this.rooturl, '').substring(1).split('/');
        }

        return responses;
    }

    promiseRequest(url, method = 'GET') {
        const request = new XMLHttpRequest();

        return new PolyPromise((resolve, reject) => {
            request.onreadystatechange = () => {
                if (request.readyState !== 4) {
                    return;
                }

                if (request.status >= 200 && request.status < 300) {
                    resolve(request);
                } else {
                    reject({
                        status: request.status,
                        statusText: request.statusText
                    });
                }
            };

            request.open(method, url, true);
            request.send();
        });
    }
}

export default SmartAnswer;
