// RPZ CHECKER

'use strict';

import MultiPageForm from '../../components/multi-page-form';
import PostcodeLookup from '../../components/postcode-lookup';
import feedback from '../../components/feedback';
import bloomreachWebfile from '../../tools/bloomreach-webfile';
import commonForms from '../../tools/forms';

const formTemplate = require('../../templates/mygov/rent-pressure-zone-checker');

const formSections = [
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        hideFromSectionNav: true,
        slug: 'overview',
        title: 'Overview',
        pages: [
            {
                slug: 'overview',
                title: 'Overview',
                hideSubsectionNav: true,
                hideSectionNav: true,
                noFormBox: true
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'postcode',
        title: 'Postcode',
        pages: [
            {
                slug: 'postcode',
                title: 'Postcode',
                hideSubsectionNav: true
            }
        ]
    }
];

const rpzChecker = {

    form: new MultiPageForm({
        formSections: formSections,
        formMapping: {},
        formObject: {}
    }),

    init: function () {
        feedback.init();

        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }
        const overviewContent = formTemplateContainer.innerHTML;
        formTemplateContainer.innerHTML = formTemplate.render({
            iconsFile: bloomreachWebfile('/assets/images/icons/icons.stack.svg')
        });
        formTemplateContainer.querySelector('#overview').innerHTML = overviewContent;

        commonForms.appendCaptchaScript();

        this.form.init();

        const rpzComplete = function(){};
        new PostcodeLookup(
            document.getElementById('rpz-checker-postcode-lookup'),
            {  // NOSONAR
            rpz: true,
            rpzComplete: rpzComplete,
            displayNotRPZ: true
        });
    }
};

window.format = rpzChecker;
window.format.init();

export default rpzChecker;
