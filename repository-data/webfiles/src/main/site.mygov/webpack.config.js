const path = require('path');

module.exports = [{
  mode: 'development',

  entry: {
    'site': [
      path.resolve(__dirname, './scripts/cookies.js'),
      path.resolve(__dirname, './scripts/usertype.js')
    ],
    'pattern-library': [
      path.resolve(__dirname, '../../../node_modules/@scottish-government/pattern-library/src/components/accordion/accordion.js'),
      path.resolve(__dirname, '../../../node_modules/@scottish-government/pattern-library/src/components/notification-banner/notification-banner.js'),
      path.resolve(__dirname, '../../../node_modules/@scottish-government/pattern-library/src/components/side-navigation/side-navigation.js'),
      path.resolve(__dirname, '../../../node_modules/@scottish-government/pattern-library/src/components/site-navigation/site-navigation.js'),
      path.resolve(__dirname, '../../../node_modules/@scottish-government/pattern-library/src/components/site-search/site-search.js')
    ]
  },

  output: {
    path: path.resolve(__dirname, '../resources/site/assets/mygov/scripts/'),
    filename: '[name].js'
  }
}, {
  mode: 'development',

  entry: {
    'site': [
      path.resolve(__dirname, './scripts/cookies.js'),
      path.resolve(__dirname, './scripts/usertype.js')
    ],
    'pattern-library': [
      path.resolve(__dirname, '../../../node_modules/@scottish-government/pattern-library/src/components/accordion/accordion.js'),
      path.resolve(__dirname, '../../../node_modules/@scottish-government/pattern-library/src/components/notification-banner/notification-banner.js'),
      path.resolve(__dirname, '../../../node_modules/@scottish-government/pattern-library/src/components/side-navigation/side-navigation.js'),
      path.resolve(__dirname, '../../../node_modules/@scottish-government/pattern-library/src/components/site-navigation/site-navigation.js'),
      path.resolve(__dirname, '../../../node_modules/@scottish-government/pattern-library/src/components/site-search/site-search.js')
    ]
  },

  output: {
    path: path.resolve(__dirname, '../resources/site/assets/mygov/scripts/'),
    filename: '[name].es5.js'
  },

  module: {
    rules: [
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
  }
}];
