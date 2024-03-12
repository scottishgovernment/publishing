'use strict';

const webpackConfig = require('../src/main/scripts/webpack.config')[0];

webpackConfig.mode = 'development';

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
            'dots'
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

        webpack: webpackConfig
    });
};
