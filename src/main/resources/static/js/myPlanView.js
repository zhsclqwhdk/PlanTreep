$(document).ready(function() {

	ReviewCheck();

});

let planListEls = document.querySelectorAll('#myPlan .planBtn');
console.log('planListEls');
console.log(planListEls);

let pid = null;
for (let plan of planListEls) {
	plan.addEventListener('click', function(evt) {
		plan.classList.add('btn-warning');
		pid = evt.target.id;
		console.log(pid);
	})
}

// 검색한 회원 찾기
// 로그인한 회원 아이디

console.log(loginMidEl)
function searchMem() {
	let searchMemNick = document.getElementById('searchMemNick');
	let searchMemNickVal = searchMemNick.value;
	console.log('searchMemIdVal : ' + searchMemNickVal);
	if (searchMemNickVal.length == 0) {
		alert('닉네임를 입력해주세요!');
		searchMemNick.focus();
		return false;
	}
	$.ajax({
		url: '/searchMember',
		type: 'get',
		data: { 'mnickname': searchMemNickVal },
		dataType: 'json',
		async: false,
		success: function(result) {
			let resultMnickname = [];
			let resultMid = [];
			for (let res of result) {
				// 로그인한 회원 ID와 다른 경우에만 추가 & resultMid에 포함되어 있지 않은 res.mid를 추가
				if (loginMidEl != res.mnickname && !resultMnickname.includes(res.mnickname)) {
					resultMnickname.push(res.mnickname);
					resultMid.push(res.mid)
				}
			}
			console.log("resultMid");
			if(resultMnickname.length == 0){
				alert('회원 검색에 실패했습니다. 다시 시도해 주세요.');
				location.reload(true);
			}
			if (resultMnickname != null) {
			console.log(resultMnickname);
				resultMemberView(resultMnickname,resultMid);
			}
			
		}
		/*error: function() {
			alert('회원 검색에 실패했습니다. 다시 시도해 주세요.');
		}*/
	})
}
//
/**/
//<button class="btn btn-warning shwMemBtn" onclick="selectMid(${mid})">${mid}</button>
function resultMemberView(nicknameList,memidList) {
	let showMemberDiv = document.getElementById('showMember');
	showMemberDiv.innerHTML = ``;
	for (let i = 0; i < nicknameList.length; i++) {
		let nickname = nicknameList[i];
	    let memid = memidList[i];
		let createDiv = document.createElement('div');
		createDiv.innerHTML =
			`
			<input type="radio" class="btn-check checkMemBtn" id="${memid}" name="mid" value="${memid}" autocomplete="off">
            <label class="btn btn-outline-success mt-3"  for="${memid}" id="my_btn"  onclick="selectMid('${memid}')">${nickname}</label><br>
            `;
		showMemberDiv.appendChild(createDiv);
	}
}

// 검색된 회원 중 한 명을 선택
let sharePlanIdList = [];
function selectMid(mid) {
	console.log("보낼 mid : "+ mid)
	let myPlanIdEls = document.querySelectorAll('.myPlanId');
	let planIdList = [];
	for (let myPlanId of myPlanIdEls) {
		planIdList.push(myPlanId.innerText);
	}
	console.log(planIdList[0]);

	// 검색된 회원 목록
	let btnList = document.querySelectorAll('.checkPlanBtn');
	let labelList = document.querySelectorAll('.shareLabel');
	let shareButton = document.getElementById("shareButton");
	// 공유하기 랑 공유 버튼 활성화
	shareButton.disabled = false;
	for (let btn of btnList) {
		btn.disabled = false;
	}
	for (let labelEl of labelList) {
		labelEl.innerText = '공유';
		labelEl.classList.remove('btn-outline-secondary');
		labelEl.classList.add('btn-outline-primary');
	}


	$.ajax({
		url: '/shareCheckByReceiverId',
		type: 'get',
		traditional: true,
		data: { 'receiveMemberId': mid },
		dataType: 'json',
		async: false,
		success: function(res) {
			console.log(res);
			sharePlanIdList = res;
		},
		error: function(err) {
			console.error("오류 발생:", err);
		}
	});


	//let labelEl = document.getElementById('shareBtn');
	for (let sharePlanId of sharePlanIdList) {
		for (let btn of btnList) {
			let btnVal = btn.value;
			if (btnVal.includes(sharePlanId)) {
				btn.disabled = true;
				// btn(input 태그)의 다음 요소
				btn.nextElementSibling.innerText = '완료';
				btn.nextElementSibling.classList.remove('btn-outline-primary');
				btn.nextElementSibling.classList.add('btn-outline-secondary');
			}
		}
	}

}


