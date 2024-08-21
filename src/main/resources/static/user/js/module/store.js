// 페이지 로드 시 카테고리 링크를 업데이트하고 이벤트 위임 사용
$(document).ready(function() {
    updateCategoryLinks();

    // 이벤트 위임을 사용하여 동적으로 생성된 링크에도 이벤트 핸들러가 적용되도록 설정
    $(document).on('click', '.category-link', function(e) {
        e.preventDefault();
        updateCategoryLinks();
        window.location.href = $(this).attr('href');
    });

    // 검색 버튼 클릭 시 검색 수행
    $('#searchButton').on("click", searchStores);
});

// 주소 리스트 토글
function toggleAddressList() {
    $('#addressListBox').toggle();
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
    $('#newAddress, #newAddrDetail, #newZipcode').val('');
    $('#addAddressModal').show();
}

// 주소 추가 모달창 닫기
function closeAddAddressModal() {
    $('#addAddressModal').hide();
}

// 다음 우편번호 API를 사용하여 주소 찾기
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete(data) {
            const addr = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress;
            let extraAddr = '';

            if (data.userSelectedType === 'R') {
                if (data.bname && /[동|로|가]$/g.test(data.bname)) extraAddr += data.bname;
                if (data.buildingName && data.apartment === 'Y') extraAddr += (extraAddr ? ', ' : '') + data.buildingName;
                if (extraAddr) extraAddr = ` (${extraAddr})`;
            }

            $('#newZipcode').val(data.zonecode);
            $('#newAddress').val(addr + extraAddr).focus();
        }
    }).open();
}

// 추가된 주소 저장
function saveAddress() {
    const newAddress = $('#newAddress').val();
    const newAddrDetail = $('#newAddrDetail').val();
    const newZipcode = $('#newZipcode').val();

    $.post("/user/addAddress", { newAddress, newAddrDetail, newZipcode })
        .done(() => {
            alert("새 주소가 추가되었습니다.");
            location.reload();
        })
        .fail(() => alert("주소 추가에 실패했습니다."));

    closeAddAddressModal();
}

// 기본 주소 설정
function setDefaultAddress(addressKey) {
    $.post("/user/setDefaultAddress", { addressKey })
        .done(() => {
            alert("대표 주소가 변경되었습니다.");
            location.reload();
        })
        .fail(() => alert("대표 주소 변경에 실패했습니다."));
}

// 로그인하지 않은 사용자를 위한 임시 주소 입력 기능 추가
function showGuestAddressModal() {
    $('#guestAddress, #guestAddrDetail').val('');
    $('#guestAddressModal').show();
}

// 임시 주소 입력 모달창 닫기
function closeGuestAddressModal() {
    $('#guestAddressModal').hide();
}

// 임시 주소 입력을 위한 다음 우편번호 API 사용
function execDaumPostcodeForGuest() {
    new daum.Postcode({
        oncomplete(data) {
            const addr = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress;
            let extraAddr = '';

            if (data.userSelectedType === 'R') {
                if (data.bname && /[동|로|가]$/g.test(data.bname)) extraAddr += data.bname;
                if (data.buildingName && data.apartment === 'Y') extraAddr += (extraAddr ? ', ' : '') + data.buildingName;
                if (extraAddr) extraAddr = ` (${extraAddr})`;
            }

            $('#guestAddress').val(addr + extraAddr).focus();
        }
    }).open();
}

// 카테고리 링크에 주소 파라미터를 추가하여 업데이트
function updateCategoryLinks() {
    const guestAddress = $('#guestAddress').val() || $('#displayedGuestAddress').text() || "";

    $('.category-link').each(function() {
        const baseUrl = $(this).data('base-url');
        $(this).attr('href', `${baseUrl}?address=${encodeURIComponent(guestAddress)}`);
    });
}

// 임시 주소 적용
function applyGuestAddress() {
    const guestAddress = $('#guestAddress').val();
    const guestAddrDetail = $('#guestAddrDetail').val();
    const fullGuestAddress = `${guestAddress} ${guestAddrDetail}`;
    const currentCategory = getCurrentCategory();

    if (!guestAddress) {
        alert("주소를 입력하세요.");
        return;
    }

    $('#displayedGuestAddress').text(fullGuestAddress);
    $('#guestAddressDisplay').show();
    $('#guestAddressModal').hide();

    $.ajax({
        url: "/user/filterStoresByAddress",
        type: "GET",
        data: { address: guestAddress, categoryId: currentCategory }
    }).done((data) => {
        $('#storeList').html($(data).find('#storeList').html());
        history.pushState({}, null, '/user/category/0');
        updateCategoryLinks();
    }).fail(() => alert("가게 목록을 불러오는데 실패했습니다."));
}

// 가게명 검색 기능
function searchStores() {
    const query = $('#storeSearchInput').val().trim();
    const categoryId = getCurrentCategory();
    const guestAddress = $("div[th\\:if='${user != null}']").length
        ? $("div[th\\:if='${user != null}'] p").text().trim()
        : $("#displayedGuestAddress").text().trim() || "";

    if (!query) {
        $.ajax({
            url: "/user/category/0",
            type: "GET",
            data: { address: guestAddress, page: 1, pageSize: 8 }
        }).done((data) => {
            $('#storeList').html($(data).find('#storeList').html());
            history.pushState({}, null, `/user/category/0?address=${encodeURIComponent(guestAddress)}`);
            location.reload();
        }).fail(() => alert("전체 목록을 불러오는데 실패했습니다."));
        return;
    }

    const url = `/user/search/${categoryId}?query=${encodeURIComponent(query)}&address=${encodeURIComponent(guestAddress)}`;

    $.ajax({
        url: url,
        type: "GET",
    }).done((data) => {
        $('#storeList').html($(data).find('#storeList').html());
        history.pushState({}, null, url);
        location.reload();
    }).fail(() => alert("검색 결과를 불러오는데 실패했습니다."));
}

function getCurrentCategory() {
    const urlParams = new URLSearchParams(window.location.search);
    const pathSegments = window.location.pathname.split('/');
    const categorySegment = pathSegments.find(segment => segment.match(/^\d+$/));
    return categorySegment || 0; // 카테고리가 없으면 기본값 0 (ALL) 사용
}