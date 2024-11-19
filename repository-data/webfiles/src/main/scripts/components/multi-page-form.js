// MULTI-PAGE FORM

'use strict';

import _ from '../vendor/lodash/dist/lodash.custom';
import commonForms from '../tools/forms';
import bloomreachWebfile from '../tools/bloomreach-webfile';
import temporaryFocus from '@scottish-government/design-system/src/base/tools/temporary-focus/temporary-focus';

const iconSpritePath = bloomreachWebfile('/assets/images/icons/icons.stack.svg');
const navButtonsTemplate = require('../templates/mygov/form-nav-buttons');
const progressTrackerTemplate = require('../templates/mygov/progress-tracker');

class MultiPageForm {
    constructor(options) {
        this.sections = options.sections;
        this.formDataObject = options.formDataObject;
        this.formEvents = options.formEvents || {};
        this.formMapping = options.formMapping;
        this.formTemplate = options.formTemplate;
        this.modifiers = options.modifiers || [];
        this.progressTrackerType = options.progressTrackerType || null;
    }

    init() {
        this.setCurrentPage(this.sections[0].pages[0]);
        this.goToPage(this.getCurrentPage());
        this.configureFieldMapping();
        this.addEventListeners();
    }

    /**
     * Note: This is a bit of a dumping ground for event listeners. It might be worth splitting this
     * into a more sensible structure.
     */
    addEventListeners() {
        /**
         * If a user has interacted with the accordion panel, we don't want it to be affected by the
         * auto open/close when moving to a new section. User choice should be retained.
         */
        const progressTrackerContainer = document.querySelector('#form-progress');
        progressTrackerContainer.addEventListener('change', event => {
            if (event.target.classList.contains('ds_accordion-item__control')) {
                this.sections.filter(section => section.slug === event.target.dataset.section)[0].override = true;
            }
        });

        window.addEventListener('popstate', () => {
            this.goToPageFromCurrentUrl();
        });

        document.addEventListener('click', event => {
            if (event.target.classList.contains('js-next-page')) {
                const page = this.getCurrentPage();

                if (this.validatePage(page)) {
                    if (page.triggerEventOnExit) {
                        if (this.formEvents[page.triggerEventOnExit]) {
                            this.formEvents[page.triggerEventOnExit].apply();
                        } else {
                            console.log('Cannot find form event "' + page.triggerEventOnExit + '"');
                        }
                    }

                    // set complete
                    page.complete = true;
                    this.goToPage(this.getNextPage());
                }
            }

            if (event.target.classList.contains('js-prev-page')) {
                this.goToPage(this.getPreviousPage());
            }

            if (event.target.classList.contains('js-summary-page')) {
                // dev query: is this a weak way of finding the summary page?
                this.goToPage(this.getPage('slug', 'review'));
            }

            if (event.target.classList.contains('js-check-progress')) {
                event.preventDefault();
                temporaryFocus(document.querySelector(event.target.getAttribute('href')));
            }
        });

        /**
         * Prompt user to confirm page exit to help prevent accidental data loss
         * @param {event} event
         */
        const beforeUnloadHandler = (event) => {
            const initialDataObject = this.initialDataObject || {};
            if (!this.pauseUnloadEvent && !_.isEqual(initialDataObject, this.formDataObject)) {
                // Recommended
                event.preventDefault();

                // Included for legacy support, e.g. Chrome/Edge < 119
                event.returnValue = true;
            }
            this.pauseUnloadEvent = false;
        };
        window.addEventListener("beforeunload", beforeUnloadHandler);
        this.pauseUnloadEvent = false;
    }

    /**
     * Tie data paths in the formMapping object to elements in the DOM
     */
    configureFieldMapping() {
        for (const key in this.formMapping) {
            if (!this.formMapping.hasOwnProperty(key)) { continue; }

            if (this.formMapping[key].name === 'PostcodeLookup') {
                this.mapPostcodeLookup(key, this.formMapping[key]);
            } else {
                // populate initial values
                let value = _.get(this.formDataObject, key) || '';
                let elements = [].slice.call(document.querySelectorAll(this.formMapping[key]));

                if (elements.length > 1 && elements[0].type === 'radio') {
                    // special case: radio buttons
                    for (let i = 0, il = elements.length; i < il; i++) {
                        if (elements[i].value === value) {
                            elements[i].checked = true;
                            break;
                        }
                    }
                } else if (elements[0] && elements[0].type === 'checkbox') {
                    // special case: checkbox
                    if (value === true) {
                        elements[0].checked = true;
                    }
                } else {
                    if (elements.length) {
                        elements[0].value = value;
                    }
                }

                this.mapField(key, this.formMapping[key]);
            }
        }
    }

    /**
     * Get the "current" page from the form sections object
     * @returns {object}
     */
    getCurrentPage() {
        return this.getPage('current', true);
    }

