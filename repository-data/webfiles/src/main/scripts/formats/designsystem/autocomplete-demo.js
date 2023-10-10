import Autocomplete from '../../../../../node_modules/@scottish-government/design-system/src/components/autocomplete/autocomplete';
import tracking from '../../../../../node_modules/@scottish-government/design-system/src/base/tools/tracking/tracking';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

window.DS = window.DS || {};
window.DS.components = window.DS.components || {
    Autocomplete: Autocomplete
};

document.addEventListener('DOMContentLoaded', () => {
    const searchElement = document.getElementById('site-search');

    const autocomplete = new window.DS.components.Autocomplete(
        document.querySelector('#site-search-autocomplete'),
        bloomreachWebfile('/assets/data/autocomplete-dummy-data.json#'),
        {
            suggestionMappingFunction: function (suggestionsObj) {

                const responseObj = JSON.parse(suggestionsObj.response).map(suggestionsObj => ({
                    key: '',
                    displayText: suggestionsObj,
                    weight: '',
                    type: '',
                    category: ''
                }));
                const filteredResults = responseObj.filter(item => (item.displayText.toLowerCase().indexOf(searchElement.value.toLowerCase()) > -1));

                return filteredResults.slice(0,6);
            }
        });
    autocomplete.init();
    tracking.init();

    // block form submission
    const form = document.querySelector('form');
    form.addEventListener('submit', event => { event.preventDefault() });
});

export default Autocomplete;
