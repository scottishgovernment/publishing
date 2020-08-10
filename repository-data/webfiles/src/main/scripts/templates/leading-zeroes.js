'use strict';

export default function (value, length) {
    let ret = value.toString();

    while (ret.length < length) {
        ret = '0' + ret.toString();
    }

    return ret;
}
