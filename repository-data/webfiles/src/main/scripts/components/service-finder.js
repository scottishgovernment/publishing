'use strict';


class ServiceFinder {
    constructor(serviceFinder) {
        this.serviceFinder = serviceFinder;
        this.selectEl = serviceFinder.querySelector('select');
        this.linkContainerEl = serviceFinder.querySelector('.mg_service-finder__link');
        this.buttonEl = this.linkContainerEl.querySelector('.js-button');
        this.descriptionEl = this.linkContainerEl.querySelector('.js-description');
        this.prefix = serviceFinder.dataset.prefix || '';
    }

    init() {
        this.selectEl.addEventListener('change', () => {
            this.linkContainerEl.classList.add('fully-hidden');
            this.showButton();
        });

        // on loading or returning to the page, any dropdowns with a LA selected will show a link button
        window.setTimeout(() => {
            if (this.selectEl.value !== '') {
                this.showButton();
            }
        }, 10);
    }

    showButton() {
        const selectedItemIdBase = '#dd-' + this.serviceFinder.querySelector('option:checked').dataset.id;
        const selectedLinkEl = this.serviceFinder.querySelector(selectedItemIdBase + '-link');
        const selectedDescriptionEl = this.serviceFinder.querySelector(selectedItemIdBase + '-description');

        this.buttonEl.innerHTML = this.prefix + ' ' + selectedLinkEl.innerHTML;
        this.buttonEl.href = selectedLinkEl.href;
        this.buttonEl.classList.add('ds_button');
        this.descriptionEl.innerHTML = selectedDescriptionEl.innerHTML;

        this.linkContainerEl.classList.remove('fully-hidden');
    }
}

export default ServiceFinder;
