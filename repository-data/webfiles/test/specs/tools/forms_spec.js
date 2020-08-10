'use strict';

/* global describe beforeEach it expect spyOn */

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import commonForms from '../../../src/main/scripts/tools/forms';
import $ from '../../../src/main/scripts/vendor/jquery/dist/jquery.min';

describe('Form validation functions:', function() {

    xdescribe ('reCAPTCHA', function () {
        beforeEach(function () {
            window.body = '';
            loadFixtures('form-fields.html');
        });

        it ('should display an error message when a recaptcha is failed', function () {
            window.grecaptcha = {
                getResponse: function () {
                    return '';
                }
            };

            window.checkRecaptcha();

            expect($('.recaptcha-errors .recaptcha').length).toEqual(1);
        });

        it ('should not add multiple duplicate error messages', function () {
            // add a fake error
            $('.form-errors').append('<ul class="recaptcha-errors"></ul>');
            $('.recaptcha-errors').append('<li class="error recaptcha"><strong>reCAPTCHA</strong> <br />This field is required</li>');

            window.grecaptcha = {
                getResponse: function () {
                    return '';
                }
            };

            window.checkRecaptcha();

            expect($('.recaptcha-errors .recaptcha').length).toEqual(1);
        });

        it ('should remove an error message when a recaptcha is successful', function () {
            // add a fake error
            $('.form-errors').append('<ul class="recaptcha-errors"></ul>');
            $('.recaptcha-errors').append('<li class="error recaptcha"><strong>reCAPTCHA</strong> <br />This field is required</li>');

            window.grecaptcha = {
                getResponse: function () {
                    return 'success!';
                }
            };

            window.checkRecaptcha();

            expect($('.recaptcha-errors .recaptcha').length).toEqual(0);
        });

        xit ('should enable associated form elements on completion', function () {
            window.grecaptcha = {
                getResponse: function () {
                    return 'success!';
                }
            };

            window.checkRecaptcha();

            expect ($('.js-document-container').find('.js-download-file').attr('disabled')).toBeUndefined();
        });

        it ('should disable associated form elements on expiration', function () {
            $('.js-document-container').find('.js-download-file').removeAttr('disabled');

            window.grecaptcha = {
                getResponse: function () {
                    return 'success!';
                },
                reset: function () {

                }
            };

            window.expireRecaptcha();

            expect($('.js-document-container').find('.js-download-file').attr('disabled')).toEqual('disabled');

        });

        it ('checkRecaptcha should be on the window object', function () {
            commonForms.setupRecaptcha();

            expect(window.checkRecaptcha).toBeDefined();
        });

        it ('expireRecaptcha should be on the window object', function () {
            commonForms.setupRecaptcha();

            expect(window.expireRecaptcha).toBeDefined();
        });
    });

    describe ('Postcode Validation', function () {
        it ( 'should be registered', function() {
            expect( commonForms.validPostcode ).toBeDefined();
        });

        it ('should recognise a valid postcode', function () {
            var input = $('<input type="text" value="EH6 6QQ">');
            var output = commonForms.validPostcode(input);
            expect(output).toEqual(true);
        });

        it ('should recognise a postcode with no spaces as valid', function () {
            var input = $('<input type="text" value="EH66QQ">');
            var output = commonForms.validPostcode(input);

            expect(output).toEqual(true);
        });

        it ('should recognise a lowercase postcode as valid', function () {
            var input = $('<input type="text" value="eh66qq">');
            var output = commonForms.validPostcode(input);

            expect(output).toEqual(true);
        });

        it ('should recognise an empty field as valid', function () {
            var input = $('<input type="text" value="">');
            var output = commonForms.validPostcode(input);

            expect(output).toEqual(true);
        });

        it ('should recognise invalid input', function () {
            var input = $('<input type="text" value="abcdefg">');
            var output = commonForms.validPostcode(input);

            expect(output).toEqual(false);
        });
    });

    describe ('Required Radio Validation', function () {
        it ( 'should be registered', function() {
            expect( commonForms.requiredRadio ).toBeDefined();
        });

        it ('should recognise a fieldset with a checked radio button as valid', function () {
            var input = $('<fieldset><input type="radio" checked><input type="radio"></fieldset>');
            var output = commonForms.requiredRadio(input);

            expect(output).toEqual(true);
        });

        it ('should recognise a fieldset with no checked radio buttons as invalid', function () {
            var input = $('<fieldset><input type="radio"><input type="radio"></fieldset>');
            var output = commonForms.requiredRadio(input);

            expect(output).toEqual(false);
        });
    });

    describe ('Phone Number Validation', function () {
        it ( 'should be registered', function() {
            expect( commonForms.validPhone ).toBeDefined();
        });

        it ('should recognise a valid phone number', function () {
            var input = $('<input type="text" value="+44 131 123 456">');
            var output = commonForms.validPhone(input);

            expect(output).toEqual(true);
        });

        it ('should recognise an empty field as valid', function () {
            var input = $('<input type="text" value="">');
            var output = commonForms.validPhone(input);

            expect(output).toEqual(true);
        });

        it ('should recognise disallowed characters as invalid', function () {
            var input = $('<input type="text" value="GER 0897-3782">');
            var output = commonForms.validPhone(input);

            expect(output).toEqual(false);
        });

        it ('should recognise a too long number as invalid', function () {
            var input = $('<input type="text" value="0131123456 09889009809">');
            var output = commonForms.validPhone(input);

            expect(output).toEqual(false);
        });
    });

    describe ('Email validation', function () {
        it ( 'should be registered', function() {
            expect( commonForms.validEmail ).toBeDefined();
        });

        it ('should recognise a valid email format', function () {
            var input = $('<input type="text" value="foo@foo.foo">');
            var output = commonForms.validEmail(input);

            expect(output).toEqual(true);
        });

        it ('should pass an empty input', function () {
            var input = $('<input type="text" value="">');
            var output = commonForms.validEmail(input);

            expect(output).toEqual(true);
        });

        it ('should fail an invalid email format', function () {
            var input = $('<input type="text" value="foo.foo.foo">');
            var output = commonForms.validEmail(input);

            expect(output).toEqual(false);
        });

        it ('should show relevant error messages', function (){
            spyOn(commonForms, 'toggleFormErrors');
            spyOn(commonForms, 'toggleCurrentErrors');

            var input = $('<input type="text" value="foo.foo.foo">');
            var output = commonForms.validEmail(input);

            expect(commonForms.toggleFormErrors).toHaveBeenCalled();
            expect(commonForms.toggleCurrentErrors).toHaveBeenCalled();
        });
    });

    describe ('Valid date check function', function () {
        it ( 'should be registered', function() {
            expect( commonForms.isValidDate ).toBeDefined();
        });

        it ('should recognise a valid date', function () {
            var output = commonForms.isValidDate(1, 3, 2017);

            expect(output).toEqual(true);
        });

        it ('should recognise an incorrect day as invalid', function () {
            var output = commonForms.isValidDate(35, 3, 2017);

            expect(output).toEqual(false);
        });

        it ('should recognise an incorrect month as invalid', function () {
            var output = commonForms.isValidDate(30, 13, 2017);

            expect(output).toEqual(false);
        });

        it ('should recognise an impossible date as invalid', function () {
            var output = commonForms.isValidDate(31, 2, 2017);

            expect(output).toEqual(false);
        });
    });

    describe ('futureDate validation', function () {
        it ('should pass dates that are after the current date', function () {
            // we'll need to make a date that's in the future
            let futureDate = new Date();
            futureDate.setMonth(futureDate.getMonth() + 9);
            let futureDateDays = '0' + futureDate.getDate().toString()
            let futureDateMonths = '0' + (futureDate.getMonth() + 1).toString();
            let futureDateAsString =
                futureDateDays.substring(futureDateDays.length - 2) + '/' + futureDateMonths.substring(futureDateMonths.length - 2) + '/' + futureDate.getFullYear();

            let input = $('<input type="text" value="' + futureDateAsString + '">');
            let output = commonForms.futureDate(input);

            expect(output).toEqual(true);
        });

        it ('should fail a date in the past', function () {
            let input = $('<input type="text" value="01/01/2000">');
            let output = commonForms.futureDate(input);

            expect(output).toEqual(false);
        });

        it ('should fail an invalid date', function () {
            let input = $('<input type="text" value="hello">');
            let output = commonForms.futureDate(input);

            expect(output).toEqual(false);
        });

        it ('should show relevant error messages', function (){
            spyOn(commonForms, 'toggleFormErrors');
            spyOn(commonForms, 'toggleCurrentErrors');

            let input = $('<input type="text" value="01/01/2000">');
            let output = commonForms.futureDate(input);

            expect(commonForms.toggleFormErrors).toHaveBeenCalled();
            expect(commonForms.toggleCurrentErrors).toHaveBeenCalled();
        });
    });

    describe ('pastDate validation', function () {
        it ('should pass dates that are before the current date', function () {
            // we'll need to make a date that's in the past
            let pastDate = new Date();
            pastDate.setMonth(pastDate.getMonth() - 9);
            let pastDateDays = '0' + pastDate.getDate().toString()
            let pastDateMonths = '0' + (pastDate.getMonth() + 1).toString();
            let pastDateAsString =
                pastDateDays.substring(pastDateDays.length - 2) + '/' + pastDateMonths.substring(pastDateMonths.length - 2) + '/' + pastDate.getFullYear();

            let input = $('<input type="text" value="' + pastDateAsString + '">');
            let output = commonForms.pastDate(input);

            expect(output).toEqual(true);
        });

        it ('should fail a date in the future', function () {
            let futureDate = new Date();
            futureDate.setMonth(futureDate.getMonth() + 9);
            let futureDateDays = '0' + futureDate.getDate().toString()
            let futureDateMonths = '0' + (futureDate.getMonth() + 1).toString();
            let futureDateAsString =
                futureDateDays.substring(futureDateDays.length - 2) + '/' + futureDateMonths.substring(futureDateMonths.length - 2) + '/' + futureDate.getFullYear();

            let input = $('<input type="text" value="' + futureDateAsString + '">');
            let output = commonForms.pastDate(input);

            expect(output).toEqual(false);
        });

        it ('should fail an invalid date', function () {
            let input = $('<input type="text" value="hello">');
            let output = commonForms.pastDate(input);

            expect(output).toEqual(false);
        });

        it ('should show relevant error messages', function (){
            spyOn(commonForms, 'toggleFormErrors');
            spyOn(commonForms, 'toggleCurrentErrors');

            let input = $('<input type="text" value="01/01/2000">');
            let output = commonForms.pastDate(input);

            expect(commonForms.toggleFormErrors).toHaveBeenCalled();
            expect(commonForms.toggleCurrentErrors).toHaveBeenCalled();
        });
    });

    describe ('afterDate validation', function () {
        //mindate format is is dd/mm/yyyy
        it ('should pass dates after a provided date', function () {
            let input = $('<input type="text" data-mindate="14/02/2017" value="14/03/2017">');
            let output = commonForms.afterDate(input);

            expect(output).toEqual(true);
        });

        it ('should fail dates that are before a provided date', function () {
            let input = $('<input type="text" data-mindate="14/02/2017" value="14/01/2017">');
            let output = commonForms.afterDate(input);

            expect(output).toEqual(false);
        });

        it ('should fail an invalid date', function () {
            let input = $('<input type="text" data-mindate="14/02/2017" value="hello">');
            let output = commonForms.afterDate(input);

            expect(output).toEqual(false);
        });

        it ('should show relevant error messages', function (){
            spyOn(commonForms, 'toggleFormErrors');
            spyOn(commonForms, 'toggleCurrentErrors');

            let input = $('<input type="text" data-mindate="14/02/2017" value="01/01/2000">');
            let output = commonForms.afterDate(input);

            expect(commonForms.toggleFormErrors).toHaveBeenCalled();
            expect(commonForms.toggleCurrentErrors).toHaveBeenCalled();
        });
    });

    describe ('DD/MM/YYYY Date Validation', function () {
        it ( 'should be registered', function() {
            expect( commonForms.dateRegex ).toBeDefined();
        });

        it ('should recognise a valid date', function () {
            var input = $('<input type="text" value="14/01/1987">');
            var output = commonForms.dateRegex(input);

            expect(output).toEqual(true);
        });

        it ('should recognise incorrect input as invalid', function () {
            var input = $('<input type="text" value="14071987">');
            var output = commonForms.dateRegex(input);

            expect(output).toEqual(false);
        });
    });

    describe ('maxValue validation', function () {
        it ('should pass values less than than a provided maxValue', function () {
            let input = $('<input type="text" data-maxvalue="200" value="10">');
            let output = commonForms.maxValue(input);

            expect(output).toEqual(true);
        });

        it ('should fail values greater than a provided maxValue', function () {
            let input = $('<input type="text" data-maxvalue="200" value="210">');
            let output = commonForms.maxValue(input);

            expect(output).toEqual(false);
        });

        it ('should pass through if a maxValue is not set', function () {
            let input = $('<input type="text" value="10">');
            let output = commonForms.maxValue(input);

            expect(output).toEqual(true);
        });

        it ('should display a custom error message if one is provided', function () {
            spyOn(commonForms, 'toggleCurrentErrors');

            let input = $('<input type="text" data-message="foo {value}" data-maxvalue="200" value="210">');
            let output = commonForms.maxValue(input);

            expect(commonForms.toggleCurrentErrors).toHaveBeenCalledWith(input, false, 'invalid-max-value', 'foo 200');
        });

        it ('should show relevant error messages', function (){
            spyOn(commonForms, 'toggleFormErrors');
            spyOn(commonForms, 'toggleCurrentErrors');

            let input = $('<input type="text" data-maxvalue="200" value="10">');
            let output = commonForms.maxValue(input);

            expect(commonForms.toggleFormErrors).toHaveBeenCalled();
            expect(commonForms.toggleCurrentErrors).toHaveBeenCalled();
        });
    });

    describe ('Currency Validation', function () {
        it ( 'should be registered', function() {
            expect( commonForms.validCurrency ).toBeDefined();
        });

        it ('should recognise a valid currency amount', function () {
            var input = $('<input type="text" value="145.67">');
            var output = commonForms.validCurrency(input);

            expect(output).toEqual(true);
        });

        it ('should recognise an empty field as valid', function () {
            var input = $('<input type="text" value="">');
            var output = commonForms.validCurrency(input);

            expect(output).toEqual(true);
        });

        it ('should recognise non-numeric input as invalid', function () {
            var input = $('<input type="text" value="sadlkj">');
            var output = commonForms.validCurrency(input);

            expect(output).toEqual(false);
        });

        it ('should recognise no decimal places as valid', function () {
            var input = $('<input type="text" value="123">');
            var output = commonForms.validCurrency(input);

            expect(output).toEqual(true);
        });

        it ('should recognise too many decimal places as invalid', function () {
            var input = $('<input type="text" value="145.678">');
            var output = commonForms.validCurrency(input);

            expect(output).toEqual(false);
        });
    });

    describe ('Required field validation', function () {
        it ('should pass any non-empty input', function () {
            var input = $('<input type="text" value="">');
            var output;

            input.val('bananas');
            output = commonForms.requiredField(input);
            expect(output).toEqual(true);

            input.val('');
            output = commonForms.requiredField(input);
            expect(output).toEqual(false);
        });

        it ('should display a custom error message if one is provided', function () {
            var input = $('<input type="text" value="">');
            var output;

            spyOn(commonForms, 'toggleCurrentErrors');

            input.val('bananas');
            output = commonForms.requiredField(input, 'a custom message');
            expect(output).toEqual(true);

            expect(commonForms.toggleCurrentErrors).toHaveBeenCalledWith(input, true, 'invalid-required', 'a custom message');
        });
    });

    describe ('Required dropdown validation', function () {
        it ('should fail dropdowns with no selected value', function () {
            var select = $('<select><option value="">foo</option><option value="bar">bar</option>');
            var output;

            output = commonForms.requiredDropdown(select);
            expect(output).toEqual(false);

            select.find('option:nth-child(1)').attr('selected', 'true');
            select.val('bar')
            output = commonForms.requiredDropdown(select);
            expect(output).toEqual(true);
        });
    });

    describe ('Regexp match validation', function () {
        it ('should pass matching values and fail non-matching values', function () {
            var input = $('<input type="text" value="" pattern="\\w\\w\\d\\d">');
            var output;

            input.val('aa11');
            output = commonForms.regexMatch(input);
            expect(output).toBeTruthy();

            input.val('ball');
            output = commonForms.regexMatch(input);
            expect(output).toBeFalsy();
        });
    });

    describe ('Max character count validation', function () {
        it ('should pass fields that are under or equal to the max character limit and fail fields that are over it.', function () {
            var input = $('<input type="text" value="">');
            var output;

            input.val('bananas');
            output = commonForms.maxCharacters(input, 50);
            expect(output).toEqual(true);

            output = commonForms.maxCharacters(input, 5);
            expect(output).toEqual(false);
        });
    });

    describe ('Numeric validation', function () {
        it ('should pass numbers and fail non-numbers', function () {
            var input = $('<input type="text" value="">');
            var output;

            input.val(100);
            output = commonForms.numericOnly(input);
            expect(output).toEqual(true);

            input.val('foo');
            output = commonForms.numericOnly(input);
            expect(output).toEqual(false);

            input.val([123,456]);
            output = commonForms.numericOnly(input);
            expect(output).toEqual(false);

            input.val(['abc','def']);
            output = commonForms.numericOnly(input);
            expect(output).toEqual(false);

            input.val({foo:'bar'});
            output = commonForms.numericOnly(input);
            expect(output).toEqual(false);
        });

        it ('should obey optional min and max params', function () {
            var input = $('<input type="text" value="">');
            var output;

            input.val(-1);
            output = commonForms.numericOnly(input);
            expect(output).toEqual(false);

            input.val('10000');
            output = commonForms.numericOnly(input);
            expect(output).toEqual(false);

            input.val(-1);
            output = commonForms.numericOnly(input, -100);
            expect(output).toEqual(true);

            input.val(10000);
            output = commonForms.numericOnly(input, 1, 100000);
            expect(output).toEqual(true);
        });
    });
});

