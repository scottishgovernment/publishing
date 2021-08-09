/**
 * Looks for a bloomreach webfiles path in a hidden input field, modifies provided path
 * @param {*} path
 * @returns modified path
 */
export default function (path) {
    const webfilePathInput = document.getElementById('br-webfile-path');
    if (webfilePathInput) {
        return webfilePathInput.value + path;
    } else {
        return path;
    }
}
