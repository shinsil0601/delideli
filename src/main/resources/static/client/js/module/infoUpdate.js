function enableEdit(button) {
    var span = button.previousElementSibling;
    var value = span.innerText;
    var input = document.createElement('input');
    input.type = 'text';
    input.name = span.dataset.field;
    input.value = value;
    span.parentNode.replaceChild(input, span);
    button.innerText = '취소';
    button.onclick = function() { cancelEdit(button, input, value); };
}

function cancelEdit(button, input, originalValue) {
    var span = document.createElement('span');
    span.dataset.field = input.name;
    span.innerText = originalValue;
    input.parentNode.replaceChild(span, input);
    button.innerText = '수정';
    button.onclick = function() { enableEdit(button); };
}

function addHiddenInputs(form) {
    var spans = form.querySelectorAll('span[data-field]');
    spans.forEach(function(span) {
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = span.dataset.field;
        input.value = span.innerText;
        form.appendChild(input);
    });
}

document.addEventListener('DOMContentLoaded', function() {
    var form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', function() {
            addHiddenInputs(form);
        });
    }
    showMessage(); // 메시지 표시 함수 호출
});

function showMessage() {
    const flashMessageElement = document.getElementById('flashMessage');
    if (flashMessageElement) {
        const message = flashMessageElement.value;
        if (message) {
            alert(message);
        }
    }
}


