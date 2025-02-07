(function () {

    const datalayerScriptElement = document.getElementById('gtm-datalayer');

    if (!datalayerScriptElement) {
        return;
    }

    const audience = datalayerScriptElement.dataset.audience;
    const dateCreated = datalayerScriptElement.dataset.datecreated;
    const format = datalayerScriptElement.dataset.format;
    const lastUpdated = datalayerScriptElement.dataset.lastupdated;
    const lifeEvents = datalayerScriptElement.dataset.lifeevents;
    const reportingTags = datalayerScriptElement.dataset.reportingtags;
    const serviceProviders = datalayerScriptElement.dataset.serviceproviders;
    const siteid = datalayerScriptElement.dataset.siteid;
    const topics = datalayerScriptElement.dataset.topics;
    const userType = datalayerScriptElement.dataset.usertype;
    const uuid = datalayerScriptElement.dataset.uuid;

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

    if (present(topics)) {
        obj.topics = topics.split('|');
    }

    if (present(lastUpdated)) {
        obj.lastUpdated = lastUpdated;
    }

    if (present(dateCreated)) {
        obj.dateCreated = dateCreated;
    }

    if (present(uuid)) {
        obj.uuid = uuid;
    }

    window.dataLayer.push(obj);
})();
