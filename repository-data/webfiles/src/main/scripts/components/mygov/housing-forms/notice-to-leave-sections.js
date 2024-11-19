// NOTICE TO LEAVE SECTIONS

'use strict';

/*
section options
* slug: slug of the section used in the URL and script (required)
* title: title of the section presented to the user (required)
* group: object, the group this section belongs to
* pages: array, pages in this section

page options
* slug: slug of the page used in the URL and script (required)
* title: title of the page presented to the user (required)
* weight: currently used only to force a page to the end of a section when repeating pages are added (i.e. landlords)
*/

const formSections = [
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'overview',
        title: 'Overview',
        pages: [
            {
                slug: 'overview',
                title: 'Overview'
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'address',
        title: 'Address',
        pages: [
            {
                slug: 'address',
                title: 'Address'
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'landlords',
        title: 'Landlord',
        pages: [
            {
                slug: 'letting-agent',
                title: 'Letting agent'
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'tenants',
        title: 'Tenants',
        pages: [
            {
                slug: 'tenants',
                title: 'Tenants'
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'subtenants',
        title: 'Subtenants',
        pages: [
            {
                slug: 'subtenants',
                title: 'Subtenants'
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'end-date',
        title: 'End date',
        pages: [
            {
                slug: 'grounds',
                title: 'Grounds'
            },
            {
                slug: 'details-and-evidence',
                title: 'Details and evidence'
            },
            {
                slug: 'notice-period',
                title: 'Notice period'
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'summary',
        title: 'Summary',
        pages: [
            {
                slug: 'summary',
                title: 'Summary',
                triggerEvent: 'updateSummary'
            }
        ]
    },
    {
        group: {
            slug: 'part-0',
            title: 'Part zero'
        },
        slug: 'download',
        title: 'Download',
        pages: [
            {
                slug: 'download',
                title: 'Download'
            }
        ]
    }
];

export default formSections;
