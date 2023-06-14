'use strict';

class UpdateHistory {
    constructor(updateHistory) {
        this.updateHistory = updateHistory;
        this.metadataLink = document.querySelector(`.ds_metadata [href="#${this.updateHistory.id}"]`);
        this.endLink = document.querySelector(`[aria-controls="${this.updateHistory.id}"]`);
    }

    init() {
        if (this.metadataLink) {
            this.metadataLink.addEventListener('click', (event) => {
                event.preventDefault();

                if (this.endLink && this.endLink.getAttribute('aria-expanded') === 'false') {
                    this.endLink.click();
                }

                this.updateHistory.scrollIntoView();
            });
        }
    }
}

export default UpdateHistory;
