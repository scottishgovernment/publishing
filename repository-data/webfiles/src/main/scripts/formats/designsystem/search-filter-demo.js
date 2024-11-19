import { initAll } from '@scottish-government/design-system/src/all';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

const dsSearchFilter = {
    init: function () {
        this.fixIconImagePaths();
        this.setupDatePicker();
    },

    fixIconImagePaths: () => {
        const iconElements = [].slice.call(document.querySelectorAll('svg.ds_facet__button-icon use, svg.ds_icon use'));

        iconElements.forEach(element => {
            element.setAttribute('href', bloomreachWebfile(element.getAttribute('href')));
        });
    },

    setupDatePicker: () => {
        const dpInterval = window.setInterval(function() {
            if (window.DS) {
                const datePickers = [].slice.call(document.querySelectorAll('[data-module="ds-datepicker"]'));
                datePickers.forEach(datePickerItem => {
                const datepicker = new window.DS.components.DSDatePicker(datePickerItem, {
                    imagePath: bloomreachWebfile('/assets/images/icons/')
                });
                datepicker.init();
                });
                clearInterval(dpInterval);

                initAll();
            }
        }, 50);
    }
}

dsSearchFilter.init();
