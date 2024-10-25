document.addEventListener('DOMContentLoaded', () => {
    const linkButtons = [].slice.call(document.querySelectorAll('a[role="button"]'));
    const keyCodes = {
        'space': 32
    };

    linkButtons.forEach(linkButton => {
        linkButton.addEventListener('keypress', event => {
            if (event.keyCode === keyCodes.space) {
                event.preventDefault();
                event.target.click();
            }
        });
    });
});
