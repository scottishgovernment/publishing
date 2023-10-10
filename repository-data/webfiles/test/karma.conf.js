'use strict';

const path = require('path');
const webpackConfig = require('../src/main/scripts/webpack.config')[0];
const coverageDir = './test/coverage';

const instrumentationConfig = {
    test: /\.js/,
    exclude: [
        path.resolve(__dirname, "../src/main/scripts/vendor"),
        path.resolve(__dirname, "../node_modules"),
        path.resolve(__dirname, '')
    ],
    enforce: 'post',
    use: {
        loader: 'istanbul-instrumenter-loader',
        options: {
            esModules: true
        }
    }
};

webpackConfig.mode = 'development';
webpackConfig.module.rules.push(instrumentationConfig);

module.exports = function (config) {
    config.set({
        basePath: '../',
        frameworks: [
            'jquery-1.8.3',
            'jasmine-jquery',
            'jasmine',
            'viewport'
        ],
        reporters: [
            'dots',
            'coverage-istanbul'
        ],
        port: 9876,
        colors: true,
        logLevel: config.LOG_ERROR,
        autoWatch: true,
        browsers: ['ChromeHeadless'],
        singleRun: true,

        files: [
            'node_modules/@scottish-government/design-system/src/all.js',
            'test/specs/**/*.js',
            'test/fixtures/*.html',
        ],

        preprocessors: {
            'node_modules/@scottish-government/design-system/src/all.js': ['babel', 'webpack'],
            '../src/main/**/*.js': ['babel', 'webpack'],
            'test/specs/**/**.js': ['webpack']
        },

        coverageIstanbulReporter: {
            reports: [ 'html', 'text-summary', 'lcov', 'json' ],
            dir: coverageDir,
            fixWebpackSourcePaths: true,
            'report-config': {
                html: { outdir: 'html' },
            }
        },

        webpack: webpackConfig
    });
};
