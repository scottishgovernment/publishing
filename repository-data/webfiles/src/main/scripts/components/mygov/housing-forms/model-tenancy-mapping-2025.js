// MODEL TENANCY MAPPING

'use strict';

const formMapping = {
    // property
    'propertyType': '[name="property-building"]',
    'buildingOther': '#building-other',
    'furnishingType': '[name="property-furnishing"]',
    'propertyAddress': '#property-property-address',
    'hmoProperty': '[name="property-hmo"]',
    'hmo24ContactNumber': '#hmo-contact',
    'hmoRegistrationExpiryDate': '#hmo-expiry',
    'hmoRenewalApplicationSubmitted': '#hmo-renewal',

    // tenancy
    'tenancyStartDate': '#tenancy-start-date',
    'rentAmount': '#tenancy-rent-amount',
    'rentPaymentFrequency': '[name="payment-frequency"]',
    'rentPayableInAdvance': '[name="payment-advance"]',
    'rentPaymentMethod': '[name="payment-method"]',
    'firstPaymentAmount': '#first-payment-amount',
    'firstPaymentDate': '#first-payment-date',
    'firstPaymentPeriodEnd': '#first-payment-end-date',
    'includedAreasOrFacilities': '#facilities-included',
    'sharedFacilities': '#facilities-shared',
    'excludedAreasFacilities': '#facilities-excluded',
    'communicationsAgreement': '[name="communication-agreement"]',
    'depositAmount': '#deposit-amount',
    'tenancyDepositSchemeAdministrator': '[name="deposit-scheme"]',

    // managing the property
    'hasLettingAgent': '[name="letting-agent-query"]',
    'lettingAgent.name': '#letting-agent-name',
    'lettingAgent.registrationNumber': '#letting-agent-registration',
    'lettingAgent.email': '#letting-agent-email',
    'lettingAgent.telephone': '#letting-agent-phone',
    'lettingAgent.address.building': '#letting-agent-address-building',
    'lettingAgent.address.street': '#letting-agent-address-street',
    'lettingAgent.address.town': '#letting-agent-address-town',
    'lettingAgent.address.region': '#letting-agent-address-region',
    'lettingAgent.address.postcode': '#letting-agent-postcode',
    'lettingAgent.services': '#letting-agent-services',
    'lettingAgent.contact': '#letting-agent-contact',

    // recommended terms
    'optionalTerms.contentsAndConditions': '#terms-contents',
    'optionalTerms.alterations': '#terms-alterations',
    'optionalTerms.localAuthorityTaxesAndCharges': '#terms-council-tax',
    'optionalTerms.utilities': '#terms-utilities',
    'optionalTerms.roof': '#terms-roof',
    'optionalTerms.commonParts': '#terms-common-parts',
    'optionalTerms.privateGarden': '#terms-private-garden',
    'optionalTerms.binsAndRecycling': '#terms-bins-recycling',
    'optionalTerms.storage': '#terms-storage',
    'optionalTerms.dangerousSubstances': '#terms-dangerous-substances',
    'optionalTerms.pets': '#terms-pets',
    'optionalTerms.smoking': '#terms-smoking',

    // must include terms editable text
    'mustIncludeTerms.notificationResidents': '#terms-notification',
    'mustIncludeTerms.endingTheTenancy': '#terms-ending-the-tenancy',

    // additional terms
    'additionalTerms[\'additional-term-1\'].title': '#additional-term-1-title',
    'additionalTerms[\'additional-term-1\'].content': '#additional-term-1-content'
};

export default formMapping;
