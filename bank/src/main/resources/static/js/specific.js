document.addEventListener('DOMContentLoaded', function() {
    var memberCheck = document.getElementById('memberCheck');
    var transactionAccount = document.getElementById('transactionAccount');
	
    var memberInfo = document.getElementById('memberInfo');
    var transactionInfo = document.getElementById('transactionInfo');

    function activateMenuItem(activeItem, activeInfo) {
        var menuItems = document.querySelectorAll('.menu-item');
        var infoItems = document.querySelectorAll('.container > div:not(.menu-item)');
		
		// 모든 메뉴 항목들을 unselected, inactive하게 만들기
        menuItems.forEach(function(item) {
            item.classList.remove('selected');
            item.classList.add('unselected');
        });

        infoItems.forEach(function(info) {
            info.classList.remove('active');
            info.classList.add('inactive');
        });

        // 선택한 메뉴 항목들만 selected, active하게 만들기
        activeItem.classList.remove('unselected');
        activeItem.classList.add('selected');
        activeInfo.classList.remove('inactive');
        activeInfo.classList.add('active');
    }

    memberCheck.addEventListener('click', function() {
        activateMenuItem(memberCheck, memberInfo);
    });

    transactionAccount.addEventListener('click', function() {
        activateMenuItem(transactionAccount, transactionInfo);
    });
    
});