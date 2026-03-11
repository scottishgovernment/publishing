import DatePicker from '@scottish-government/design-system/dist/scripts/components/date-picker/date-picker';
import tracking from '@scottish-government/design-system/dist/scripts/base/tools/tracking/tracking';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

window.DS = window.DS || {};
window.DS.components = window.DS.components || {
    DatePicker: DatePicker
};

document.addEventListener('DOMContentLoaded', () => {
    const datePickers = [].slice.call(document.querySelectorAll('[data-module="ds-datepicker"]'));
    datePickers.forEach(datePickerItem => {
      const datepicker = new window.DS.components.DatePicker(datePickerItem, {
        imagePath: bloomreachWebfile('/assets/images/icons/')
      });
      datepicker.init();
    });
    tracking.init();
});
