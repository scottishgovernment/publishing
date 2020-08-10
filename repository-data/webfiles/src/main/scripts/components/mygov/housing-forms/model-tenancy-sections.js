'use strict';

const formSections = [
    {
        group: {
            slug: 'part-0',
            title: 'Part zero',
            hideFromSectionNav: true
        },
        hideFromSectionNav: true,
        slug: 'summary',
        title: 'Summary',
        pages: [
            {
                slug: 'summary',
                title: 'Summary',
                hideSubsectionNav: true,
                hideSectionNav: true,
                noFormBox: true
            }
        ]
    },
    {
        group: {
            slug: 'part-1',
            title: 'The details',
            iconLeft: true
        },
        slug: 'property',
        title: 'Property',
        pages: [
            {
                slug: 'property-details',
                title: 'Property details'
            },
            {
                slug: 'property-furnishings',
                title: 'Furnishings'
            },
            {
                slug: 'property-hmo',
                title: 'HMO'
            }
        ]
    },
    {
        group: {
            slug: 'part-1',
            title: 'The details',
            iconLeft: true
        },
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
        group: {
            slug: 'part-1',
            title: 'The details',
            iconLeft: true
        },
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
        group: {
            slug: 'part-1',
            title: 'The details',
            iconLeft: true
        },
        slug: 'tenants',
        title: 'Tenants',
        pages: [
        ]
    },
    {
        group: {
            slug: 'part-1',
            title: 'The details',
            iconLeft: true
        },
        slug: 'end-of-part-1',
        title: 'Summary',
        pages: [
            {
                slug: 'part-1-summary',
                title: 'Summary',
                hideSubsectionNav: true,
                noFormBox: true,
                triggerEvent: 'updateSummary1'
            }
        ]
    },
    {
        group: {
            slug: 'part-2',
            title: 'The terms'
        },
        slug: 'must-include-terms',
        title: 'Must-include terms',
        pages: [
            {
                slug: 'must-include-terms-list',
                title: 'Must-include terms',
                noFormBox: true,
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
        group: {
            slug: 'part-2',
            title: 'The terms'
        },
        slug: 'extra-terms',
        title: 'Extra terms',
        pages: [
            {
                slug: 'extra-terms-overview',
                title: 'Extra terms',
                noFormBox: true
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
        group: {
            slug: 'part-2',
            title: 'The terms'
        },
        slug: 'additional-terms',
        title: 'Additional terms',
        pages: []
    },
    {
        group: {
            slug: 'part-2',
            title: 'The Terms'
        },
        slug: 'end-of-part-2',
        title: 'Summary',
        pages: [
            {
                slug: 'part-2-summary',
                title: 'Summary',
                hideSubsectionNav: true,
                noFormBox: true,
                triggerEvent: 'updateSummary2'
            }
        ]
    },
    {
        group: {
            slug: 'part-2',
            title: 'The terms'
        },
        slug: 'download',
        title: 'Download',
        pages: [
            {
                slug: 'tenancy-download',
                title: 'Download',
                hideSubsectionNav: true,
                noFormBox: true
            }
        ]
    }
];

export default formSections;
