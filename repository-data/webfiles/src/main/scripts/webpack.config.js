/* global require __dirname module */

const path = require('path');

const commonItems = {
    mode: 'development',

    entry: {
        'global':                       path.resolve(__dirname, './global.js'),

        // format-specific entry points
        'accordion':                    path.resolve(__dirname, './formats/accordion.js'),
        'article':                      path.resolve(__dirname, './formats/article.js'),
        'category':                     path.resolve(__dirname, './formats/category.js'),
        'default':                      path.resolve(__dirname, './formats/default-format.js'),
        'document-landing':             path.resolve(__dirname, './formats/document-landing.js'),
        'guide':                        path.resolve(__dirname, './formats/guide.js'),
        'home':                         path.resolve(__dirname, './formats/home.js'),
        'organisation':                 path.resolve(__dirname, './formats/organisation.js'),
        'organisation-corporate':       path.resolve(__dirname, './formats/organisation-corporate.js'),
        'organisation-list':            path.resolve(__dirname, './formats/organisation-list.js'),
        //'search':                     path.resolve(__dirname, './formats/search.js'),
        'signpost':                     path.resolve(__dirname, './formats/signpost.js'),
        'site-item':                    path.resolve(__dirname, './formats/site-item.js'),
        'status':                       path.resolve(__dirname, './formats/status.js'),

        // mygov-specific formats
        'business-rates-calculator':    path.resolve(__dirname, './formats/mygov/business-rates-calculator.js'),
        'fair-rent-register':           path.resolve(__dirname, './formats/mygov/fair-rent-register.js'),
        'fair-rent-register-result':    path.resolve(__dirname, './formats/mygov/fair-rent-register-result.js'),
        'model-tenancy-form':           path.resolve(__dirname, './formats/mygov/model-tenancy-form.js'),
        'non-provision-form':           path.resolve(__dirname, './formats/mygov/non-provision-form.js'),
        'notice-to-leave-form':         path.resolve(__dirname, './formats/mygov/notice-to-leave-form.js'),
        'rent-adjudication-form':       path.resolve(__dirname, './formats/mygov/rent-adjudication-form.js'),
        'rent-improvements-form':       path.resolve(__dirname, './formats/mygov/rent-improvements-form.js'),
        'rent-increase-form':           path.resolve(__dirname, './formats/mygov/rent-increase-form.js'),
        'rpz-checker':                  path.resolve(__dirname, './formats/mygov/rpz-checker.js'),
    },

    externals: {
        jquery: 'jQuery'
    },

    resolve: {
        // equivalent to requirejs baseUrl
        modules: [
            './app/assets/scripts',
            'node_modules'
        ],

        extensions: ['.js', '.hbs'],
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
                path: path.resolve(__dirname, '../resources/site/assets/scripts/'),
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
            path: path.resolve(__dirname, '../resources/site/assets/scripts/'),
            filename: '[name].es5.js'
        }
    }
];
