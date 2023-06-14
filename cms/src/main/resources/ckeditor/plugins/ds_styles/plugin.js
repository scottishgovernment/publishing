CKEDITOR.plugins.add( 'ds_styles', {
    init: function( editor ) {
        if (!CKEDITOR.hasAddedDsStyles) {
            let loadedStyleSet = CKEDITOR.stylesSet.loaded;
            let loadedStyleSetName;
            for (let key in loadedStyleSet) {
                if (loadedStyleSet[key] === 1) {
                    loadedStyleSetName = key;
                    break;
                }
            }

            let stylesSet = CKEDITOR.stylesSet.registered[loadedStyleSetName] || [];
            stylesSet.push({
                element: 'code',
                name: 'Code',
            });

            CKEDITOR.hasAddedDsStyles = true;
        }
    }
});
