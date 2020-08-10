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
    'rentPaymentFrequency': '#tenancy-payment-frequency',
    'rentPayableInAdvance': '[name="payment-advance"]',
    'rentPaymentMethod': '#tenancy-payment-method',
    'firstPaymentAmount': '#first-payment-amount',
    'firstPaymentDate': '#first-payment-date',
    'firstPaymentPeriodEnd': '#first-payment-end-date',
    'includedAreasOrFacilities': '#facilities-included',
    'sharedFacilities': '#facilities-shared',
    'excludedAreasFacilities': '#facilities-excluded',
    'communicationsAgreement': '[name="communication-agreement"]',
    'depositAmount': '#deposit-amount',
    'tenancyDepositSchemeAdministrator': '#deposit-scheme',

    // managing the property
    'landlords[\'landlord-1\'].name': '#landlord-1-name',
    'landlords[\'landlord-1\'].registrationNumber': '#landlord-1-registration',
    'landlords[\'landlord-1\'].email': '#landlord-1-email',
    'landlords[\'landlord-1\'].telephone': '#landlord-1-phone',
    'landlords[\'landlord-1\'].address.building': '#landlord-1-address-building',
    'landlords[\'landlord-1\'].address.street': '#landlord-1-address-street',
    'landlords[\'landlord-1\'].address.town': '#landlord-1-address-town',
    'landlords[\'landlord-1\'].address.region': '#landlord-1-address-region',
    'landlords[\'landlord-1\'].address.postcode': '#landlord-1-postcode',

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

    // tenants
    'tenants[\'tenant-1\'].name': '#tenant-1-name',
    'tenants[\'tenant-1\'].email': '#tenant-1-email',
    'tenants[\'tenant-1\'].telephone': '#tenant-1-phone',
    'tenants[\'tenant-1\'].address.building': '#tenant-1-address-building',
    'tenants[\'tenant-1\'].address.street': '#tenant-1-address-street',
    'tenants[\'tenant-1\'].address.town': '#tenant-1-address-town',
    'tenants[\'tenant-1\'].address.region': '#tenant-1-address-region',
    'tenants[\'tenant-1\'].address.postcode': '#tenant-1-postcode',

    'tenants[\'tenant-1\'].hasGuarantor': '[name="guarantor-1-query"]',
    'tenants[\'tenant-1\'].guarantor.name': '#guarantor-1-name',
    'tenants[\'tenant-1\'].guarantor.address.building': '#guarantor-1-address-building',
    'tenants[\'tenant-1\'].guarantor.address.street': '#guarantor-1-address-street',
    'tenants[\'tenant-1\'].guarantor.address.town': '#guarantor-1-address-town',
    'tenants[\'tenant-1\'].guarantor.address.region': '#guarantor-1-address-region',
    'tenants[\'tenant-1\'].guarantor.address.postcode': '#guarantor-1-postcode',

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
