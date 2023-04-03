(function () {

    const datalayerScriptElement = document.getElementById('gtm-datalayer');
    const userType = datalayerScriptElement.dataset.usertype;
    const audience = datalayerScriptElement.dataset.audience;
    const reportingTags = datalayerScriptElement.dataset.reportingtags;
    const lifeEvents = datalayerScriptElement.dataset.lifeevents;
    const serviceProviders = datalayerScriptElement.dataset.serviceproviders;
    const format = datalayerScriptElement.dataset.format;
    const siteid = datalayerScriptElement.dataset.siteid;

    window.dataLayer = window.dataLayer || [];

    const obj = {};

    obj['gtm.whitelist'] = ['google', 'jsm', 'lcl'];

    if (userType && !!userType.length) {
        obj.userType = userType;
    }

    if (audience && !!audience.length) {
        obj.audience = audience;
    }

    if (reportingTags && !!reportingTags.length) {
        obj.reportingTags = reportingTags.split('|');
    }

    if (lifeEvents && !!lifeEvents.length) {
        obj.lifeEvents = lifeEvents.split('|');
    }

    if (serviceProviders && !!serviceProviders.length) {
        obj.serviceProviders = serviceProviders.split('|');
    }

    if (format && !!format.length) {
        obj.format = format;
    }

    if (siteid && !!siteid.length) {
        obj.siteid = siteid;
    }

    window.dataLayer.push(obj);
})();
