// RPZ CHECKER

'use strict';

import MultiPageForm from '../../components/multi-page-form';
import PostcodeLookup from '../../components/postcode-lookup';
import feedback from '../../components/feedback';

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
        // append form template
        const formTemplateContainer = document.querySelector('#form-container');
        if (!formTemplateContainer) {
            return false;
        }
        const overviewContent = formTemplateContainer.innerHTML;
        formTemplateContainer.innerHTML = formTemplate.render();
        formTemplateContainer.querySelector('#overview').innerHTML = overviewContent;

        commonForms.appendCaptchaScript();

        this.form.init();

        feedback.init();
        const rpzComplete = function(){};
        new PostcodeLookup({  // NOSONAR
            rpz: true,
            lookupId: 'rpz-checker-postcode-lookup',
            rpzComplete: rpzComplete,
            displayNotRPZ: true
        });
    }
};

window.format = rpzChecker;
window.format.init();

export default rpzChecker;
