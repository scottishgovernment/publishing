//feedback.js
/*
 Contains functionality for on page feedback
 */


'use strict';

const feedbackForm = {
    init: function () {
        this.formElement = document.getElementById('feedbackForm');
        this.errorSummary = document.getElementById('feedbackErrorSummary');

        this.fields = {
            yes: {
                comments: document.querySelector('#comments-yes')
            },
            no: {
                reason: document.querySelector('#reason-no'),
                comments: document.querySelector('#comments-no')
            },
            yesbut: {
                reason: document.querySelector('#reason-yesbut'),
                comments: document.querySelector('#comments-yesbut')
            }
        };

        if (this.formElement) {
            this.attachEventHandlers();
        }
    },

    attachEventHandlers: function () {
        this.formElement.addEventListener('submit', (event) => {
            event.preventDefault();

            if (document.querySelector('[name=feedbacktype]:checked')) {
                this.submitFeedback();
            }
        });
    },

    submitFeedback: function () {
        if (!document.querySelector('[name=feedbacktype]:checked')) {
            return;
        }

        // remove any error messages
        this.removeErrorMessages();

        const feedback = {
            slug: document.location.pathname,
            type: document.querySelector('[name=feedbacktype]:checked').value,
            reason: '',
            freetext: '',
            category: document.querySelector('#page-category').value,
            errors: [],
            contentItem: document.getElementById('documentUuid').value,
            hippoContentItem: window.location.pathname
        };

        if (feedback.type === 'no') {
            feedback.reason = this.fields.no.reason.value;
            feedback.freetext = this.fields.no.comments.value;
        } else if (feedback.type === 'yesbut') {
            feedback.reason = this.fields.yesbut.reason.value;
            feedback.freetext = this.fields.yesbut.comments.value;
        } else {
            feedback.reason = '';
            feedback.freetext = this.fields.yes.comments.value || '';
        }

        if (!this.validateFeedback(feedback)) {
            //error
            feedback.errors.forEach(function (error) {
                const fieldElement = error.field.nodeName === 'SELECT' ? error.field.parentNode : error.field;
                fieldElement.classList.add('ds_input--error');

                const questionElement = error.field.closest('.ds_question');
                questionElement.classList.add('ds_question--error');

                let messageElement = questionElement.querySelector('.ds_question__message');
                if (!messageElement) {
                    messageElement = document.createElement('p');
                    messageElement.classList.add('ds_question__message', 'ds_question__message--error');
                }

                messageElement.innerHTML = error.message;

                fieldElement.insertAdjacentElement('beforebegin', messageElement);
            });

            // scroll to first error
            feedback.errors[0].field.focus();
            feedback.errors[0].field.closest('.ds_question').scrollIntoView();
        } else {
            // submit feedback
            var xhr = new XMLHttpRequest();
            xhr.open('POST', '/feedback/', true);

            //Send the proper header information along with the request
            xhr.setRequestHeader('Content-Type', 'application/json; charset=utf-8');

            const that = this;

            xhr.onreadystatechange = function () { // Call a function when the state changes.
                if (this.readyState === XMLHttpRequest.DONE && this.status === 201) {
                    // show success message
                    document.getElementById('feedbackThanks').classList.remove('fully-hidden');

                    // hide form
                    that.formElement.classList.add('fully-hidden');

                    // update datalayer
                    window.dataLayer = window.dataLayer || [];
                    window.dataLayer.push({
                        'type': feedback.type,
                        'reason': feedback.reason,
                        'event': 'feedbackSubmit'
                    });
                } else if (this.status >= 400) {
                    that.errorSummary.querySelector('.ds_error-summary__content').innerHTML = '<p>Sorry, we have a problem at our side. Please try again later.</p>';
                    that.errorSummary.classList.remove('fully-hidden');
                    that.errorSummary.scrollIntoView();
                }
            };

            xhr.send(JSON.stringify(feedback));
        }
    },

    removeErrorMessages: function () {
        this.errorSummary.classList.add('fully-hidden');

        [].slice.call(this.formElement.querySelectorAll('.ds_input--error')).forEach(function (inputElement) {
            inputElement.classList.remove('ds_input--error');
        });

        [].slice.call(this.formElement.querySelectorAll('.ds_question--error')).forEach(function (inputElement) {
            inputElement.classList.remove('ds_question--error');
        });

        [].slice.call(this.formElement.querySelectorAll('.ds_question__message--error')).forEach(function (message) {
            message.parentNode.removeChild(message);
        });
    },

    validateFeedback: function (feedback) {
        switch (feedback.type) {
        case 'no':
            if (feedback.freetext === '') {
                feedback.errors.push({
                    field: this.fields.no.comments,
                    message: 'Please enter a comment'
                });
            }

            if (feedback.reason === '') {
                feedback.errors.push({
                    field: this.fields.no.reason,
                    message: 'Please select a reason'
                });
            }
            break;

        case 'yesbut':
            if (!feedback.freetext) {
                feedback.errors.push({
                    field: this.fields.yesbut.comments,
                    message: 'Please enter a comment'
                });
            }
            break;

        default:
            break;
        }

        return feedback.errors.length === 0;
    }
};

export default feedbackForm;
