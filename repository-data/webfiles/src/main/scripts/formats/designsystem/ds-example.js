import { initAll } from '@scottish-government/design-system/src/all';
import bloomreachWebfile from '../../tools/bloomreach-webfile';

const dsExample = {
    init: function () {
        this.fixIconImagePaths();
        this.fixImagePaths();
        initAll();
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
    }
}

dsExample.init();
