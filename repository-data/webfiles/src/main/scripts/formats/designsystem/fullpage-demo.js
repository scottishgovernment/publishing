
import { initAll } from '../../../../../node_modules/@scottish-government/design-system/src/all';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

const dsFullPage = {
    init: function () {
        this.fixIconImagePaths();
        this.fixImagePaths();
        this.setupDatePicker();
    },

    fixIconImagePaths: () => {
          const iconElements = [].slice.call(document.querySelectorAll('svg.ds_icon use'));

          iconElements.forEach(element => {
              element.setAttribute('href', bloomreachWebfile(element.getAttribute('href')));
          });
      },

      fixImagePaths: () => {
          const imageElements = [].slice.call(document.querySelectorAll('img[src*="/assets/images/"]'));

          imageElements.forEach(element => {
              element.setAttribute('src', bloomreachWebfile(element.getAttribute('src')));
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

dsFullPage.init();