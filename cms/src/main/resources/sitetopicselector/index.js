/**

todo ...
the html fetches js from
<script type="text/javascript" src="https://unpkg.com/@bloomreach/ui-extension@15.2.2/dist/ui-extension.min.js"></script>
do we need to store that locally or anything?  at least update the version number I guess

style to look like other selct ones.
set the size
when in readonly show the right one
**/


document.addEventListener('DOMContentLoaded', async () => {
  try {
    const ui = await UiExtension.register();
    const brDocument = await ui.document.get();
    const value = await ui.document.field.getValue();
    const url = '/cms/ws/internal/topics/' + brDocument.id;
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
              select.add(option);
          });

        showFieldValue(value);
        initSetFieldValueButton(ui, brDocument);

    });

    // should these be after the url has been fetched, put them in the callback?

  } catch (error) {
    console.error('Failed to register extension:', error.message);
    console.error('- error code:', error.code);
  }
});

function initSetFieldValueButton(ui, brDocument) {

    const label = document.querySelector('#fieldValue');
    if (brDocument.mode !== 'edit') {
        label.style.display = 'none';
        // set the value here
        return;
    }

    const select = document.querySelector('#topics-select');
    select.onchange = (event) => {
        ui.document.field.setValue(event.target.value);
    };
}

function showFieldValue(value) {
  document.querySelector('#fieldValue').innerHTML = value;
}
