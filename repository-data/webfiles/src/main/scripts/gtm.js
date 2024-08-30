import setInitialCookiePermissions from './tools/set-initial-cookie-permissions';

(function () {
    setInitialCookiePermissions();

    window.initGTM = function () {
        const gtmScriptElement = document.getElementById('gtm-script');
        const containerId = gtmScriptElement.dataset.containerid;
        const auth = gtmScriptElement.dataset.auth;
        const env = gtmScriptElement.dataset.env;

        let authString = '';
        let envString = '';

        if (auth && !!auth.length) {
            authString = `&gtm_auth=${auth}`;
        }

        if (env && !!env.length) {
            envString = `&gtm_preview=${env}&gtm_cookies_win=x`;
        }

        function getCookie(name) {
            const cookie = {};
            document.cookie.split(';').forEach(function(el) {
                const [k,v] = el.split('=');
                cookie[k.trim()] = v;
            });
            return cookie[name];
        }

        const cookiePermissions = getCookie('cookiePermissions');

        let statisticsEnabled = false;

        if (cookiePermissions) {
            try {
                statisticsEnabled = JSON.parse(atob(cookiePermissions)).statistics !== false;
            } catch (err) {
                statisticsEnabled = false;
            }
        }

        if (statisticsEnabled) {
            (function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
                    new Date().getTime(),event:'gtm.js'});let f=d.getElementsByTagName(s)[0],
                    j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
                    'https://www.googletagmanager.com/gtm.js?id='+i+dl+authString+envString;f.parentNode.insertBefore(j,f);
            })(window,document,'script','dataLayer',containerId);
        }
    };

    window.initGTM();
})();