/* 공유되었던 일정이 있는지 확인하는 기능 */

// 1. 공유할 회원 mid로 List<shareContent>를 찾아
// 2. 공유하고자 하는 planId가 List<shareContent>에 있는 planId가 일치하는지 확인

function shareCheck(planId) {
	// 선택된 MEMBER MID
	let checkPlanList = document.querySelectorAll('.checkMemBtn');
	if (!(Array.from(checkPlanList).some(btn => btn.checked))) {
		alert("공유할 회원을 선택해주세요")
		// 공유하기 랑 공유 버튼 비활성화
		let shareButton = document.getElementById("shareButton");
		shareButton.disabled = true;

		let btnList = document.querySelectorAll('.checkPlanBtn');
		for (let btn of btnList) {
			btn.checked = false;
			btn.disabled = true;
		}
	}
	if (!planId) {
		let checkMember = document.querySelector('.checkMemBtn').value;
		console.log(checkMember);
	}
}

// 일정 공유(회원 선택 & 일정 선택 후 공유 버튼 클릭하면 실행되는 기능)
function sharePlanToMember() {
	// 선택된 MEMBER MID
	if (document.querySelector('.checkMemBtn') == null) {
		alert("공유할 회원을 선택해주세요")
		return;
	}
	let checkMember = document.querySelector('.checkMemBtn').value;
	console.log(checkMember);
	let formObj = document.createElement('form');
	formObj.action = '/sharePlanToMember';
	formObj.method = 'get';
	formObj.innerHTML = `<input type="text" name="mid" value="${checkMember}">`;

	// location.href = "/members/sharePlanToMember?mid=" + resultMid + "&pid=" + pid;

	// 선택가능한 input:checkbox의 전체목록
	let checkPlanList = document.querySelectorAll('.checkPlanBtn');
	if (!(Array.from(checkPlanList).some(btn => btn.checked))) {
		// 클릭이 안 되어 있는 버튼이 있는 경우
		alert("공유 버튼이 클릭 되어있지 않습니다")
		return; // 함수 종료
	}

	console.log('checkPlanList : ' + checkPlanList);
	for (let checkPlan of checkPlanList) {
		if (checkPlan.checked) {
			formObj.appendChild(checkPlan);
		}
	}
	document.querySelector('body').appendChild(formObj);
	console.log(formObj);
	formObj.submit();
}

// 후기 작성된거 작성완료로 바꾸고 비활성화
function ReviewCheck() {
	let reviewList = document.querySelectorAll(".reviewBtn")
	$.ajax({
		url: '/boards/ReviewCheck',
		type: 'get',
		dataType: 'json',
		async: false,
		success: function(res) {
			console.log("res")
			console.log(res)
			let planIdArr = res; // 배열 [3,2]
			for (let i = 0; i < reviewList.length; i++) {
				// reviewList[i].dataset.planid >> 3
				if (planIdArr.includes(Number(reviewList[i].dataset.planid))) {
					reviewList[i].classList.remove('reviewBtn', 'btn-outline-danger');
					reviewList[i].classList.add('btn-outline-primary');
					reviewList[i].setAttribute("disabled", true);
					reviewList[i].innerText = "작성완료";
				}
			}
		}
	})
}
function confirmCheck(id) {

	if (confirm("이미지 등록 하시겠습니까??")) {
		window.open("boards/popUpReview?id=" + id, "ImageReview", "width=500,height=600");
	} else {
		alert("취소!");
		openWin = window.open("boards/popUpReview?id=" + id, "Review", "width=500,height=600");
		openWin.onload = function() {
			const element = openWin.document.getElementById('file');
			if (element) {
				element.style.display="none";
			}
		}
	}


}
/*function cancel() {
	var el = document.getElementById('file');
	el.style.visibility.hidden;
}*/
function pageReload() {
	location.reload();
}