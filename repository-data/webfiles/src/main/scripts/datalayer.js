(function () {

    const datalayerScriptElement = document.getElementById('gtm-datalayer');
    const userType = datalayerScriptElement.dataset.usertype;
    const audience = datalayerScriptElement.dataset.audience;
    const reportingTags = datalayerScriptElement.dataset.reportingtags;
    const lifeEvents = datalayerScriptElement.dataset.lifeevents;
    const serviceProviders = datalayerScriptElement.dataset.serviceproviders;
    const gtmName = datalayerScriptElement.dataset.gtmname;
    const gtmId = datalayerScriptElement.dataset.gtmid;

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

    if (gtmName && !!gtmName.length) {
        obj.gtmName = gtmName;
    }

    if (gtmId && !!gtmId.length) {
        obj.gtmId = gtmId;
    }

    window.dataLayer.push(obj);
})();
