'use strict';

const formSections = [
    {
        slug: 'summary',
        title: 'Summary',
        pages: [
            {
                slug: 'summary',
                title: 'Summary'
            }
        ]
    },
    {
        slug: 'property',
        title: 'Property',
        pages: [
            {
                slug: 'property-address',
                title: 'What\'s the property address?'
            },
            {
                slug: 'property-type',
                title: 'What type of property is it?'
            },
            {
                slug: 'property-included',
                title: 'What\'s included?'
            },
            {
                slug: 'property-furnished',
                title: 'Is the property furnished?'
            },
            {
                slug: 'property-hmo',
                title: 'Is the property a HMO?'
            }
        ]
    },
    {
        slug: 'tenancy',
        title: 'Tenancy',
        pages: [
            {
                slug: 'tenancy-start',
                title: 'Tenancy start date'
            },
            {
                slug: 'tenancy-payment',
                title: 'Payment details'
            },
            {
                slug: 'tenancy-first-payment',
                title: 'First payment'
            },
            {
                slug: 'tenancy-deposit',
                title: 'Deposit'
            },
            {
                slug: 'tenancy-communication-agreement',
                title: 'Communication agreement'
            }
        ]
    },
    {
        slug: 'managing-the-property',
        title: 'Managing the property',
        pages: [
            {
                slug: 'letting-agent',
                title: 'Letting agent'
            }
        ]
    },
    {
        slug: 'tenants',
        title: 'Tenants',
        pages: [
        ]
    },
    {
        slug: 'end-of-part-1',
        title: 'Summary',
        pages: [
            {
                slug: 'part-1-summary',
                title: 'Summary',
                triggerEvent: 'updateSummary1'
            }
        ]
    },
    {
        slug: 'must-include-terms',
        title: 'Must-include terms',
        pages: [
            {
                slug: 'must-include-terms-list',
                title: 'Must-include terms',
                triggerEvent: 'updateMandatoryTerms'
            },
            {
                slug: 'notification-residents',
                title: 'Notification about other residents'
            },
            {
                slug: 'ending-the-tenancy',
                title: 'Ending the tenancy'
            }
        ]
    },
    {
        slug: 'extra-terms',
        title: 'Extra terms',
        pages: [
            {
                slug: 'extra-terms-overview',
                title: 'Extra terms'
            },
            {
                slug: 'contents-and-condition',
                title: 'Contents and condition'
            },
            {
                slug: 'local-authority-taxes',
                title: 'Local authority taxes/charges'
            },
            {
                slug: 'utilities',
                title: 'Utilities'
            },
            {
                slug: 'alterations',
                title: 'Alterations'
            },
            {
                slug: 'common-parts',
                title: 'Common parts'
            },
            {
                slug: 'private-garden',
                title: 'Private garden'
            },
            {
                slug: 'roof',
                title: 'Roof'
            },
            {
                slug: 'bins-and-recycling',
                title: 'Bins and recycling'
            },
            {
                slug: 'storage',
                title: 'Storage'
            },
            {
                slug: 'dangerous-substances',
                title: 'Dangerous substances'
            },
            {
                slug: 'pets',
                title: 'Pets'
            },
            {
                slug: 'smoking',
                title: 'Smoking'
            }
        ]
    },
    {
        slug: 'additional-terms',
        title: 'Additional terms',
        pages: []
    },
    {
        slug: 'end-of-part-2',
        title: 'Summary',
        pages: [
            {
                slug: 'part-2-summary',
                title: 'Summary',
                triggerEvent: 'updateSummary2'
            }
        ]
    },
    {
        slug: 'download',
        title: 'Download',
        pages: [
            {
                slug: 'tenancy-download',
                title: 'Download'
            }
        ]
    }
];

export default formSections;
