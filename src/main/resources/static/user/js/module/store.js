// 주소 리스트 토글
function toggleAddressList() {
    var $addressListBox = $('#addressListBox');
    if ($addressListBox.is(':hidden')) {
        $addressListBox.show();
    } else {
        $addressListBox.hide();
    }
}

// 선택된 주소를 기본 주소로 설정
function selectAddress() {
    var addressKey = $('#addressSelect').val();
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
            var addr = '';
            var extraAddr = '';

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
    var newAddress = $('#newAddress').val();
    var newAddrDetail = $('#newAddrDetail').val();
    var newZipcode = $('#newZipcode').val();

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

// 로그인하지 않은 사용자를 위한 임시 주소 입력 기능 추가
function showGuestAddressModal() {
    $('#guestAddress').val('');
    $('#guestAddrDetail').val('');
    $('#guestAddressModal').show();
}

// 임시 주소 입력 모달창 닫기
function closeGuestAddressModal() {
    $('#guestAddressModal').hide();
}

// 임시 주소 입력을 위한 다음 우편번호 API 사용
function execDaumPostcodeForGuest() {
    new daum.Postcode({
        oncomplete: function(data) {
            var addr = '';
            var extraAddr = '';

            if (data.userSelectedType === 'R') {
                addr = data.roadAddress;
            } else {
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

            $('#guestAddress').val(addr + extraAddr);
            $('#guestAddrDetail').focus();
        }
    }).open();
}

// 카테고리 링크에 주소 파라미터를 추가하여 업데이트
function updateCategoryLinks() {
    var guestAddress = $('#guestAddress').val() || $('#displayedGuestAddress').text() || "";

    console.log("Updating category links with address: " + guestAddress); // 디버깅용 로그 추가
    $('.category-link').each(function() {
        var $link = $(this);
        var baseUrl = $link.data('base-url');
        var updatedHref = baseUrl + "?address=" + encodeURIComponent(guestAddress);
        console.log("Updated link: " + updatedHref); // 디버깅용 로그 추가
        $link.attr('href', updatedHref);
    });
}

// 페이지 로드 시 카테고리 링크를 업데이트하고 이벤트 위임 사용
$(document).ready(function() {
    updateCategoryLinks();

    // 이벤트 위임을 사용하여 동적으로 생성된 링크에도 이벤트 핸들러가 적용되도록 설정
    $(document).on('click', '.category-link', function(e) {
        e.preventDefault(); // 기본 동작 막기
        console.log("Category link clicked: " + $(this).attr('href')); // 디버깅용 로그 추가
        updateCategoryLinks();  // 카테고리 클릭 시 링크 갱신
        window.location.href = $(this).attr('href'); // 주소를 포함한 링크로 이동
    });
});

// 임시 주소 적용
function applyGuestAddress() {
    var guestAddress = $('#guestAddress').val();
    var guestAddrDetail = $('#guestAddrDetail').val();
    var fullGuestAddress = guestAddress + " " + guestAddrDetail;

    if (guestAddress === "") {
        alert("주소를 입력하세요.");
        return;
    }

    // 입력된 주소를 화면에 표시
    $('#displayedGuestAddress').text(fullGuestAddress);
    $('#guestAddressDisplay').show(); // 대표주소지 섹션 표시
    $('#guestAddressModal').hide(); // 모달 숨기기

    // 서버에 주소를 보내고 해당 지역의 가게 목록을 가져옴
    $.ajax({
        url: "/user/filterStoresByAddress", // 가게 목록을 필터링하는 서버 API 호출
        type: "GET",
        data: { address: guestAddress }
    }).done(function(data) {
        $('#storeList').html($(data).find('#storeList').html());  // 가게 목록만 갱신
        // 페이지 전체 새로고침 방지
        history.pushState({}, null, '/user/category/0');
        // AJAX 완료 후 카테고리 링크 갱신
        updateCategoryLinks();
    }).fail(function() {
        alert("가게 목록을 불러오는데 실패했습니다.");
    });
}

// 가게명 검색 기능
function searchStores() {
    var query = $('#storeSearchInput').val().trim();
    var guestAddress = "";

    // 로그인 상태 확인
    if ($("div[th\\:if='${user != null}']").length) {
        // 로그인한 사용자의 주소 사용
        guestAddress = $("div[th\\:if='${user != null}'] p").text().trim();
    } else {
        // 비로그인 사용자의 임시 주소 사용
        guestAddress = $("#displayedGuestAddress").text().trim() || "";
    }

    if (query === "") {
        // 검색어가 비어 있을 경우 전체 목록을 요청
        $.ajax({
            url: "/user/category/0",
            type: "GET",
            data: {
                address: guestAddress,
                page: 1,
                pageSize: 8
            }
        }).done(function(data) {
            $('#storeList').html($(data).find('#storeList').html());  // 가게 목록만 갱신
            history.pushState({}, null, '/user/category/0?address=' + encodeURIComponent(guestAddress));
        }).fail(function(xhr, status, error) {
            console.error("전체 목록 요청 중 오류 발생:", error);  // 오류 메시지 출력
            alert("전체 목록을 불러오는데 실패했습니다.");
        });
        return;
    }

    // 검색어가 있을 경우 검색 수행
    $.ajax({
        url: "/user/search",
        type: "GET",
        data: {
            query: query,
            address: guestAddress,
            page: 1,
            pageSize: 8
        }
    }).done(function(data) {
        $('#storeList').html($(data).find('#storeList').html());  // 가게 목록만 갱신
        history.pushState({}, null, '/user/search?query=' + encodeURIComponent(query) + '&address=' + encodeURIComponent(guestAddress));
    }).fail(function(xhr, status, error) {
        console.error("검색 중 오류 발생:", error);  // 오류 메시지 출력
        alert("검색 결과를 불러오는데 실패했습니다.");
    });
}

// 검색 버튼 클릭 시 검색 수행
$('#searchButton').on("click", function() {
    searchStores();
});
