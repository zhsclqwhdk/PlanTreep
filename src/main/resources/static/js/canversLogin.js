function canversLogin() {
	let inputMidValue = document.getElementById("inputMid").value;
	let inputMpwValue = document.getElementById("inputMpw").value;

	let loginResultEl = document.getElementById("loginResultMsg");
	let ajaxResult = "";
	$.ajax({
		url: '/member/canversLogin',
		type: 'POST',
		data: { mid: inputMidValue, mpw: inputMpwValue },
		async: false,
		success: function(result) {
			console.log(result)
			ajaxResult = result;
		}
	});

	if (ajaxResult == "N") {
		loginResultEl.classList.remove('d-none');
		loginResultEl.classList.add('text-danger');
	} else {
		location.reload();
	}
}