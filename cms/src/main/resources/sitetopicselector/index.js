document.addEventListener('DOMContentLoaded', async () => {
  try {
    const ui = await UiExtension.register();
    const brDocument = await ui.document.get();
    const value = await ui.document.field.getValue();
    //
    const domain = location.hostname;
    var url = '/ws/topics/' + brDocument.id;
    if (location.hostname == 'localhost') {
        // when running in cargo we need to alter the topics url
        url = '/cms' + url;
    }
    fetch(url)
        .then((response) => {
          return response.json();
        })
        .then((data) => {
          let topics = data;
          const select = document.querySelector('#topics-select');
          // set which item is selected based on 'value'
          topics.forEach((topic) => {
              const option = document.createElement( 'option' );
              option.text = topic.label;
              option.value = topic.key;
              if (option.value === value) {
                option.selected = true;
                showFieldValue(topic.label);
              }
              select.add(option);
          });

          initSetFieldValueButton(ui, brDocument);
        });
  } catch (error) {
    console.error('Failed to register extension:', error.message);
    console.error('- error code:', error.code);
  }
});

function initSetFieldValueButton(ui, brDocument) {
    const label = document.querySelector('#fieldValue');
    const select = document.querySelector('#topics-select');

    if (brDocument.mode !== 'edit') {
        select.style.display = 'none';
        // set the value here
        return;
    } else {
        label.style.display = 'none';
    }

    select.onchange = (event) => {
        ui.document.field.setValue(event.target.value);
    };
}

function showFieldValue(value) {
  document.querySelector('#fieldValue').innerHTML = value;
}
