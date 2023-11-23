(function () {

    const datalayerScriptElement = document.getElementById('gtm-datalayer');

    if (!datalayerScriptElement) {
        return;
    }

    const userType = datalayerScriptElement.dataset.usertype;
    const audience = datalayerScriptElement.dataset.audience;
    const reportingTags = datalayerScriptElement.dataset.reportingtags;
    const lifeEvents = datalayerScriptElement.dataset.lifeevents;
    const serviceProviders = datalayerScriptElement.dataset.serviceproviders;
    const format = datalayerScriptElement.dataset.format;
    const siteid = datalayerScriptElement.dataset.siteid;

    window.dataLayer = window.dataLayer || [];

    const obj = {};

    function present(value) {
        return value && !!value.length;
    }

    obj['gtm.whitelist'] = ['google', 'jsm', 'lcl'];

    if (present(userType)) {
        obj.userType = userType;
    }

    if (present(audience)) {
        obj.audience = audience;
    }

    if (present(reportingTags)) {
        obj.reportingTags = reportingTags.split('|');
    }

    if (present(lifeEvents)) {
        obj.lifeEvents = lifeEvents.split('|');
    }

    if (present(serviceProviders)) {
        obj.serviceProviders = serviceProviders.split('|');
    }

    if (present(format)) {
        obj.format = format;
    }

    if (present(siteid)) {
        obj.siteid = siteid;
    }

    window.dataLayer.push(obj);
})();