    /**
     * Transforms the nested sections/pages in the form sections object into a flat array of pages
     * @returns {object}
     */
    getFlattenedPages() {
        let flattenedPages = [];

        for (const section of this.sections) {
            flattenedPages = flattenedPages.concat(section.pages);
        }

        return flattenedPages;
    }

    /**
     * Get the page after the current page in the form sections object
     * @returns {object}
     */
    getNextPage() {
        return this.getRelativePage(1);
    }

    /**
     * Get a page from the form sections object that matches some provided conditions
     * @param {string} property - property on the page object to match
     * @param {string} condition - condition to match
     * @returns
     */
    getPage(property, condition) {
        const flattenedPages = this.getFlattenedPages();
        const index = flattenedPages.findIndex(page => page[property] === condition);
        return flattenedPages[index];
    }

    /**
     * Get the page before the current page in the form sections object
     * @returns {object}
     */
    getPreviousPage() {
        return this.getRelativePage(-1);
    }

    /**
     * Get a page relative to the current page by a provided offset. For example getRelativePage(1)
     * would get the next page, getRelativePage(-1) would get the previous page
     * @param {number} offset
     * @returns {object} page
     */
    getRelativePage(offset) {
        const flattenedPages = this.getFlattenedPages().filter(page => page.disabled !== true);
        const currentIndex = flattenedPages.findIndex(page => page.slug === this.getCurrentPage().slug);

        return flattenedPages[currentIndex + offset];
    }

    /**
     * Go to (show in browser) a given page
     * @param {object} page
     * @param {boolean} addToHistory
     */
    goToPage(page, addToHistory = true) {
        this.setCurrentPage(page);
        this.populatePage(page);
        page.visited = true;

        if (page.triggerEventOnEntry) {
            if (this.formEvents[page.triggerEventOnEntry]) {
                this.formEvents[page.triggerEventOnEntry].apply();
            } else {
                console.log('Cannot find form event "' + page.triggerEventOnEntry + '"');
            }
        }

        if (addToHistory) {
            window.history.pushState('', '', `#!/${page.slug}`);
        }

        const mainContent = document.getElementById('main-content');
        const pageElement = mainContent.querySelector('.js-form-page:not(.fully-hidden)');

        this.initDSComponents(pageElement);
        window.setTimeout(() => {
            temporaryFocus(mainContent);
            mainContent.scrollIntoView();
        }, 0);
    }

    /**
     * Get page/section from the URL and render the matching page
     */
    goToPageFromCurrentUrl() {
        const pageSlug = window.location.hash.substring(window.location.hash.indexOf('#!/') + 3);
        const page = this.getPage('slug', pageSlug);
        if (page) {
            this.goToPage(page, false);
        } else {
            console.log('Page not found ' + pageSlug);
        }
    }

    /**
     * Initialise any new DS components in the page
     * @param {element} scope - element to init DS components within
     */
    initDSComponents(scope = document) {
        window.DS.initAll(scope);
    }

    /**
     * Bind input value changes from the DOM to the formDataObject's data
     * @param {string} dataPath - path to the field in the form data object
     * @param {string} selector - CSS selector for the field
     */
    mapField(dataPath, selector) {
        const elementArray = [].slice.call(document.querySelectorAll(selector));
        let fieldModifier = function (value) { return value; }

        for (let i = 0, il = this.modifiers.length; i < il; i++) {
            if (dataPath.match(this.modifiers[0].pattern)) {
                fieldModifier = this.modifiers[0].transformFunction;
                break;
            }
        }

        const setDataAndDependantText = (dataPath, value, text) => {
            _.set(this.formDataObject, dataPath, value);
                if (text) {
                    const dependantElements = document.querySelectorAll('[data-mapped="' + dataPath + '"]');

                    for (const dependantElement of dependantElements) {
                        dependantElement.innerText = text;
                    }

                    _.set(this.formDataObject, dataPath + '_text', text);
                }
        }

        for (const element of elementArray) {
            element.addEventListener('change', event => {
                let text, value;

                // get value
                if (element.type === 'checkbox') {
                    if (elementArray.length > 1) {
                        const checkedItems = elementArray.filter(item => item.checked);
                        const itemsArray = [];
                        for (const item of checkedItems) {
                            itemsArray.push({
                                id: item.value,
                                name: document.querySelector(`label[for="${item.id}"]`).innerText
                            });
                        }
                        value = itemsArray;
                    } else {
                        value = element.checked;
                    }
                } else if (element.type === 'radio') {
                    const checkedRadio = document.querySelector(`[name="${event.target.name}"]:checked`);
                    value = fieldModifier(checkedRadio.value);
                    text = document.querySelector(`label[for="${checkedRadio.id}"]`).innerText;
                } else if (element.nodeName === 'SELECT') {
                    value = fieldModifier(element.options[element.selectedIndex].value);
                    text = element.options[element.selectedIndex].innerText;
                } else {
                    value = fieldModifier(event.currentTarget.value);
                }

                // set data
                setDataAndDependantText(dataPath, value, text)
            });

            element.addEventListener('blur', event => {
                let text, value;

                // get value
                if (element.nodeName === 'SELECT') {
                    value = fieldModifier(element.options[element.selectedIndex].value);
                } else if (element.type === 'checkbox') {
                    value = element.checked;
                } else {
                    value = fieldModifier(event.currentTarget.value)
                }

                // set data
                setDataAndDependantText(dataPath, value, text)
            });
        }
    }

