const cancel_elements = document.getElementsByClassName("cancel");
	Array.from(cancel_elements).forEach(function(element) {
		element.addEventListener('click', function() {
			if(confirm("정말로 취소하시겠습니까?")) {
				location.href=this.dataset.uri;
		};
	});
});