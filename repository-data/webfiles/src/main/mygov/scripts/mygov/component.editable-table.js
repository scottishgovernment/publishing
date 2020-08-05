// EDITABLE TABLE

/* global require */

'use strict';

import commonForms from './common.forms';
import _ from 'tinydash';
import $ from 'jquery';

const editableTableTemplate = require('../templates/editable-table.hbs');

export default class EditableTable {
    constructor(settings) {

        this.settings = {};

        this.settings = $.extend(this.settings, settings);
        this.settings.data = _.get(window.formObject, this.settings.dataPath);

        // self-init
        this.init();
    }

    init() {
        var editableTable = this;

        this.renderTable();

        this.settings.tableContainer.on('click', '.js-edit-button', function (event) {
            event.preventDefault();
            var row = $(this).closest('tr');
            row.addClass('editable-table__edit-row');
            row.find('input, select').filter(':first').focus();

            var tableContainer = editableTable.settings.tableContainer;
            tableContainer.find('button:not(.editable-table__edit)').prop('disabled', true);
        });

        this.settings.tableContainer.on('click', '.js-cancel-button', function (event) {
            event.preventDefault();
            // rerender table
            editableTable.renderTable();
        });

        this.settings.tableContainer.on('click', '.js-remove-button', function (event) {
            event.preventDefault();
            // update data
            var row = $(this).closest('tr');
            var index = row.prevAll().length;

            editableTable.removeEntry(index, row);

            // rerender table
            editableTable.renderTable();
        });

        this.settings.tableContainer.on('click', '.js-save-button', function (event) {
            event.preventDefault();
            // update data
            var row = $(this).closest('tr');
            var index = row.prevAll().length;
            var inputs = row[0].querySelectorAll('.js-value');

            if (editableTable.addOrEditEntry(index, inputs, row)) {
                // rerender table
                editableTable.renderTable();
            }
        });

        this.settings.tableContainer.on('click', '.js-show-add-form', function (event) {
            event.preventDefault();
            editableTable.settings.tableContainer.children('.editable-table').addClass('editable-table--adding');
            editableTable.settings.tableContainer.find('button:not(.editable-table__add)').prop('disabled', true);

            editableTable.settings.tableContainer.find('.js-add-form input, .js-add-form select').filter(':first').focus();
        });

        this.settings.tableContainer.on('click', '.js-add-button', function (event) {
            event.preventDefault();
            editableTable.settings.data = editableTable.settings.data || [];

            // creates a new entry
            var addForm = $(this).closest('.js-add-form');
            var index = editableTable.settings.data.length;
            var inputs = addForm[0].querySelectorAll('.js-value');

            if (editableTable.addOrEditEntry(index, inputs, addForm)) {
                // rerender table
                editableTable.renderTable();
            }
        });

        this.settings.tableContainer.on('click', '.js-cancel-add-button', function (event) {
            event.preventDefault();
            // rerender table
            editableTable.renderTable();
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

        var tableHtml = editableTableTemplate({
            data: this.settings.data,
            fields: this.settings.fields,
            addText: this.settings.addText || 'Add',
            name: this.settings.tableContainer.attr('id') ? this.settings.tableContainer.attr('id').replace('#', '') : ''
        });
        this.settings.tableContainer.html(tableHtml);
    }

    removeEntry (index) {
        this.settings.data.splice(index, 1);

        _.set(window.formObject, this.settings.dataPath, this.settings.data);

        this.settings.tableContainer.children('.editable-table').removeClass('editable-table--adding');
    }

    addOrEditEntry (index, inputs, container) {
        var editableTable = this;
        var tempEntry = {};

        // loop through the inputs to build up an object to save
        for (var i = 0, il = inputs.length; i < il; i++) {
            var field = inputs[i];
            var value;

            // determine the value
            if ($(field).hasClass('js-radio-group')) {
                value = $(field).find(':checked').val();
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

        var fieldsThatNeedToBeValidated = container.find('[data-validation]:visible');

        fieldsThatNeedToBeValidated.each(function (index, element) {
            var validations = element.getAttribute('data-validation').split(' ');
            var validationChecks = [];
            for (var i = 0, il = validations.length; i < il; i++) {
                if (commonForms[validations[i]]) {
                    validationChecks.push(commonForms[validations[i]]);
                }
            }

            commonForms.validateInput($(this), validationChecks);
        });

        var invalidFields = container.find('[aria-invalid="true"]');

        if (invalidFields.length) {
            return false;
        } else {
            // "write" the data
            var dataPath = editableTable.settings.dataPath + '[' + index + ']';
            _.set(window.formObject, dataPath, tempEntry);

            editableTable.settings.data[index] = tempEntry;

            editableTable.settings.tableContainer.children('.editable-table').removeClass('editable-table--adding');

            return true;
        }
    }
}
