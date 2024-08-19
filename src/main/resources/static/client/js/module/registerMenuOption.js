$(document).ready(function() {
    // 옵션 그룹 추가 버튼 클릭 시
    $('#openOptionGroupWindowBtn').on('click', function() {
        var menuKey = $(this).data('menu-key');
        var url = '/client/addOptionGroup/' + menuKey;
        var windowName = '옵션그룹추가';
        var windowFeatures = 'width=600,height=400,resizable=yes,scrollbars=yes';

        window.open(url, windowName, windowFeatures);
    });

    // 옵션 그룹 수정 버튼 클릭 시
    $(document).on('click', '.editOptionGroupBtn', function() {
        var optionGroupKey = $(this).data('option-group-key');
        var url = '/client/updateOptionGroup/' + optionGroupKey;
        var windowName = '옵션그룹수정';
        var windowFeatures = 'width=600,height=400,resizable=yes,scrollbars=yes';

        window.open(url, windowName, windowFeatures);
    });

    // 옵션 그룹 제거 버튼 클릭 시
    $(document).on('click', '.deleteOptionGroupBtn', function() {
        var optionGroupKey = $(this).data('option-group-key');
        if (confirm('정말 이 옵션 그룹을 삭제하시겠습니까?')) {
            $.ajax({
                url: '/client/deleteOptionGroup/' + optionGroupKey,
                type: 'POST',
                success: function(response) {
                    alert('옵션 그룹이 삭제되었습니다.');
                    window.location.reload();
                },
                error: function(error) {
                    console.error('옵션 그룹 삭제 중 오류 발생:', error);
                    alert('옵션 그룹 삭제 중 오류가 발생했습니다.');
                }
            });
        }
    });

    // 옵션 그룹 클릭 시 옵션 그룹명 및 옵션 목록 표시
    $(document).on('click', '.option-group-container', function() {
        var optionGroupName = $(this).find('.option-group-name').text();
        var optionGroupKey = $(this).find('.editOptionGroupBtn').data('option-group-key');

        // 선택된 옵션 그룹 키를 로컬 스토리지에 저장
        localStorage.setItem('selectedOptionGroup', optionGroupKey);

        var optionsHtml = $(this).find('.option-list ul').html();
        $('h3:nth-of-type(2) span').text(optionGroupName);
        $('#optionsList').html(optionsHtml);

        // 추가된 옵션 추가 폼이 표시되도록 이벤트 바인딩
        bindAddOptionEvents(optionGroupKey);
        bindRemoveOptionEvents();
    });

    // 옵션 추가 버튼 클릭 시 옵션 추가 폼 표시
    function bindAddOptionEvents(optionGroupKey) {
        $(document).off('click', '.addOptionBtn').on('click', '.addOptionBtn', function() {
            $('.add-option-form').show();
        });

        // 옵션 추가 폼의 취소 버튼 클릭 시 폼 숨기기
        $(document).off('click', '.cancelOptionBtn').on('click', '.cancelOptionBtn', function() {
            $('.add-option-form').hide();
        });

        // 옵션 추가 폼의 저장 버튼 클릭 시 서버에 옵션 추가 요청
        $(document).off('click', '.saveOptionBtn').on('click', '.saveOptionBtn', function() {
            var addOptionForm = $(this).closest('.add-option-form');
            var optionName = addOptionForm.find('.newOptionName').val();
            var optionPrice = addOptionForm.find('.newOptionPrice').val();

            $.ajax({
                url: '/client/addOption',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    optionGroupKey: optionGroupKey,
                    optionName: optionName,
                    optionPrice: optionPrice
                }),
                success: function(data) {
                    if (data.success) {
                        alert('옵션이 추가되었습니다.');
                        // 페이지 새로고침
                        location.reload();
                    } else {
                        alert('옵션 추가에 실패했습니다.');
                    }
                },
                error: function(error) {
                    console.error('옵션 추가 중 오류 발생:', error);
                    alert('옵션 추가 중 오류가 발생했습니다.');
                }
            });
        });
    }

    // 옵션 제거 버튼 클릭 시
    function bindRemoveOptionEvents() {
        $(document).off('click', '.deleteOptionBtn').on('click', '.deleteOptionBtn', function() {
            var optionKey = $(this).data('menukey');

            if (confirm('정말 이 옵션을 삭제하시겠습니까?')) {
                $.ajax({
                    url: '/client/deleteOption',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        optionKey: optionKey
                    }),
                    success: function(response) {
                        if (response.success) {
                            // 삭제 후 옵션 목록 갱신 또는 옵션 항목 제거
                            alert('옵션이 삭제되었습니다.');
                            window.location.reload(); // 페이지 새로고침 (또는 선택한 옵션 그룹 유지)
                        } else {
                            alert('옵션 삭제에 실패했습니다.');
                        }
                    },
                    error: function(error) {
                        alert('옵션 삭제 중 오류가 발생했습니다.');
                    }
                });
            }
        });
    }

    // 초기 바인딩 호출
    var selectedOptionGroup = localStorage.getItem('selectedOptionGroup');
    if (selectedOptionGroup) {
        $('.editOptionGroupBtn[data-option-group-key="' + selectedOptionGroup + '"]').closest('.option-group-container').trigger('click');
    }
});
