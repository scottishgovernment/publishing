// RENT IMPROVEMENT SECTIONS

'use strict';

/*
section options
* slug: slug of the section used in the URL and script (required)
* title: title of the section presented to the user (required)
* hideFromSectionNav: bool, don't include this section in the main nav
* group: object, the group this section belongs to
* pages: array, pages in this section

page options
* slug: slug of the page used in the URL and script (required)
* title: title of the page presented to the user (required)
* hideSubsectionNav: bool, hides sidebar navigation for this page
* hideSectionNav: bool, hides top navigation for this page
* noFormBox: bool, don't show the grey background
* weight: currently used only to force a page to the end of a section when repeating pages are added (i.e. landlords)
*/

const formSections = [
    {
        group: {
            slug: 'part-0',
            title: 'Part zero',
            hideFromSectionNav: true
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
                hideSubsectionNav: true,
                noFormBox: true,
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
                hideSubsectionNav: true,
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
                hideSubsectionNav: true,
                noFormBox: true,
                triggerEvent: 'checkDocuments'
            }
        ]
    }
];

export default formSections;
