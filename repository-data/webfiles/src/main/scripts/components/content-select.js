'use strict';

class ContentSelect {
    constructor(contentSelect) {
        this.contentSelect = contentSelect;
        this.selectEl = this.contentSelect.querySelector('select');
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
        }, 10);
    }

    showButton() {
        const selectedLinkElement = this.contentSelect.querySelector('#dd-' + this.selectEl.querySelector('option:checked').dataset.id);
        const linkElements = [].slice.call(this.contentSelect.querySelectorAll('a'));

        linkElements.forEach((linkElement) => {
            linkElement.classList.add('fully-hidden');
            linkElement.setAttribute('aria-hidden', true);
        });

        if (selectedLinkElement) {
            selectedLinkElement.classList.remove('fully-hidden');
            selectedLinkElement.setAttribute('aria-hidden', false);
        }
    }
}

export default ContentSelect;
