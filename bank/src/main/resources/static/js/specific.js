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

function getAccountId() {
    var accountIdField = document.querySelector('input[name="accountId"]');
    return accountIdField ? accountIdField.value : null;
}

function sendKakaoMessage() {
    var accountId = getAccountId(); 
    Kakao.init("52c2ca4da9bd16808234404f2c1b87b5"); 
    Kakao.Link.sendCustom({
        templateId: 111368, 
        templateArgs: {
            'url': 'redirect.html?accountId=' + encodeURIComponent(accountId) // 해당파일은 templates이 아닌 static 안에 위치해야함.
        }
    });
}

function submitForm() {
       var form = document.getElementById('inviteMsg');
       var formData = new FormData(form);

       fetch(form.action, {
           method: 'POST',
           body: formData // hidden시킨 모든 value들이 넘어감.
       })
       .then(response => {
           if (response.ok) {
               return response.json(); 
           } else {
               throw new Error('Error');
           }
       })
       .then(data => {
           sendKakaoMessage();
       })
       .catch(error => {
           console.log('Error:', error);
       });
   }

   document.getElementById('inviteMsg').addEventListener('submit', function(event) {
       event.preventDefault(); 
       submitForm(); 
   });
   
   // 회비요청
   function requestDue() {
       Kakao.init("2bb0162fcb6b2743b0378328b1317cbb"); 
       Kakao.Link.sendCustom({
           templateId: 111796, 
		   templateArgs: {
               'url': 'bank/login'
           }
       });
   }
