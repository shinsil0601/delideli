function showAddAddressModal() {
    document.getElementById('addressKey').value = '';
    document.getElementById('newAddress').value = '';
    document.getElementById('newAddrDetail').value = '';
    document.getElementById('newZipcode').value = '';
    document.getElementById('modalTitle').innerText = '주소 추가';
    document.getElementById('addressModal').style.display = 'block';
}

function showModifyAddressModal(button) {
    const addressKey = button.getAttribute('data-address-key');
    const address = button.getAttribute('data-address');
    const addrDetail = button.getAttribute('data-addr-detail');
    const zipcode = button.getAttribute('data-zipcode');

    document.getElementById('addressKey').value = addressKey;
    document.getElementById('newAddress').value = address;
    document.getElementById('newAddrDetail').value = addrDetail;
    document.getElementById('newZipcode').value = zipcode;
    document.getElementById('modalTitle').innerText = '주소 수정';
    document.getElementById('addressModal').style.display = 'block';
}

function closeAddressModal() {
    document.getElementById('addressModal').style.display = 'none';
}

function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우
                addr = data.jibunAddress;
            }

            if(data.userSelectedType === 'R'){
                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraAddr += data.bname;
                }
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                if(extraAddr !== ''){
                    extraAddr = ' (' + extraAddr + ')';
                }
            }

            document.getElementById('newZipcode').value = data.zonecode;
            document.getElementById("newAddress").value = addr + extraAddr;
            document.getElementById("newAddrDetail").focus();
        }
    }).open();
}

function saveAddress() {
    var addressKey = document.getElementById('addressKey').value;
    var newAddress = document.getElementById('newAddress').value;
    var newAddrDetail = document.getElementById('newAddrDetail').value;
    var newZipcode = document.getElementById('newZipcode').value;

    if (addressKey) {
        $.post("/user/modifyAddress", { addressKey: addressKey, newAddress: newAddress, newAddrDetail: newAddrDetail, newZipcode: newZipcode }, function(data) {
            alert("주소가 수정되었습니다.");
            location.reload();
        }).fail(function() {
            alert("주소 수정에 실패했습니다.");
        });
    } else {
        $.post("/user/addAddress", { newAddress: newAddress, newAddrDetail: newAddrDetail, newZipcode: newZipcode }, function(data) {
            alert("새 주소가 추가되었습니다.");
            location.reload();
        }).fail(function() {
            alert("주소 추가에 실패했습니다.");
        });
    }

    closeAddressModal();
}

function setDefaultAddress(button) {
    const addressKey = button.getAttribute('data-address-key');

    $.post("/user/setDefaultAddress", { addressKey: addressKey }, function(data) {
        alert("대표 주소가 변경되었습니다.");
        location.reload();
    }).fail(function() {
        alert("대표 주소 변경에 실패했습니다.");
    });
}

function deleteAddress(button) {
    const addressKey = button.getAttribute('data-address-key');

    $.post("/user/deleteAddress", { addressKey: addressKey }, function(data) {
        alert("주소가 삭제되었습니다.");
        location.reload();
    }).fail(function() {
        alert("주소 삭제에 실패했습니다.");
    });
}
