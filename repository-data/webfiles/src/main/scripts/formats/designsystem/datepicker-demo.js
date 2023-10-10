import DatePicker from '../../../../../node_modules/@scottish-government/design-system/src/components/date-picker/date-picker';
import tracking from '../../../../../node_modules/@scottish-government/design-system/src/base/tools/tracking/tracking';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

window.DS = window.DS || {};
window.DS.components = window.DS.components || {
    DatePicker: DatePicker
};

document.addEventListener('DOMContentLoaded', () => {
    const datePicker = new window.DS.components.DatePicker(
        document.querySelector('[data-module="ds-datepicker"]'),
        {
            imagePath: bloomreachWebfile('/assets/images/icons/')
        }
    );
    datePicker.init();
    tracking.init();
});
