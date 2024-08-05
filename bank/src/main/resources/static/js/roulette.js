var button = document.getElementById("number");
	const $c = document.querySelector("canvas");
	const ctx = $c.getContext(`2d`);
	
	const product = [
	  "추가이자율 1%지급", "꽝", "아메리카노 쿠폰1장지급", "SafeLocker 첫출금시 십만원 추가지급"
	];
	
	const colors = ["#ddedf0", "#f2f2c9", "#e9f2c2", "#f6d9fc", "#60b236", "#209b6c", "#169ed8", "#3f297e", "#87207b", "#be107f", "#e7167b"];
	
	const newMake = () => {
	    const [cw, ch] = [$c.width / 2, $c.height / 2]; // center width, center height
	    const arc = Math.PI / (product.length / 2);
	  	
	    for (let i = 0; i < product.length; i++) {
	      ctx.beginPath();
	      ctx.fillStyle = colors[i % (colors.length -1)];
		  ctx.moveTo(cw, ch);
	      ctx.arc(cw, ch, cw, arc * (i - 1), arc * i);
	      ctx.fill();
	      ctx.closePath();
	    }
		
	    ctx.fillStyle = "black";
	    ctx.font = "15px Pretendard";
	    ctx.textAlign = "center";
	
	    for (let i = 0; i < product.length; i++) {
	      const angle = (arc * i) + (arc / 2);
	
	      ctx.save()  ;
	
	      ctx.translate(
	        cw + Math.cos(angle) * (cw - 50),
	        ch + Math.sin(angle) * (ch - 50),
	      );
	
	      ctx.rotate(angle + Math.PI / 2);
	
	      product[i].split(" ").forEach((text, j) => {
	        ctx.fillText(text, 0, 30 * j);
	      });
	
	      ctx.restore();
	    }
		
	}
	
	const rotate = () => {
	  $c.style.transform = `initial`;
	  $c.style.transition = `initial`;
	  
	  setTimeout(() => {
	    
	    const ran = Math.floor(Math.random() * product.length);
	
	    const arc = 360 / product.length;
	    //const rotate = (ran * arc) + 3600 + (arc * 3) - (arc/4); // 3600: 10바퀴수
		const rotate = (ran * arc) + 1.5*arc + 3600;
		
	    $c.style.transform = `rotate(-${rotate}deg)`;
	    $c.style.transition = `1.5s`;
	    
		setTimeout(() => {
                alert(`${product[ran]} 이벤트가 당첨되었습니다!!`);
                // AJAX 요청을 통해 서버로 데이터 전송
                sendDataToServer(product[ran]);
            }, 2000);
        }, 1);
    };
				
	const sendDataToServer = (selectedProduct) => {
       fetch('/bank/saveResult', {
           method: 'POST',
           headers: {
               'Content-Type': 'application/json'
           },
           body: JSON.stringify({ result: selectedProduct })
       })
       .then(response => response.json())
       .then(data => console.log(data))
       .catch(error => console.error('Error:', error));
   };
	       
	
	newMake();
	
	/* 횟수 제한 */
	number = 1;
	button.addEventListener('click', () => {
		number -= 1;

		if (number == 0) {
			button.style.display = 'none';
		}
})
	