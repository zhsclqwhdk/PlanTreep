const Today = new Date();
const today_date = Today.getFullYear() + "-" + (Today.getMonth() + 1).toString().padStart(2, '0') + "-" + Today.getDate().toString().padStart(2, '0')

let hexEl = null;
let isEventDate = false;
let result = []; // '2024-'
document.addEventListener('DOMContentLoaded', function() {
	var calendarEl = document.getElementById('calendar');
	var Calendar = FullCalendar.Calendar;
	var Draggable = FullCalendar.Draggable;
	var calendar = new Calendar(calendarEl, {
		headerToolbar: {
			left: 'prev,next,today',
			center: 'title',
			right: 'dayGridMonth'
		},
		eventOverlap: true,
		selectable: true,
		editable: true, // 이벤트 편집 가능
		droppable: true, // 드래그 앤 드롭 활성화
		eventDrop: function(info) {
			let newStartDate = info.event.start.toISOString().split("T")[0];
			if (newStartDate < today_date) {
				info.revert(); // 이벤트를 원래 위치로 되돌립니다.
				return; // 함수 종료
			}
			const dateStr = new Date();
			let titleValue = info.event._def.title;
			let startDate = info.event._instance.range.start;
			let lastDate = info.event._instance.range.end;
			var curDate = new Date(startDate);
			result.length = 0;
			while (curDate <= new Date(lastDate)) {
				result.push(curDate.toISOString().split("T")[0]);
				curDate.setDate(curDate.getDate() + 1);
			}
			viewSelectDate(result, lastDate, hexEl, titleValue);
		}
		,
		events: all,
		eventResize: function(info) {
			const dateStr = new Date();
			let titleValue = info.event._def.title;
			let startDate = info.event._instance.range.start;
			let lastDate = info.event._instance.range.end;
			var curDate = new Date(startDate);
			result.length = 0;
			while (curDate <= new Date(lastDate)) {
				result.push(curDate.toISOString().split("T")[0]);
				curDate.setDate(curDate.getDate() + 1);
			}
			viewSelectDate(result, lastDate, hexEl, titleValue);
		},

		select: function(info) {
			if (isEventDate) {
				alert('이미 선택된 날짜가 있습니다.');
				return;
			}
			const dateStr = new Date();
			if (moment(info.startStr + ' 24:00').isBefore(dateStr)) {
				return false;
			}
			var title = prompt('selected ' + info.startStr + ' to ' + info.endStr);
			if (title) {
				isEventDate = true;
				var eventData = {
					title: title,
					start: info.startStr,
					end: info.endStr
				};
				const hex = '#b2b6ef';
				hexEl = hex;
				calendar.addEvent({
					title: eventData.title,
					start: eventData.start,
					end: eventData.end,
					color: hex
				})
				let titleValue = eventData.title;
				let startDate = eventData.start;
				let lastDate = eventData.end;
				var regex = RegExp(/^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/);
				if (!(regex.test(startDate) && regex.test(lastDate))) return "Not Date Format";
				var curDate = new Date(startDate);
				result.length = 0;
				while (curDate <= new Date(lastDate)) {
					result.push(curDate.toISOString().split("T")[0]);
					curDate.setDate(curDate.getDate() + 1);
				}
				viewSelectDate(result, lastDate, hex, titleValue);
			}

		},

		eventClick: function(info) {
			for (let i = 0; i < all.length; i++) {
				if (info.event.id === 'plansId') {
					return false; // 이벤트 클릭 전파 중지
				}
			}
			if (confirm("등록된 일정을 삭제 하시겠습니까?")) {
				deleteList(info);
				info.event.remove();
				isEventDate = false;
			} else {
				alert("취소")
			}
		},
	});
	calendar.addEvent({
		start: '1945-08-15',
		end: today_date,
		display: 'background',
		color: '#4c4c4c',
		unselectAuto: true,
	});
	calendar.render();
});
function weather() {
	let lat = 37;
	let lon = 127;
	let respose = [];
	$.ajax({
		url: '/WheatherMethod',
		type: 'GET',
		data: {
			'lat': lat,
			'lon': lon
		},
		dataType: 'json',
		async: false,
		success: function(res) {
			console.log("결과값 : ");
			console.log(res);
			respose = res;
		}
	});
	return respose;
}

//let isClickEvt = false;

function viewSelectDate(res, lastDate, hex, titleValue) {
	console.log(res);
	const ListEl = document.getElementById('dateList');
	let btnEl = document.getElementById('dateBtn');
	let titleClass = document.querySelectorAll('.c' + titleValue);
	for (let tel of titleClass) {
		tel.parentElement.remove();
	}

	for (let i = 0; i < res.length - 1; i++) {


		let divEl = document.createElement('div');
		divEl.innerHTML = `<p class="fw-bold fs-5 btn w-100 text-center c${titleValue}" style="background-color: ${hex};">${res[i]}</p>`
		ListEl.appendChild(divEl);

	}


	btnEl.classList.remove('d-none');
	btnEl.setAttribute('onclick', `dateValue("${titleValue}")`);
	/*    if (!isClickEvt) {
			isClickEvt = true;
			btnEl.addEventListener('click', function() {
				dateValue(titleValue);
			})
		}*/
}

function deleteList(info) {
	let btnEl = document.getElementById('dateBtn');
	let dTitleEl = info.event._def.title;
	console.log(dTitleEl);
	if ($('#dateList').html == '') {
		btnEl.classList.add('d-none');
	}

	let selList = document.querySelectorAll('.c' + dTitleEl);
	for (let title of selList) {
		title.parentElement.remove();
	}

}
function dateValue(titleValue) {
	let seldateEls = document.querySelectorAll('.c' + titleValue);
	let dataArr = [];
	for (let el of seldateEls) {
		dataArr.push(el.innerText);
	}


	location.href = "map?result=" + dataArr + "&title=" + titleValue;
}