'use strict';

import commonForms from "../tools/forms";

class ServiceFinder {
    constructor(serviceFinder) {
        this.serviceFinder = serviceFinder;
        this.selectEl = this.serviceFinder.querySelector('select');
        this.buttonEl = this.serviceFinder.querySelector('.js-submit-finder');
        this.keycodes = {
            'space': 32
        };
    }

    init() {
        this.selectEl.addEventListener('change', () => {
            this.buttonEl.href = this.selectEl.selectedOptions[0].dataset.url || '';
        });

        this.serviceFinder.addEventListener('submit', event => {
            event.preventDefault();

            if (this.validateSelect()) {
                window.location.href = this.selectEl.selectedOptions[0].dataset.url;
            }
        });

        this.buttonEl.addEventListener('click', event => {
            console.log('click')
            if (!this.validateSelect()) {
                event.preventDefault();
            }
        });

        this.buttonEl.addEventListener('keypress', event => {
            if (event.keyCode === this.keycodes.space) {
                event.preventDefault();
                this.buttonEl.click();
            }
        });
    }

    validateSelect() {
        const valid = !(this.selectEl.value === '' || this.selectEl.selectedOptions[0].disabled);

        commonForms.toggleCurrentErrors(this.selectEl, valid, 'no-service', this.serviceFinder.dataset.errormessage);

        if (!valid) {
            this.selectEl.classList.add('ds_input--error');
        } else {
            this.selectEl.classList.remove('ds_input--error');
        }

        return valid;
    }
}

export default ServiceFinder;
