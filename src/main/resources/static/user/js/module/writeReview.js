$(document).ready(function() {
    $(".image-box").click(function() {
        const inputId = $(this).data("input-id");
        $("#" + inputId).click();
    });

    $("input[type='file']").change(function(event) {
        const input = $(this);
        const imageBox = input.prev(".writeReview__img");
        const file = this.files[0];
        const fileType = file.type;
        const validImageTypes = ["image/jpeg", "image/png", "image/jpg"];

        if (validImageTypes.includes(fileType)) {
            const reader = new FileReader();
            reader.onload = function(e) {
                imageBox.html('<img src="' + e.target.result + '" alt="Uploaded Image">');
            }
            reader.readAsDataURL(file);
        } else {
            alert("jpg, jpeg, png 파일만 업로드 가능합니다.");
            input.val("");
        }
    });
});