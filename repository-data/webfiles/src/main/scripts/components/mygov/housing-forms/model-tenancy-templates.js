// MODEL TENANCY TEMPLATES

'use strict';

const TenancyTemplates = {

    serviceDetails: function(serviceNumber) {
        return '<tr>' +
            '<td>' +
                '<label for="service-' + serviceNumber + '-name">Service</label>' +
                '<input type="text" id="service-' + serviceNumber + '-name" class="grey form-control" placeholder="e.g. Window cleaning">' +
            '</td>' +
            '<td>' +
                '<label for="service-' + serviceNumber + '-value">Value</label>' +
                '<input type="text" id="service-' + serviceNumber + '-value" class="grey form-control" placeholder="e.g. £5 per month">' +
            '</td>' +
        '</tr>';
    }
};

export default TenancyTemplates;
