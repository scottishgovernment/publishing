import bloomreachWebfile from '../tools/bloomreach-webfile';
import commonForms from "../tools/forms";
import temporaryFocus from "@scottish-government/design-system/src/base/tools/temporary-focus/temporary-focus";

const confirmationMessageTemplate = require('../templates/confirmation-message');

class AddAnother {
    constructor(settings) {
        this.addString = settings.addString || 'You have added an item.';
        this.changeString = settings.changeString || 'You have changed an item.';
        this.deleteString = settings.deleteString || 'You have deleted an item.';
        this.nameString = settings.nameString || 'Item';
        this.valueProp = settings.valueProp || 'name';

        this.buttonText = settings.buttonText || 'Add another';
        this.container = settings.container;
        this.data = settings.data || [];
        this.deleteTemplate = settings.deleteTemplate;
        this.formTemplate = settings.formTemplate;
        this.formTemplateRenderArguments = settings.fomrTemplateRenderArguments;
        this.onRenderForm = settings.onRenderForm;
        this.onSave = settings.onSave;
        this.summaryTemplate = settings.summaryTemplate;

        this.elementsToHide = [].slice.call(document.querySelectorAll('.js-form-page:not(.fully-hidden) .mg_form-container__nav, #form-progress, .js-form-page:not(.fully-hidden) .mg_form-container__title'));
    }

    init() {
        this.addEventListeners();
        this.renderSummary(false, false);
    }

    addEventListeners() {
        if (!this.container.classList.contains('js-event-listeners-added')) {
            this.container.addEventListener('click', event => {
                let confirmationMessageObj = {};

                if (event.target.classList.contains('js-add-another')) {
                    // switch to edit view with a new item
                    this.renderForm({ number: this.data.length + 1 });
                }
                if (event.target.classList.contains('js-change-item')) {
                    // switch to edit view with an existing item
                    this.editingIndex = Number(event.target.dataset.index);

                    this.renderForm({
                        ...{ number: this.editingIndex + 1 },
                        ...{ data: this.data[this.editingIndex] }
                    });
                }
                if (event.target.classList.contains('js-remove-item')) {
                    // switch to read-only view with existing item
                    this.editingIndex = Number(event.target.dataset.index);
                    this.renderDeleteConfirmation(this.data[this.editingIndex], this.editingIndex);
                }
                if (event.target.classList.contains('js-confirm-remove-item')) {
                    // delete item, re-render summary
                    const match = this.deleteString.match(/{{(\w+)}}/);
                    let bodyString;

                    if (match) {
                        bodyString = match.input.replace(match[0], this.data[event.target.dataset.index][match[1]]);
                    }

                    this.data.splice(event.target.dataset.index, 1);
                    this.renderSummary({ title: `${this.nameString} deleted successfully`, body: bodyString });
                }
                if (event.target.classList.contains('js-save-item')) {
                    // add/change item, switch to summary view to summary
                    if (!commonForms.validateStep(this.container)) return;

                    const data = this.onSave(this.container);

                    if (typeof this.editingIndex === 'number') {
                        const match = this.changeString.match(/{{(\w+)}}/);
                        let bodyString;

                        if (match) {
                            bodyString = match.input.replace(match[0], data[match[1]]);
                        }

                        this.data[this.editingIndex] = data;
                        confirmationMessageObj = {title: `${this.nameString} changed successfully`, body: bodyString}
                    } else {
                        const match = this.addString.match(/{{(\w+)}}/);
                        let bodyString;

                        if (match) {
                            bodyString = match.input.replace(match[0], data[match[1]]);
                        }

                        this.data.push(data);
                        confirmationMessageObj = {title: `${this.nameString} added successfully`, body: bodyString};
                    }

                    this.renderSummary(confirmationMessageObj);
                }
                if (event.target.classList.contains('js-cancel-item')) {
                    // clear error summary
                    const errorContainerElement = document.querySelector('.ds_layout__error');
                    errorContainerElement.innerHTML = '';

                    // switch to summary view
                    this.renderSummary();
                }
            });

            this.container.classList.add('js-event-listeners-added');
        }
    }

    getData() {
        return this.data;
    }

    renderDeleteConfirmation(item, index) {
        this.elementsToHide.forEach(item => item.classList.add('fully-hidden'));
        this.container.innerHTML = this.deleteTemplate.render({ item: item, index: index });
        temporaryFocus(this.container);
    }

    renderForm(item) {
        this.elementsToHide.forEach(item => item.classList.add('fully-hidden'));

        this.container.innerHTML = this.formTemplate.render({
            ...item, ...this.formTemplateRenderArguments
        });
        if (typeof this.onRenderForm === 'function') {
            this.onRenderForm(this.container);
        }
        temporaryFocus(this.container);
    }

    renderSummary(confirmationMessageObj, focusOnStatusMessage = true) {
        this.elementsToHide.forEach(item => item.classList.remove('fully-hidden'));
        delete this.editingIndex;

        this.container.innerHTML = this.summaryTemplate.render({
            actions: ['edit', 'delete'],
            addButtonText: this.buttonText,
            data: this.data,
            iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg')
        });

        if (focusOnStatusMessage) {
            temporaryFocus(this.container.querySelector('.js-status'));
        }

        if (confirmationMessageObj) {
            this.container.querySelector('.js-message-container').innerHTML = confirmationMessageTemplate.render({
                iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg'),
                ...confirmationMessageObj
            });
        }
    }
}

export default AddAnother;
