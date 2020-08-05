'use strict';

import leadingZeroes from './leading-zeroes';

const date = {
    ddMMyyyy: function(inputDate) {
        return leadingZeroes(inputDate.getDate(), 2) + '/' + leadingZeroes(inputDate.getMonth() + 1, 2) + '/' + inputDate.getFullYear();
    }
};

export default date;
