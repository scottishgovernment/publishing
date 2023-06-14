import { initAll } from '../../../../../node_modules/@scottish-government/pattern-library/src/all';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

const dsExample = {
    init: function () {
        this.fixIconImagePaths();
        initAll();
    },

    fixIconImagePaths: () => {
        const iconElements = [].slice.call(document.querySelectorAll('svg.ds_icon use'));

        iconElements.forEach(element => {
            element.setAttribute('href', bloomreachWebfile(element.getAttribute('href')));
        });
    }
}

dsExample.init();
