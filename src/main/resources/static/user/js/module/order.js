// 메뉴 삭제
function deleteCartItem(cartKey) {
    if (confirm("정말 삭제하시겠습니까?")) {
        $.ajax({
            url: '/user/deleteCartItem',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ cartKey: cartKey }),
            success: function(data) {
                if (data.success) {
                    alert("삭제되었습니다.");

                    // 모든 메뉴가 삭제되었는지 확인
                    if ($('.cart-item').length === 0) {
                        window.location.href = '/user/myCart';
                    } else {
                        location.reload();
                    }
                } else {
                    alert("삭제에 실패했습니다.");
                }
            },
            error: function() {
                alert("오류가 발생했습니다. 다시 시도해 주세요.");
            }
        });
    }
}

// Hidden input 값을 jQuery 변수로 가져오기
const orderAmount1 = parseInt($('#orderAmount1').val());
const deliveryAmount1 = parseInt($('#deliveryAmount1').val());
const orderAmount2 = parseInt($('#orderAmount2').val());
const deliveryAmount2 = parseInt($('#deliveryAmount2').val());
const orderAmount3 = parseInt($('#orderAmount3').val());
const deliveryAmount3 = parseInt($('#deliveryAmount3').val());

let totalAmount = parseInt($('#totalAmountText').text().replace(/[^0-9]/g, ''));
let deliveryFee = 0;
let couponDiscount = 0;

// 가게 주소와 사용자 주소에서 구, 동, 읍을 추출하는 함수
function extractArea(address) {
    const areaRegex = /(구|동|읍)/;
    const match = address.match(areaRegex);
    if (match) {
        const index = address.indexOf(match[0]);
        return address.substring(index - 2, index + match[0].length);
    }
    return '';
}

// 사용자 주소와 가게 주소를 비교하여 주문 가능 여부 판단
function validateDeliveryArea() {
    const storeAddress = $('#storeAddress').val();
    const userAddress = $('#addressInfo p').text().trim();

    const storeArea = extractArea(storeAddress);
    const userArea = extractArea(userAddress);

    if (storeArea !== userArea) {
        $('#addressErrorMessage').show();
        $('#confirmOrderButton').prop('disabled', true);
    } else {
        $('#addressErrorMessage').hide();
        $('#confirmOrderButton').prop('disabled', false);
    }
}

// 배달비 계산 로직
function toggleDeliveryOption(isDelivery) {
    const deliveryFeeContainer = $('#deliveryFeeContainer');
    const riderDescContainer = $('#riderDescContainer');
    const addressInfo = $('#addressInfo');

    if (isDelivery) {
        deliveryFeeContainer.show();
        riderDescContainer.show();
        addressInfo.show();

        if (totalAmount < orderAmount1) {
            deliveryFee = deliveryAmount1;
        } else if (totalAmount >= orderAmount1 && totalAmount < orderAmount2) {
            deliveryFee = deliveryAmount2;
        } else if (totalAmount >= orderAmount2) {
            deliveryFee = deliveryAmount3;
        }

        $('#deliveryFeeText').text('배달비: ' + deliveryFee + '원');
        validateDeliveryArea(); // 주소 검증 수행
    } else {
        deliveryFeeContainer.hide();
        riderDescContainer.hide();
        addressInfo.hide();
        deliveryFee = 0;

        $('#addressErrorMessage').hide();
        $('#confirmOrderButton').prop('disabled', false);
    }

    calculateTotalPayment();
}

// 쿠폰 할인 적용
function applyCouponDiscount() {
    const couponSelect = $('#couponSelect');
    couponDiscount = parseInt(couponSelect.find(':selected').data('discount'));

    $('#couponDiscountText').text('쿠폰 할인: ' + couponDiscount + '원');
    calculateTotalPayment();
}

// 총 결제 금액 계산
function calculateTotalPayment() {
    const totalPayment = totalAmount + deliveryFee - couponDiscount;
    $('#totalPaymentText').text('총 결제금액: ' + totalPayment + '원');
}

// 초기 배달비 및 총 결제금액 계산
toggleDeliveryOption(true);

// 주소 리스트 토글
function toggleAddressList() {
    const $addressListBox = $('#addressListBox');
    if ($addressListBox.is(':hidden')) {
        $addressListBox.show();
    } else {
        $addressListBox.hide();
    }
}

// 선택된 주소를 기본 주소로 설정
function selectAddress() {
    const addressKey = $('#addressSelect').val();
    if (addressKey) {
        setDefaultAddress(addressKey);
    }
}

