/**
 * Looks for a bloomreach webfiles path in a hidden input field, modifies provided path
 * @param {*} path
 * @returns modified path
 */
export default function (path = '') {
    const webfilePathInput = document.getElementById('br-webfile-path');

    if (webfilePathInput) {
        const webfilesURL = new URL(webfilePathInput.value, `${window.location.protocol}//${window.location.hostname}`);
        return webfilesURL.pathname + path + webfilesURL.search;
    } else {
        return path;
    }
}
