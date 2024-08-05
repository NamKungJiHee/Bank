document.getElementById('signupForm').addEventListener('submit', function(event){
	let isValid = true;
	const inputs = document.querySelectorAll('#signupForm .form-control');
    inputs.forEach(input => {
        if (!input.value) {
            isValid = false;
        }
    });
    if (!isValid) {
        event.preventDefault();
        document.getElementById('errorMessage').style.display = 'block';
    }
	});
	
	const userNameInput = document.getElementById('userName');
	const usernameMessageDiv = document.getElementById('usernameMessage');
	
   	userNameInput.addEventListener('input', function() {
   	const username = this.value.trim(); // 입력값에서 공백 제거
   	if (username.length > 0) {
       fetch(`/bank/checkusername?username=${username}`)
	   .then(response => {
	          return response.json(); 
	      })
	  .then(data => {
           if (data) { 
               usernameMessageDiv.style.color = 'green';
               usernameMessageDiv.textContent = '사용가능한 아이디입니다.';
           } else {
               usernameMessageDiv.style.color = 'red';
               usernameMessageDiv.textContent = '이미 사용중인 아이디입니다.';
           }
           usernameMessageDiv.style.display = 'block';
       	});
       } else {
           usernameMessageDiv.style.display = 'none';
       }
});