describe('Miscellaneous helper functions:', function () {
    it ('leadingzeroes() should append a parameterised number of leading zeroes to a value', function () {
        var input = 10;
        var output;

        output = commonForms.leadingZeroes(input, 1);
        expect (output).toEqual('10');

        output = commonForms.leadingZeroes(input, 2);
        expect (output).toEqual('10');

        output = commonForms.leadingZeroes(input, 3);
        expect (output).toEqual('010');

        output = commonForms.leadingZeroes(input, 4);
        expect (output).toEqual('0010');
    });

    it ('stringToDate() should return a date object from a dd/MM/yyyy date string', function () {
        var input = '14/02/1979';
        var expectedOutput = new Date('2/14/1979');

        var output = commonForms.stringToDate(input);

        expect(output).toEqual(expectedOutput);
        expect(output instanceof Date).toEqual(true);
    });

    it ('dateToString() should return a dd/MM/yyyy date string from a date object', function () {
        var input = new Date('2/14/1979');
        var expectedOutput = '14/02/1979';

        var output = commonForms.dateToString(input);

        expect(output).toEqual(expectedOutput);
    });

    it ('getLabelText should get the relevant label text for a form control', function () {
        loadFixtures('form-fields.html');

        // 1. get label from a label elements
        let output = commonForms.getLabelText($('#labeledInput'));
        expect(output).toEqual('Labeled input');

        // 2. get label from an aria-labeledby attribute
        output = commonForms.getLabelText($('#ariaLabeledInput'));
        expect(output).toEqual('Aria-labeled input');
    });
});
