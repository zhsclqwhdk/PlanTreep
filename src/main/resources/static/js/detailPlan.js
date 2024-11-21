let planSocket = new WebSocket("/memberPlan");
planSocket.onopen = function(event) {
  console.log('memberPlan 접속');
}


planSocket.onmessage = function(event) {
  console.log('전송받은 여행일정');
  console.log(event.data);
  let receivePlan = event.data;
  console.log('receivePlan : ' + receivePlan);
  
  showToastAlert_Plan(receivePlan);
  
}

/*function searchPlan(plan){
  let resultPlan = [];
  $.ajax({
    url : '/members/searchPlan',
    type : 'get',
    data : {'planId' : plan},
    dataType : 'json',
    async : false,
    success : function(res){
      console.log(res);
      resultPlan = res;
    }    
  })
  showToastAlert_Plan(resultPlan);
}*/


/* Body에 알림 출력 DIV 생성 */
const planBodyEl = document.querySelector('body');
const planCreateDiv = document.createElement('div')
planCreateDiv.setAttribute('id', 'planToastAlert');
planBodyEl.appendChild(planCreateDiv);

function showToastAlert_Plan(plan) {
  const toastAlertEl = document.getElementById('planToastAlert');
  toastAlertEl.innerHTML =
    `<div class="toast-container position-fixed top-0 end-0 p-3" onclick="location.href='/members/sharePlan?detailId=${plan}'">
          <div id="planAlert" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header text-bg-success">
              <strong class="me-auto">일정공유 알림</strong>
              <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body">
              ${plan} 을 공유하였습니다.
            </div>
          </div>
        </div>
      `;

  const planAlert = document.getElementById('planAlert');
  const planToastBootstrap = bootstrap.Toast.getOrCreateInstance(planAlert);
  planToastBootstrap.show();
}