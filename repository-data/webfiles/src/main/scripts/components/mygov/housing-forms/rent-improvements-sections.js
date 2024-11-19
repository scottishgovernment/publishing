// RENT IMPROVEMENT SECTIONS

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
            slug: 'details',
            title: 'Details',
            iconLeft: true
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
            slug: 'details',
            title: 'Details',
            iconLeft: true
        },
        slug: 'landlords',
        title: 'Landlord',
        pages: [
            {
                slug: 'letting-agent',
                title: 'Letting agent',
                weight: 9999
            }
        ]
    },
    {
        group: {
            slug: 'details',
            title: 'Details',
            iconLeft: true
        },
        slug: 'tenants',
        title: 'Tenants',
        pages: [
        ]
    },
    {
        group: {
            slug: 'details',
            title: 'Details',
            iconLeft: true
        },
        slug: 'improvements',
        title: 'Improvements',
        pages: [
            {
                slug: 'property-improvements',
                title: 'Improvements details'
            },
            {
                slug: 'proof-of-work',
                title: 'Proof of work'
            }
        ]
    },
    {
        group: {
            slug: 'details',
            title: 'Details',
            iconLeft: true
        },
        slug: 'rent-increase',
        title: 'Increase',
        pages: [
            {
                slug: 'rent-increase',
                title: 'Rent increase'
            }
        ]
    },
    {
        group: {
            slug: 'details',
            title: 'Details',
            iconLeft: true
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
            slug: 'download',
            title: 'Download',
            hideFromGroupNav: true
        },
        slug: 'declaration',
        title: 'Declaration',
        pages: [
            {
                slug: 'declaration',
                title: 'Declaration',
                triggerEvent: 'insertPropertyAddress'
            }
        ]
    },
    {
        group: {
            slug: 'download',
            title: 'Download',
            hideFromGroupNav: true
        },
        slug: 'download',
        title: 'Download',
        pages: [
            {
                slug: 'form-download',
                title: 'Download',
                triggerEvent: 'checkDocuments'
            }
        ]
    }
];

export default formSections;
