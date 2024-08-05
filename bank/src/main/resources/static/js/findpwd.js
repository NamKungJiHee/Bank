const modal=document.getElementById("modalContainer");
	const confirmButton=document.getElementById("confirmButton");
	const isSuccess=document.getElementById("isSuccess").value;
  	if(isSuccess == "true"){modal.style.display = 'block';}
	confirmButton.addEventListener('click', () => {
		modal.style.display = 'none';
});