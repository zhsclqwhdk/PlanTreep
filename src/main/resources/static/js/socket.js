// SOCKET
let planSocket = new WebSocket("/sharePlan");
planSocket.onopen = function(event) {
  console.log('memberPlan 접속');
}

// 전송받은 여행일정
planSocket.onmessage = function(event) {
  let receivePlanList = JSON.parse(event.data);
  console.log('확인 하지 않은 공유내역 : ' + receivePlanList.unchecksize);
  let alertBtn = document.getElementById('alertBtn');
  let sharedPlanBtn = document.getElementById('sharedPlan');
  alertBtn.classList.remove('d-none');
  sharedPlanBtn.classList.remove('d-none');
  sharedPlanBtn.innerText = receivePlanList.unchecksize;
  showToastAlert_Plan(receivePlanList);
}

/* Body에 알림 출력 DIV 생성 */
const planBodyEl = document.querySelector('body');
const planCreateDiv = document.createElement('div')
planCreateDiv.setAttribute('id', 'planToastAlert');
planBodyEl.appendChild(planCreateDiv);

function showToastAlert_Plan(plan) {
  const toastAlertEl = document.getElementById('planToastAlert');//   '[1,25,3]'
  toastAlertEl.innerHTML =
    `<div class="toast-container position-fixed top-0 end-0 p-3">
          <div id="planAlert" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header text-bg-success">
              <strong class="me-auto">일정공유 알림</strong>
              <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body">
            <div class="text-center mb-2 fw-bold" style="font-family-Jost">${plan.sendMid} 님이 일정을 공유하였습니다.</div>
            <div class="justify-content-center d-flex"><a href="/viewPlan" class="btn btn-primary">확인</a></div>
            </div>
          </div>
      </div>
      `;

  const planAlert = document.getElementById('planAlert');
  const planToastBootstrap = bootstrap.Toast.getOrCreateInstance(planAlert);
  planToastBootstrap.show();
}



/* function receivePlanView(planidList, sendMid) {
  let pid_Json = JSON.parse(planidList)
  console.log('planid : ' + pid_Json);//
  let formObj = document.createElement('form');
  formObj.action = '/receiveShareContents';
  formObj.method = 'get';
  //formObj.innerHTML = `<input type="text" name="sendMid" value="${sendMid}">`;

  // 1. 선택가능한 input:checkbox의 전체목록
  //let receivePlanList = plan.detailListId;
  for (let planid of pid_Json) {
    let planInput = document.createElement('input');
    planInput.setAttribute('name', 'receivePlan');
    planInput.value = planid;
    formObj.appendChild(planInput);
  }
  console.log("formObj : " + formObj)
  document.querySelector('body').appendChild(formObj);
  formObj.submit();
} */