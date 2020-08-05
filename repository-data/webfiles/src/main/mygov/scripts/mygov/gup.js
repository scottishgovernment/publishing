// GUP

/* jshint ignore:start */
export default function (name, $window) {
    const win = $window || window;
    name = name.replace(/[\[]/, '\\\[').replace(/[\]]/, '\\\]');
    const regexS = '[\\?&]' + name + '=([^&#]*)';
    const regex = new RegExp(regexS);
    const results = regex.exec(win.location.href);
    if (results === null) {
        return null;
    } else {
        return results[1];
    }
}
/* jshint ignore:end */
