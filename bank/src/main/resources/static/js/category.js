document.addEventListener('DOMContentLoaded', function() {
    var accountCheck = document.getElementById('accountCheck');
    var sendAccount = document.getElementById('sendAccount');
    var event = document.getElementById('event');
	var groupAccount = document.getElementById('groupAccount');
	var checkAccount = document.getElementById('checkAccount');
	
    var accountInfo = document.getElementById('accountInfo');
    var sendInfo = document.getElementById('sendInfo');
    var eventInfo = document.getElementById('eventInfo');
	var groupInfo = document.getElementById('groupInfo');
	var checkInfo = document.getElementById('checkInfo');

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

    accountCheck.addEventListener('click', function() {
        activateMenuItem(accountCheck, accountInfo);
    });

    sendAccount.addEventListener('click', function() {
        activateMenuItem(sendAccount, sendInfo);
    });
    
    event.addEventListener('click', function() {
        activateMenuItem(event, eventInfo);
    });
	
	groupAccount.addEventListener('click', function() {
		activateMenuItem(groupAccount, groupInfo);
	})
	
	checkAccount.addEventListener('click', function() {
		activateMenuItem(checkAccount, checkInfo);
	})
});