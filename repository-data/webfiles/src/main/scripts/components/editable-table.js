// EDITABLE TABLE

/* global require */

'use strict';

import commonForms from '../tools/forms';
import _ from '../vendor/lodash/dist/tinydash.es6';

const editableTableTemplate = require('../templates/editable-table');

function extend(){
    for(var i=1; i<arguments.length; i++)
        for(var key in arguments[i])
            if(arguments[i].hasOwnProperty(key))
                arguments[0][key] = arguments[i][key];
    return arguments[0];
}

export default class EditableTable {
    constructor(settings) {

        this.settings = {};

        this.settings = extend(this.settings, settings);
        this.settings.data = _.get(window.formObject, this.settings.dataPath);

        // self-init
        this.init();
    }

    init() {
        var editableTable = this;

        this.renderTable();

        this.settings.tableContainer.addEventListener('click', (event) => {
            event.preventDefault();
        });

        this.settings.tableContainer.addEventListener('click', (event) => {
            event.preventDefault();

            if (event.target.classList.contains('js-edit-button')) {
                const row = event.target.closest('tr');

                row.classList.add('editable-table__edit-row');
                row.querySelector('input, select').focus();

                const viewButtons = editableTable.settings.tableContainer.querySelectorAll('button:not(.editable-table__edit)');

                viewButtons.forEach((button => button.disabled = true));
            }

            if (event.target.classList.contains('js-cancel-button')) {
                editableTable.renderTable();
            }

            if (event.target.classList.contains('js-remove-button')) {
                const row = event.target.closest('tr');
                var index = parseInt(row.dataset.index, 10);
                editableTable.removeEntry(index, row);
console.log(index, row)
                // rerender table
                editableTable.renderTable();
            }

            if (event.target.classList.contains('js-save-button')) {
                var row = event.target.closest('tr');
                var index = parseInt(row.dataset.index, 10);
                var inputs = row.querySelectorAll('.js-value');

                if (editableTable.addOrEditEntry(index, inputs, row)) {
                    // rerender table
                    editableTable.renderTable();
                }
            }

            if (event.target.classList.contains('js-show-add-form')) {
                const table = editableTable.settings.tableContainer.querySelector('.editable-table');
                table.classList.add('editable-table--adding');

                const nonAddButtons = editableTable.settings.tableContainer.querySelectorAll('button:not(.editable-table__add)');
                nonAddButtons.forEach(button => button.disabled = true);

                editableTable.settings.tableContainer.querySelector('.js-add-form input, .js-add-form select').focus();
            }

            if (event.target.classList.contains('js-add-button')) {
                editableTable.settings.data = editableTable.settings.data || [];

                // creates a new entry
                var addForm = event.target.closest('.js-add-form');
                var index = editableTable.settings.data.length;
                var inputs = addForm.querySelectorAll('.js-value');

                if (editableTable.addOrEditEntry(index, inputs, addForm)) {
                    // rerender table
                    editableTable.renderTable();
                }
            }

            if (event.target.classList.contains('js-cancel-add-button')) {
                editableTable.renderTable();
            }
        });
    }

    renderTable () {

        // DATAMUNGE!
        var data = this.settings.data || [];
        var fields = this.settings.fields || [];

        for(var i = 0, il = data.length; i < il; i++) {
            for (var j = 0, jl = fields.length; j < jl; j++) {
                // copy dropdown options
                if (data[i].hasOwnProperty(fields[j].slug) && fields[j].options) {
                    data[i][fields[j].slug + '_options_nodisplay'] = fields[j].options;
                    data[i].slug_nodisplay = fields[j].slug;
                }
            }
        }

        var tableHtml = editableTableTemplate.render({
            data: this.settings.data,
            fields: this.settings.fields,
            addText: this.settings.addText || 'Add',
            name: this.settings.tableContainer.id ? this.settings.tableContainer.id.replace('#', '') : ''
        });
        this.settings.tableContainer.innerHTML = tableHtml;
    }

    removeEntry (index) {
        this.settings.data.splice(index, 1);

        _.set(window.formObject, this.settings.dataPath, this.settings.data);

        this.settings.tableContainer.querySelector('.editable-table').classList.remove('editable-table--adding');
    }

    addOrEditEntry (index, inputs, container) {
        var editableTable = this;
        var tempEntry = {};

        // loop through the inputs to build up an object to save
        for (var i = 0, il = inputs.length; i < il; i++) {
            var field = inputs[i];
            var value;

            // determine the value
            if (field.classList.contains('js-radio-group')) {
                value = field.querySelector(':checked').val();
            } else if (field.tagName === 'INPUT') {
                if (field.getAttribute('type') === 'checkbox') {
                    value = field.value;
                } else {
                    value = field.value;
                }
            } else if (field.tagName === 'SELECT') {
                value = field.options[field.selectedIndex].value;
            }

            // special case: currency -- strip non-numerics & restrict to two decimal points
            if (field.getAttribute('data-format') === 'currency') {
                value = value || '';
                value = value.replace(/[^0-9.]/g, '');
                if (value.indexOf('.') > -1) {
                    value = +value.toFixed(2);
                }
            }

            tempEntry[field.getAttribute('data-field')] = value;
        }

        var fieldsThatNeedToBeValidated = [].slice.call(container.querySelectorAll('[data-validation]'));

        fieldsThatNeedToBeValidated.forEach(element => {
            var validations = element.getAttribute('data-validation').split(' ');
            var validationChecks = [];
            for (var i = 0, il = validations.length; i < il; i++) {
                if (commonForms[validations[i]]) {
                    validationChecks.push(commonForms[validations[i]]);
                }
            }

            commonForms.validateInput($(element), validationChecks);
        });

        var invalidFields = container.querySelectorAll('[aria-invalid="true"]');

        if (invalidFields.length) {
            return false;
        } else {
            // "write" the data
            var dataPath = editableTable.settings.dataPath + '[' + index + ']';
            _.set(window.formObject, dataPath, tempEntry);

            editableTable.settings.data[index] = tempEntry;

            editableTable.settings.tableContainer.querySelector('.editable-table').classList.remove('editable-table--adding');

            return true;
        }
    }
}
