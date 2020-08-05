/* global require __dirname module */

const path = require('path');

const commonItems = {
    mode: 'development',

    entry: {
        'global':                       path.resolve(__dirname, './scripts/mygov/global.js'),

        // format-specific entry points
        'accordion':                    path.resolve(__dirname, './scripts/mygov/format.accordion.js'),
        'article':                      path.resolve(__dirname, './scripts/mygov/format.article.js'),
        'business-rates-calculator':    path.resolve(__dirname, './scripts/mygov/format.business-rates-calculator.js'),
        'categories':                   path.resolve(__dirname, './scripts/mygov/format.categories.js'),
        'default':                      path.resolve(__dirname, './scripts/mygov/format.default.js'),
        'corporate-org-hub':            path.resolve(__dirname, './scripts/mygov/format.corporate-org-hub.js'),
        'document-landing':             path.resolve(__dirname, './scripts/mygov/format.document-landing.js'),
        'fair-rent-register':           path.resolve(__dirname, './scripts/mygov/format.fair-rent-register.js'),
        'fair-rent-register-result':    path.resolve(__dirname, './scripts/mygov/format.fair-rent-register-result.js'),
        'guide':                        path.resolve(__dirname, './scripts/mygov/format.guide.js'),
        'home':                         path.resolve(__dirname, './scripts/mygov/format.home.js'),
        'model-tenancy-form':           path.resolve(__dirname, './scripts/mygov/format.model-tenancy-form.js'),
        'non-provision-form':           path.resolve(__dirname, './scripts/mygov/format.non-provision-form.js'),
        'notice-to-leave-form':         path.resolve(__dirname, './scripts/mygov/format.notice-to-leave-form.js'),
        'org-hub':                      path.resolve(__dirname, './scripts/mygov/format.org-hub.js'),
        'org-list':                     path.resolve(__dirname, './scripts/mygov/format.org-list.js'),
        'rent-adjudication-form':       path.resolve(__dirname, './scripts/mygov/format.rent-adjudication-form.js'),
        'rent-improvements-form':       path.resolve(__dirname, './scripts/mygov/format.rent-improvements-form.js'),
        'rent-increase-form':           path.resolve(__dirname, './scripts/mygov/format.rent-increase-form.js'),
        'rpz-checker':                  path.resolve(__dirname, './scripts/mygov/format.rpz-checker.js'),
        //'search':                     path.resolve(__dirname, './scripts/mygov/format.search.js'),
        'signpost':                     path.resolve(__dirname, './scripts/mygov/format.signpost.js'),
        'site-item':                    path.resolve(__dirname, './scripts/mygov/format.site-item.js'),
        'status':                       path.resolve(__dirname, './scripts/mygov/format.status.js'),
        'suspicious-activity-form':     path.resolve(__dirname, './scripts/mygov/format.suspicious-activity-form.js'),

        // for the cookie form markdown helper
        'cookie-form':                  path.resolve(__dirname, './scripts/mygov/component.cookie-form.js'),
    },

    externals: {
        jquery: 'jQuery'
    },

    output: {
        path: path.resolve(__dirname, '../resources/site/assets/mygov/scripts/'),
        filename: '[name].js'
    },

    resolve: {
        // equivalent to requirejs baseUrl
        modules: [
            './app/assets/scripts',
            'node_modules'
        ],

        extensions: ['.js', '.hbs'],

        // equivalent to requirejs paths
        alias: {
            'hbs': '../../../../../node_modules/handlebars/dist',
            'jquery': '../vendor/jquery/dist/jquery',
            'tinydash': '../vendor/lodash/dist/tinydash.es6',
            'autocomplete': '../vendor/jquery.auto-complete.min',
            'grecaptcha': '../vendor/recaptcha/api',
            'textchange': '../vendor/jquery.splendid.textchange'
        }
    },

    module: {
        rules: [
            {
                test: /\.hbs/,
                use: [{
                    loader: 'handlebars-loader',
                    options: {
                        helperDirs: [path.join(__dirname, 'src/scripts/templates/helpers/')]
                    }
                }]
            }
        ]
    }
};

module.exports = [{
        mode: commonItems.mode,
        entry: commonItems.entry,
        externals: commonItems.externals,
        resolve: commonItems.resolve,
        module: commonItems.module,
        output: {
                path: path.resolve(__dirname, '../resources/site/assets/mygov/scripts/'),
                filename: '[name].js'
        }
    }, {
        mode: commonItems.mode,
        entry: commonItems.entry,
        externals: commonItems.externals,
        resolve: commonItems.resolve,
        module: {
            rules: [
                commonItems.module.rules[0],
                {
                    test: /\.js$/,
                    use: {
                        loader: 'babel-loader',
                        options: {
                            presets: ['@babel/preset-env']
                        }
                    }
                }
            ]
        },
        output: {
            path: path.resolve(__dirname, '../resources/site/assets/mygov/scripts/'),
            filename: '[name].es5.js'
        }
    }
];
