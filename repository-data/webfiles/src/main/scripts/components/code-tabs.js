class CodeTabs {
    constructor(element) {
        this.element = element;
        const tabs = Array.from(element.querySelectorAll('.mg_code__tab'));
        const tabLinks = Array.from(element.querySelectorAll('.mg_code__tab-link'));
        const tabContainers = Array.from(element.querySelectorAll('.mg_code__tabpanel'));
        const tabHeadingLinks = Array.from(element.querySelectorAll('.mg_code__tabpanel-link'));

        // convert mobile tab links to buttons
        tabHeadingLinks.forEach(tabHeadingLink => {
            const button = document.createElement('button');
            button.innerHTML = tabHeadingLink.innerHTML;
            button.setAttribute('role', 'button');
            button.setAttribute('aria-controls', tabHeadingLink.getAttribute('aria-controls'));
            button.classList.add('mg_code__tabpanel-button', 'ds_link');

            tabHeadingLink.parentNode.appendChild(button);
            tabHeadingLink.parentNode.removeChild(tabHeadingLink);
        });

        const tabButtons = Array.from(element.querySelectorAll('.mg_code__tabpanel-button'));

        // set initial state of button
        tabButtons.forEach(tabButton => {
            tabButton.classList.add('js-tab-trigger');

            const tabPanelForButton = element.querySelector(`#${tabButton.getAttribute('aria-controls')}`).closest('.mg_code__tabpanel');
            if (tabPanelForButton.classList.contains('mg_code__tabpanel--open')) {
                this.activateTabButton(tabButton);
            } else {
                this.deactivateTabButton(tabButton);
            }
        });

        // set initial state of link
        tabLinks.forEach(tabLink => {
            tabLink.classList.add('js-tab-trigger');

            const tabPanelForLink = element.querySelector(`#${tabLink.getAttribute('aria-controls')}`).closest('.mg_code__tabpanel');
            if (tabPanelForLink.classList.contains('mg_code__tabpanel--open')) {
                this.activateTabLink(tabLink);
            } else {
                this.deactivateTabLink(tabLink);
            }
        });

        tabs.forEach(tab => {
            const tabLink = tab.querySelector('.js-tab-trigger');

            tabLink.addEventListener('keydown', event => {
                let index = tabs.indexOf(tab);

                if (event.key === 'ArrowRight') {
                    index = index + 1;
                } else if (event.key === 'ArrowLeft') {
                    index = index - 1;
                } else if (event.key === ' ') {
                    event.preventDefault();
                    this.switchToTab(tabLink.getAttribute('aria-controls'));
                }

                const targetTab = tabs[this.modulo(index, tabs.length)];

                targetTab.querySelector('.js-tab-link').focus();
            });
        });

        element.addEventListener('click', event => {
            if (event.target.classList.contains('js-tab-trigger')) {
                event.preventDefault();
                this.switchToTab(event.target.getAttribute('aria-controls'));
            }
        });

        this.tabContainers = tabContainers;
        this.tabLinks = tabLinks;
        this.tabButtons = tabButtons;
    }

    activateTabButton(tabButton) {
        tabButton.setAttribute('aria-expanded', 'true');
    }

    activateTabLink(tabLink) {
        tabLink.setAttribute('aria-expanded', 'true');
        tabLink.setAttribute('aria-selected', 'true');
        tabLink.tabIndex = '0';
    }

    deactivateTabButton(tabButton) {
        tabButton.setAttribute('aria-expanded', 'false');
    }

    deactivateTabLink(tabLink) {
        tabLink.setAttribute('aria-expanded', 'false');
        tabLink.setAttribute('aria-selected', 'false');
        tabLink.tabIndex = '-1';
    }

    switchToTab(id) {
        const targetTabContainer = this.element.querySelector(`#${id}`).closest('.mg_code__tabpanel');

        this.tabContainers.forEach(tabContainer => {
            if (tabContainer.querySelector(`#${id}`)) {
                tabContainer.classList.toggle('mg_code__tabpanel--open');
            } else {
                tabContainer.classList.remove('mg_code__tabpanel--open');
            }
        });

        this.tabLinks.forEach(tabLink => {
            if (tabLink.getAttribute('aria-controls') === id && targetTabContainer.classList.contains('mg_code__tabpanel--open')) {
                this.activateTabLink(tabLink);
            } else {
                this.deactivateTabLink(tabLink);
            }
        });

        this.tabButtons.forEach(tabButton => {
            if (tabButton.getAttribute('aria-controls') === id && targetTabContainer.classList.contains('mg_code__tabpanel--open')) {
                this.activateTabButton(tabButton);
            } else {
                this.deactivateTabButton(tabButton);
            }
        });
    }

    modulo (a, b) {
        return ((a % b) + b) % b;
    }
}

export default CodeTabs;
