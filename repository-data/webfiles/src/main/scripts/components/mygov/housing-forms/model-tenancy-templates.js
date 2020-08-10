// MODEL TENANCY TEMPLATES

'use strict';

const TenancyTemplates = {

    serviceDetails: function(serviceNumber) {
        return '<tr>' +
            '<td>' +
                '<label class="hidden" for="service-' + serviceNumber + '-name">Service</label>' +
                '<input type="text" id="service-' + serviceNumber + '-name" class="grey form-control" placeholder="e.g. Window cleaning">' +
            '</td>' +
            '<td>' +
                '<label class="hidden" for="service-' + serviceNumber + '-value">Value</label>' +
                '<input type="text" id="service-' + serviceNumber + '-value" class="grey form-control" placeholder="e.g. £5 per month">' +
            '</td>' +
        '</tr>';
    },

    subsectionNav: function(){
        return '{{#unless hideSubsectionNav}}' +
        '<nav aria-label="subsections in this section" class="page-group  page-group--right">' +
            '<button type="button" class="page-group__toggle visible-xsmall js-show-page-group-list">{{sectionTitle}}</button>' +

            '<ol class="page-group__list no-margin js-contents">' +
                '{{#pages}}' +
                    '<li class="page-group__item">' +
                        '{{#if excluded}}' +
                            '{{#if current}}' +
                            '<span class="page-group__text page-group__text--excluded">' +
                                '{{title}}' +
                            '</span>' +
                            '{{else}}' +
                            '<a href="#!/{{section}}/{{slug}}/" class="page-group__text page-group__text--excluded">' +
                                '{{title}}' +
                            '</a>' +
                            '{{/if}}' +
                        '{{else}}' +
                            '{{#if current}}' +
                            '<span class="page-group__text page-group__text--no-link">{{title}}</span>' +
                            '{{else}}' +
                            '<a href="#!/{{section}}/{{slug}}/" class="page-group__text">' +
                                '{{title}}' +
                            '</a>' +
                            '{{/if}}' +
                        '{{/if}}' +
                    '</li>' +
                '{{/pages}}' +
            '</ol>' +
        '</nav>' +
        '{{/unless}}';
    }
};

export default TenancyTemplates;
