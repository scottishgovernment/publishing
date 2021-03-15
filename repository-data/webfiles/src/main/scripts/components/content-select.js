'use strict';

class ContentSelect {
    constructor(contentSelect) {
        this.contentSelect = contentSelect;
        this.selectEl = this.contentSelect.querySelector('select');
        if (this.contentSelect.dataset.type === 'button') {
            this.type = 'button';
        } else if (this.contentSelect.dataset.type === 'block') {
            this.type = 'block';
        }
    }

    init() {
        this.selectEl.addEventListener('change', () => {
            if (this.type === 'button') {
                this.showButton();
            } else if (this.type === 'block') {
                this.showBlock();
            }
        });

        // on loading or returning to the page, any dropdowns with a LA selected will show a link button
        window.setTimeout(() => {
            if (this.selectEl.value !== '') {
                this.showButton();
            }
        }, 0);
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

    showBlock() {
        const selectedContentElement = this.contentSelect.querySelector('#' + this.selectEl.querySelector('option:checked').dataset.block);
        const contentElements = [].slice.call(this.contentSelect.querySelectorAll('.content-block'));

        contentElements.forEach((contentElement) => {
            contentElement.classList.add('fully-hidden');
            contentElement.setAttribute('aria-hidden', true);
        });

        selectedContentElement.classList.remove('fully-hidden');
        selectedContentElement.setAttribute('aria-hidden', false);
    }
}

const contentSelects = {
    init: () => {
        const contentSelectEls = [].slice.call(document.querySelectorAll('.js-contentselect'));
        contentSelectEls.forEach(contentSelect => new ContentSelect(contentSelect).init());
    }
};

export default contentSelects;
