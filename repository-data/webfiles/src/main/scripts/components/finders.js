'use strict';

class Finder {
    constructor(finder) {
        this.finder = finder;
        this.selectEl = this.finder.querySelector('select');
    }

    init() {
        this.selectEl.addEventListener('change', () => {
            this.showButton();
        });

        // on loading or returning to the page, any dropdowns with a LA selected will show a link button
        window.setTimeout(() => {
            if (this.selectEl.value !== '') {
                this.showButton();
            }
        }, 0);
    }

    showButton() {
        const selectedLinkElement = this.finder.querySelector('#dd-' + this.selectEl.querySelector('option:checked').dataset.id);
        const linkElements = [].slice.call(this.finder.querySelectorAll('a'));

        linkElements.forEach((linkElement) => {
            linkElement.classList.add('fully-hidden');
            linkElement.setAttribute('aria-hidden', true);
        });

        selectedLinkElement.classList.remove('fully-hidden');
        selectedLinkElement.setAttribute('aria-hidden', false);
    }
}

const finders = {
    init: () => {
        const finders = [].slice.call(document.querySelectorAll('.dd.finder-hero'));
        finders.forEach(finder => new Finder(finder).init());
    }
};

export default finders;