    /**
     * Special field mapping function for postcode lookups
     * @param {string} dataPath - path to the field in the form data object
     * @param {Object} lookup - the postcode lookup instance
     */
    mapPostcodeLookup(dataPath, lookup) {
        lookup.resultsSelectElement.addEventListener('change', event => {
            if (lookup.options.readOnly) {
                _.set(this.formDataObject, dataPath, lookup.getAddressAsString());
            } else {
                _.set(this.formDataObject, dataPath, lookup.getAddressAsObject());
            }
        });
    }

    /**
     * Populate the navigation buttons on a page
     * includes logic for when to show particular buttons
     * @param {Object} page - page object from formSections
     */
    populateNavButtons(page) {
        const navButtonsContainer = document.querySelector(`[data-slug="${page.slug}"] .mg_form-container__nav`);
        if (!navButtonsContainer) {
            return;
        }

        const flattenedPages = this.getFlattenedPages();
        const summaryPage = this.getPage('slug', 'review');
        const templateData = {
            back: flattenedPages.indexOf(page) === 0 ? false : page.backText || 'Back',
            next: flattenedPages.indexOf(page) === flattenedPages.length - 1 ? false : page.nextText || 'Continue',
            summary: !!summaryPage.visited && !page.noSummaryButton
        }

        // render template
        navButtonsContainer.innerHTML = navButtonsTemplate.render(templateData);
    }

    /**
     * Populate the HTML for the specified page
     * @param {object} page - page (from form sections object)
     */
    populatePage(page) {
        this.populateNavButtons(page);
        this.populateProgressTracker();
        this.showPage(page);
        document.title = `${page.title} - Mygov`;
    }

    /**
     * Populate the progress tracker from the current state of the form sections object
     */
    populateProgressTracker() {
        const progressTrackerContainer = document.querySelector('#form-progress');
        const progressAccordionItems = [].slice.call(progressTrackerContainer.querySelectorAll('.ds_accordion-item'));

        for (let i = 0, il = progressAccordionItems.length; i < il; i++) {
            this.sections[i].open = !!progressAccordionItems[i].querySelectorAll('.ds_accordion-item__control:checked').length
        }

        let completedSectionCount = 0;

        const currentSection = this.sections.filter(section => section.pages.filter(page => page.current).length > 0)[0];

        for (const section of this.sections) {
            const activePages = section.pages.filter(page => !page.disabled);
            if (activePages.filter(page => page.complete).length === activePages.length) {
                section.complete = true;
            }

            if (section.complete) {
                completedSectionCount = completedSectionCount + 1;
            }

            const isCurrentSection = section.pages.filter(page => page.current).length > 0;

            if (!section.override) {
                section.open = isCurrentSection;
            }
        }

        if (!currentSection.hideTracker) {
            progressTrackerContainer.innerHTML = progressTrackerTemplate.render({
                completedSectionCount: completedSectionCount,
                sectionCount: this.sections.filter(item => item.showInTracker !== false).length,
                sections: this.sections,
                iconPath: iconSpritePath,
                type: this.progressTrackerType
            });

            window.DS.initAll(progressTrackerContainer);
        } else {
            progressTrackerContainer.innerHTML = '';
        }
    }

    /**
     * Mark a given page as "current" in the form section sobject
     * @param {object} page
     */
    setCurrentPage(page) {
        for (const section of this.sections) {
            for (const item of section.pages) {
                item.current = item.slug === page.slug;
            }
        }
    }

    /**
     * Show the specified page in the browser (show this page, hide other pages)
     * @param {object} page
     */
    showPage(page) {
        [].slice.call(document.querySelectorAll('.js-form-page')).forEach(formPage => {
            if (formPage.dataset.slug === page.slug) {
                formPage.classList.remove('fully-hidden');
            } else {
                formPage.classList.add('fully-hidden');
            }
        });
    }

    /**
     * Perform form validation on inputs on the specified page
     * @param {object} page
     * @returns {boolean}
     */
    validatePage(page) {
        return commonForms.validateStep(document.querySelector(`[data-slug="${page.slug}"]`));
    }
};

export default MultiPageForm;
