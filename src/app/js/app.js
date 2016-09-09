/**
 * Created by ndriama on 23/03/16.
 */

tinymce.init({
    selector: 'textarea',
    height: 400,
    plugins: [
        'advlist autolink lists link image charmap print preview anchor',
        'searchreplace visualblocks wordcount spellchecker fullscreen',
        'insertdatetime media table contextmenu paste code',
        'colorpicker textcolor'
    ],
    toolbar: 'insertfile undo redo | bold italic underline | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image'
});