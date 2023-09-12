export default function() {
    // remove cookies
    // this is ugly
    const cookiesAsObject = document.cookie.split('; ').reduce((prev, current) => {
        const [name, ...value] = current.split('=');
        prev[name] = value.join('=');
        return prev;
    }, {});

    if (!storage.hasPermission(storage.categories.statistics)) {
        const statisticsCookiePrefixes = ['_ga', '_gid', '_ga_'];
        for (const cookie in cookiesAsObject) {
            if (statisticsCookiePrefixes.includes(cookie.substring(0,4))) {
                storage.cookie.remove(cookie);
            }
        }
    }

    if (!storage.hasPermission(storage.categories.preferences)) {
        const preferencesCookiePrefixes = ['banner-'];
        for (const cookie in cookiesAsObject) {
            if (preferencesCookiePrefixes.includes(cookie.substring(0,8))) {
                storage.cookie.remove(cookie);
            }
        }
    }
}