// 주소 추가 모달창 표시
function showAddAddressModal() {
    $('#newAddress').val('');
    $('#newAddrDetail').val('');
    $('#newZipcode').val('');
    $('#addAddressModal').show();
}

// 주소 추가 모달창 닫기
function closeAddAddressModal() {
    $('#addAddressModal').hide();
}

// 다음 우편번호 API를 사용하여 주소 찾기
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            let addr = '';
            let extraAddr = '';

            if (data.userSelectedType === 'R') { // 도로명 주소 선택 시
                addr = data.roadAddress;
            } else { // 지번 주소 선택 시
                addr = data.jibunAddress;
            }

            if (data.userSelectedType === 'R') {
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                if (extraAddr !== '') {
                    extraAddr = ' (' + extraAddr + ')';
                }
            }

            $('#newZipcode').val(data.zonecode);
            $('#newAddress').val(addr + extraAddr);
            $('#newAddrDetail').focus();
        }
    }).open();
}

// 추가된 주소 저장
function saveAddress() {
    const newAddress = $('#newAddress').val();
    const newAddrDetail = $('#newAddrDetail').val();
    const newZipcode = $('#newZipcode').val();

    $.post("/user/addAddress", {
        newAddress: newAddress,
        newAddrDetail: newAddrDetail,
        newZipcode: newZipcode
    }).done(function() {
        alert("새 주소가 추가되었습니다.");
        location.reload();
    }).fail(function() {
        alert("주소 추가에 실패했습니다.");
    });

    closeAddAddressModal();
}

// 기본 주소 설정
function setDefaultAddress(addressKey) {
    $.post("/user/setDefaultAddress", { addressKey: addressKey })
        .done(function() {
            alert("대표 주소가 변경되었습니다.");
            location.reload();
        })
        .fail(function() {
            alert("대표 주소 변경에 실패했습니다.");
        });
}

// 주문버튼 클릭시
function confirmOrder() {
    const paymentMethod = $('#paymentMethod').val();
    const userPoint = parseInt($('#userPoint').val());
    const minOrderAmount = parseInt($('#minOrderAmount').val());
    const totalPayment = totalAmount + deliveryFee - couponDiscount;

    if (totalAmount < minOrderAmount) {
        alert("최소 주문 금액을 충족하지 못했습니다.");
        window.location.href = '/user/myCart';
        return;
    }

    if (paymentMethod === "포인트 결제" && userPoint < totalPayment) {
        alert("잔액이 부족합니다.");
        return;
    }

    const orderNo = generateOrderNo(); // 주문번호 생성

    // 주문 데이터 서버로 전송
    const cartItems = [];
    $('.cart-item').each(function() {
        const cartKey = $(this).data('cartKey');
        const menuName = $(this).find('.menu-name').text().trim().replace('메뉴: ', '');
        const optionName = $(this).find('.option-name').map(function() {
            return $(this).text().trim();
        }).get().join(' '); // 옵션을 빈칸으로 구분하여 저장
        const quantity = parseInt($(this).find('.quantity').text().replace(/[^0-9]/g, '').trim());

        cartItems.push({
            cartKey: cartKey,
            menuName: menuName,
            optionName: optionName,
            quantity: quantity
        });
    });

    const orderData = {
        storeInfoKey: $('#storeInfoKey').val(),  // storeInfoKey 추가
        orderNo: orderNo,
        paymentMethod: paymentMethod,
        totalPayment: totalPayment,
        deliveryFee: deliveryFee,
        couponDiscount: couponDiscount,
        address: $('#addressInfo p').text().trim(),
        riderDesc: $('#riderDesc').val(),
        shopDesc: $('#shopDesc').val(),
        orderMethod: $('input[name="orderMethod"]:checked').val(), // orderMethod 추가
        couponKey: $('#couponSelect').val() !== "0" ? $('#couponSelect').val() : null, // couponKey 추가
        cartItems: cartItems // cartItems 추가
    };

    $.ajax({
        url: '/user/confirmOrder',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(orderData),
        success: function(response) {
            if (response.success) {
                alert("주문이 확정되었습니다. 주문번호: " + orderNo);
                window.location.href = '/user/myOrder';
            } else {
                alert("주문에 실패했습니다. 다시 시도해 주세요.");
            }
        },
        error: function(xhr) {
            const response = JSON.parse(xhr.responseText);
            alert(response.message || "오류가 발생했습니다. 다시 시도해 주세요.");
        }
    });
}

// 주문번호 생성 함수 (16진수 6자리)
function generateOrderNo() {
    return Math.floor(Math.random() * 0xFFFFFF).toString(16).padStart(6, '0').toUpperCase();
}
