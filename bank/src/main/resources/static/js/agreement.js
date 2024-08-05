function checkAnswers() {
     var question1 = document.querySelector('input[name="check1"]:checked');
     var question2 = document.querySelector('input[name="check2"]:checked');
     var condition = document.querySelector('input[name="checkCondition"]:checked');

     if (question1 && question2 && condition) {
         if (question1.value === 'yes' && question2.value === 'yes') {
             window.location.href = "/bank/agreementsetting";
         } else {
             alert("모두 동의하셔야합니다.");
         }
     } else {
         alert("모두 동의하셔야합니다.");
     }
 }