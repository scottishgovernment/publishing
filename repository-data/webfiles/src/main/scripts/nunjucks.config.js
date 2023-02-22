module.exports = function (env) {

    /**
     * Return the sum of two numbers
     *
     * @param {number} a
     * @param {number} b
     */
    env.addFilter('add', function (a, b) {
        return a + b;
    });

    env.addFilter('compare', function (a, b, options) {
        if (a === b) {
            return options.fn(this);
        } else {
            return options.inverse(this);
        }
    });

    env.addFilter('concat', function (a, b) {
        return a.toString() + b.toString();
    });

    env.addFilter('currency', function (number) {
        if (!number || isNaN(number)) {
            return null;
        }
        return '£' + (number * 1).toFixed(2);
    });

    env.addFilter('currencyFormatted', function (number) {
        const baseParts = Number(number).toFixed(2).split('.');
        const commaGroupedUnits = baseParts[0].split('').reverse().join('').match(/.{1,3}/g).join(',').split('').reverse().join('');
        return `£${commaGroupedUnits}.${baseParts[1]}`;
    });

    env.addFilter('getDisplayName', function (valueArray, value) {
        for (var i = 0, il = valueArray.length; i < il; i++) {
            if (valueArray[i].value.toString() === value.toString()) {
                return valueArray[i].displayName;
            }
        }
        return '';
    });

    env.addFilter('incrementslug', function(slug) {
        return slug.replace(/(\d+)$/, function (match, $1){return parseInt($1, 10) + 1;});
    });

    env.addFilter('match', function (a, b, options) {
        if (a.match(b)) {
            return options.fn(this);
        } else {
            return options.inverse(this);
        }
    });

    env.addFilter('notspecified', function (value) {
        if (value) {
            return value;
        } else {
            return '<span class="summary__blank" title="not specified"></span>';
        }
    });

    env.addFilter('paragraphify', function (input) {
        if (!input) {
            return;
        }

        var paras = input.split('\n\n');

        var html = '';

        for (var i = 0, il = paras.length; i < il; i++) {
            html += '<p>' + paras[i] + '</p>';
        }

        return html;
    });

    env.addFilter('decimalToPercent', function (value) {
        return `${(value * 100).toFixed(2)}%`;
    });

    // env.addExtension(...) etc
    env.addExtension('zcompare', function () {

    });
}
