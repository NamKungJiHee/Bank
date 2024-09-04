function checkId() {
    var fileInput = document.getElementById('chooseFile');
  
    if (fileInput.files.length > 0) {
        alert("신분증이 확인되었습니다.");
    } else {
        alert("신분증을 등록해주세요.");
    }
}

function checkAnswers() {
    var password = document.getElementById('accountpwd').value;
    var fileInput = document.getElementById('chooseFile');

    if (password === "") {
        alert("통장 비밀번호를 입력하세요.");
        return false;
    }

    if (fileInput.files.length === 0) {
        alert("신분증을 등록해주세요.");
        return false;
    }
	alert('계좌 개설이 완료되었습니다.');
	return true
}

