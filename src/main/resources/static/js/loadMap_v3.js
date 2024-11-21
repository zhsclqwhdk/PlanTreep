// 전역 변수 선언
let onEl = null; // 현재 활성화된 요소의 ID를 저장
let placeEl = null; // 선택된 장소 요소를 저장


// 문서가 준비되면 실행되는 함수
$(document).ready(function() {
    // 날짜 버튼 클릭 이벤트 처리
    $(".datebtn").click(function(event) {
        // 클릭된 버튼이 이전에 활성화된 버튼과 다르고, 'button-addon2'가 아닌 경우
        if ($('#' + event.target.id).hasClass("Active") === false && event.target.id != 'button-addon2') {
            $('#' + event.target.id).addClass("Active");
        }
        if (onEl == null && event.target.id != 'button-addon2') {
            $('#' + event.target.id).addClass("Active");
        }
        if (event.target.id != onEl && event.target.id != 'button-addon2') {
            $('#' + onEl).removeClass("Active"); // 이전 활성화 버튼에서 'Active' 클래스 제거
            if (event.target.id != onEl && event.target.id != 'button-addon2') {

                $('#' + event.target.id).addClass("Active"); // 클릭된 버튼에 'Active' 클래스 추가
                console.log(onEl + ' re');
            }
        } else {
            // 'button-addon2'가 아닌 경우에만 'Active' 클래스 추가
            if (event.target.id != 'button-addon2' && event.target.id != onEl) {
                $('#' + event.target.id).addClass("Active");
                console.log(onEl + ' add');
            }
            if (event.target.id == onEl && event.target.id != 'button-addon2') {
                $('#' + onEl).removeClass("Active");
            }
            if ($(".s1_slide > li.list3").hasClass("on") == false && event.target.id != 'button-addon2') {
                $('#' + event.target.id).addClass("Active");
            }
        }
        // 'list3' 클래스를 가진 요소가 'on' 클래스를 가지고 있는 경우
        if ($(".s1_slide > li.list3").hasClass("on") == true) {
            if (event.target.id == onEl) {
                if (onEl != 'button-addon2') {
                    $(".s1_slide > li.list3").removeClass("on"); // 'on' 클래스 제거
                    $(".s1_slide li.list4").css('width', '1550px'); // 'list4' 너비 조정
                }
            } else {
                onEl = event.target.id; // 현재 활성화된 요소 ID 업데이트
            }
        } else {
            onEl = event.target.id; // 현재 활성화된 요소 ID 업데이트
            console.log(onEl);

            $(".s1_slide li.list4").css('width', '1170px'); // 'list4' 너비 조정
            $(".s1_slide > li.list3").addClass("on"); // 'list3'에 'on' 클래스 추가
        }
    });
});

// 마커, 노드, 정보창을 저장할 객체 선언

const NODE_MAP = {};
const MARKER_MAP = {};
const INFO_MAP = {};

// 마커를 저장할 객체
let MARKERS = {};
let HOTELMARKERS = {};
// 내일 날짜와 날짜 카운트 변수
let tomorrow_Date = null;

// 폴리라인 변수
var POLYLINE = null;

// 선택된 날짜
let selDateList = null;
// 계획 리스트
let planList = {};
let vrpList = {};
// 마커 리스트
let markerList = {};

// 시작과 끝 지점
let startPoint = {};
let endPoint = {};  // {  '2024-09-27' : node  }
let pathPoints = {};

// 추가 변수 선언
let calendarEl = []; // 캘린더 요소를 저장하는 배열
let todayLastNode = null; // 오늘의 마지막 노드를 저장
let chooseDate = document.querySelector('Active'); // 'Active' 클래스를 가진 요소 선택

// 지도 관련 변수 및 객체 선언
var mapContainer = document.getElementById('map'), // 지도를 표시할 div
    mapOption = {
        center: new kakao.maps.LatLng(35.8176, 127.1521), // 지도의 중심좌표(인천일보아카데미)
        level: 3 // 지도의 확대 레벨
    };
var map = new kakao.maps.Map(mapContainer, mapOption); // 지도 생성
function openShareList() {
    window.open('/popUpSharePlanList', 'popupplan', 'width=500, height=700')
}
//이미지 관련

$(document).ready(function() {
    $('#allCon').hide();
})

function delImg() {
    document.getElementById('mInfoImg').classList.add('d-none');
    $('#allCon').fadeIn(1000);
    if ($('#mInfoImg').hasClass('d-none')) {
        setTimeout(function() {
            if (confirm('등록된 주소를 출발지로 설정하시겠습니까?')) {
                searchPoi(loginAddr);
            }
        }, 1000)
    }
}



// POI 검색 함수
let searchTextEl = document.getElementById('inputKeyword');
searchTextEl.addEventListener('keyup', event => searchEnter(event));

function searchEnter(e) {
    if (e.key == 'Enter') {
        if (searchTextEl.value == "") {
            alert("검색어를 입력해주세요.");
            return;
        }
        $(".s1_slide > li.list3").addClass('on');
        $(".s1_slide li.list4").css('width', '1170px');
        searchPoi()
    }
}
function searchPoi(addr) {
    if (addr == null) {
        var latlng = map.getCenter();
        const x = latlng.getLng();
        const y = latlng.getLat();
        let inputKeywordVal = document.getElementById('inputKeyword').value;
        loadingOn();
        $.ajax({
            url: '/travelMap/poi',
            data: {
                x: x,
                y: y,
                keyword: inputKeywordVal
            },
            success: function(result) {
                if (result.code != 'SUCC') {
                    alert("검색에 실패 하였습니다.");
                    return;
                }
                const data = result.data;
                displayData(data);
                loadingOff()
            },
            error: function() {
                alert("검색에 실패 하였습니다.");
                loadingOff()
            }
        });
    } else {
        console.log("출발지 검색");
        $.ajax({
            url: '/travelMap/poi',
            data: {
                x: loginX,
                y: loginY,
                keyword: addr
            },
            success: function(result) {
                if (result.code != 'SUCC') {
                    alert("검색에 실패 하였습니다.");
                    return;
                }
                let data = result.data;
                let start = document.getElementById('dateList');
                let starts = [];
                starts = start.getElementsByTagName('p');
                starts[0].click();
                displayData(data);
                loadingOff()
            },
            error: function() {
                alert("검색에 실패 하였습니다.");
                loadingOff()
            }
        });
    }

}
// 날짜 선택 함수
function selDate(date) {
    console.log('selDate(date) : ' + date);

    // 기존 폴리라인 제거
    if (POLYLINE) {
        POLYLINE.setMap(null);
        POLYLINE = null;
    }

    // 삭제할 마커 목록
    let deleteMarkerList = MARKERS[selDateList];
    if (deleteMarkerList != undefined) {
        for (let delMarker of deleteMarkerList) {
            delMarker.setMap(null);
        }
    }

    // 모든 호텔 마커 숨기기
    for (let key in HOTELMARKERS) {
        if (HOTELMARKERS[key] != undefined) {
            HOTELMARKERS[key].setMap(null);
        }
    }

    // 선택한 날짜의 호텔 마커만 표시
    if (HOTELMARKERS[date] != undefined) {
        HOTELMARKERS[date].setMap(map);
    }

    // 출력할 마커 목록
    let addMarkerList = MARKERS[date];
    if (addMarkerList != undefined) {
        for (let addMarker of addMarkerList) {
            addMarker.setMap(map);
        }
    }

    selDateList = date;
    let inOutDate = document.getElementById('b' + date);
    inOutDate.innerHTML = ``;

    // 출발지: 이전 날짜의 마지막 도착지점 설정
    if (date !== dateArr[0]) {
        let prevDate = new Date(date);
        prevDate.setDate(prevDate.getDate() - 1);
        let prevDateString = prevDate.toISOString().split('T')[0]; // 'YYYY-MM-DD' 형식으로 변환
        let lastEndNode = endPoint[prevDateString];

        if (lastEndNode != undefined) {
            let startDiv = document.createElement('div');
            startDiv.classList.add('List', 'btn', 'w-100', 'btn-dark', 'mt-2', 'fw-bold');
            startDiv.setAttribute('id', `n${lastEndNode.id}`);
            startDiv.setAttribute('data-Nid', `${lastEndNode.categoryGroupName}`);
            startDiv.innerHTML = `<div data-x="${lastEndNode.x}" data-y="${lastEndNode.y}" onclick="deletePlace(${lastEndNode.id},this)">${lastEndNode.name}</div>`;
            inOutDate.appendChild(startDiv);

            // 출발지 마커 추가
            const startPosition = new kakao.maps.LatLng(lastEndNode.y, lastEndNode.x);
            const startMarker = new kakao.maps.Marker({ position: startPosition, clickable: true });
            startMarker.setMap(map);
            MARKERS[date] = MARKERS[date] || [];
            MARKERS[date].push(startMarker);

        }
    }
    if (vrpList[date] != null) {
        for (let node of vrpList[date]) {
            if (node.categoryGroupName == '숙박') {
                continue;
            }
            let dateList = document.createElement('div');
            dateList.classList.add('List', 'btn', 'w-100', 'btn-dark', 'mt-2', 'fw-bold');
            dateList.setAttribute('id', `n${node.id}`);
            dateList.setAttribute('data-Nid', `${node.categoryGroupName}`);
            dateList.innerHTML = `<div data-x="${node.x}" data-y="${node.y}" onclick="deletePlace(${node.id},this)" >${node.name}</div>`;
            inOutDate.appendChild(dateList);
            var moveLatLon = new kakao.maps.LatLng(node.y, node.x);
            map.panTo(moveLatLon);
        }

    }

    // 경로 최적화 버튼 추가
    let selectPath = document.getElementById('dateAccordion');
    let vrpDiv = document.createElement('div');
    if (document.getElementById('govrp')) {
        document.getElementById('govrp').remove();
    }
    if (document.getElementsByClassName("List").length >= 2) {
        vrpDiv.innerHTML = `<button type="button" id="govrp" class="btn btn-info w-100 mt-2 fw-bold" onclick="goVrp()">경로 최적화</button>`;
        selectPath.appendChild(vrpDiv);
    }

    // 저장 버튼 추가
    let selectplace = document.querySelectorAll('.placeBox');
    let isSaveBtn = true;
    for (let p of selectplace) {
        if (p.childElementCount == 0) {
            isSaveBtn = false;
        }
    }
    if (isSaveBtn) {
        let savePath = document.getElementById('dateAccordion');
        let saveDiv = document.createElement('div');
        if (vrpList[selDateList] != undefined) {
            if (document.getElementById('save')) {
                document.getElementById('save').remove();
            }
            if (document.getElementsByClassName("List").length >= 2) {
                saveDiv.innerHTML = `<button type="button" id="save" class="btn btn-info w-100 mt-2 fw-bold" onclick="realsave()">저장하기</button>`;
                savePath.appendChild(saveDiv);
            }
        }
    }

    // 도착지
    let endNode = endPoint[date];
    if (endNode != undefined) {
        console.log("endnode 있어요!");
        let endDiv = document.createElement('div');
        endDiv.classList.add('List', 'btn', 'w-100', 'btn-dark', 'mt-2', 'fw-bold');
        endDiv.setAttribute('id', `n${endNode.id}`);
        endDiv.setAttribute('data-Nid', `${endNode.categoryGroupName}`);
        endDiv.innerHTML = `<div data-x="${endNode.x}" data-y="${endNode.y}" onclick="deletePlace(${endNode.id},this)">${endNode.name}</div>`;
        inOutDate.appendChild(endDiv);
    }

    /* 경로 */
    console.log("pathPoints[selDateList]");
    console.log(pathPoints[selDateList]);
    if (pathPoints[selDateList] != undefined) {
        drawPath(pathPoints[selDateList]);
    }
    console.log(planList[selDateList]);

}
// 검색 결과 표시 함수

function displayData(data) {
    console.log(data);
    const nodeList = data.nodeList; // 방문지목록
    var bounds = new kakao.maps.LatLngBounds();
    const nodeList_parent = document.getElementById('nodeList');
    nodeList_parent.innerHTML = ``;
    let nodeList_div = document.createElement('div');
    nodeList_div.classList.add('list-group');
    let phoneNo = "";

    for (let node of nodeList) {
        if (node.phone == null) {
            phoneNo = "(번호없음)";
        } else {
            phoneNo = node.phone;
        }
        let listItem_div = document.createElement('div');
        listItem_div.classList.add('list-group-item', 'list-group-item-action', 'place');
        listItem_div.setAttribute("id", `${node.name}`)
        listItem_div.innerHTML = `
                 <div class="mt-3 small">
                     <div class="d-flex w-100 align-items-center justify-content-start mb-1">
                         <strong class="ms-1">${node.name}</strong>
                     </div>
                  <div class="d-flex align-items-center justify-content-between">
                       <div class="d-flex align-items-center"><span class="badge text-bg-light border border-secondary-subtle me-2">주소</span> <span>${node.address}</span></div>
                       <div class="d-flex align-items-center"><span class="badge text-bg-light border 
                       border-secondary-subtle me-2">전화</span> <span>${phoneNo}</span></div>
                       <div class="d-flex align-items-center">
                  </div>
                 </div>
                 `;
        nodeList_div.appendChild(listItem_div);
        listItem_div.addEventListener('click', function() {
            selectPlace(node);
        })
        const position = new kakao.maps.LatLng(node.y, node.x);
        bounds.extend(position);
    }
    nodeList_parent.appendChild(nodeList_div);
    if (nodeList.length > 0) {
        map.setBounds(bounds);
    }
    $(".place").click(function(event) {
        $('#' + event.target.id).addClass('NodeActive');
    });
}
// 장소 선택 함수
function selectPlace(node) {
    let x = node.x;
    let y = node.y;
    //document.getElementById(`${node.name}`).classList.add('Active');
    console.log(x, y);
    if (selDateList == null) {
        alert('날짜를 선택해주세요!');
        return;
    }

    // 플랜 추가 기능 호출
    addPlan(node);
    // 출력 기능 호출
    selDate(selDateList);
    // 마커 등록 기능 호출
    addMarker(node);


}
// 마커 추가 함수
function addMarker(node) {
    const position = new kakao.maps.LatLng(node.y, node.x);
    // 마커를 지도에 추가
    const marker = new kakao.maps.Marker({ position: position, clickable: true });
    marker.id = node.id;

    // 선택된 날짜의 마커 배열에 추가
    if (node.categoryGroupName === '숙박') {
        if (HOTELMARKERS[selDateList]) {
            HOTELMARKERS[selDateList].setMap(null);

        }
        HOTELMARKERS[selDateList] = marker;
        HOTELMARKERS[selDateList].setMap(map);

    } else {
        let markerByDate = MARKERS[selDateList]
        if (markerByDate == undefined) {
            markerByDate = [];
        }
        markerByDate.push(marker);
        MARKERS[selDateList] = markerByDate;

        let addMarkerList = MARKERS[selDateList];
        if (addMarkerList != undefined) {
            for (let addMarker of addMarkerList) {
                addMarker.setMap(map);
            }
        }
    }

    const name = node.name;
    let phone = "";
    if (node.phone == null) {
        phone = "(번호없음)";
    } else {
        phone = node.phone;
    }
    // 인포윈도우 생성
    const infowindow = new kakao.maps.InfoWindow({
        content: '<div style="padding:5px;">' + name + '<br/>' + phone + '</div>',
        removable: true
    });
    // 마커에 클릭 이벤트 등록
    kakao.maps.event.addListener(marker, 'click', function() {
        infowindow.open(map, marker);
    });
    MARKER_MAP[node.id] = marker;
    INFO_MAP[node.id] = infowindow;
}
// 플랜 추가 함수
function addPlan(node) {
    console.log("addPlan 실행");
    // 이미 존재하는 장소인지 확인
    let bdiv = document.querySelector(`#b${selDateList}`);
    console.log(bdiv);
    if (bdiv.querySelector(`#n${node.id}`) != null) {
        if (node.categoryGroupName == '숙박') {
            endPoint[selDateList] = node;
            let today = new Date(selDateList);
            var year = today.getFullYear();
            var month = ("0" + (1 + today.getMonth())).slice(-2);
            var day = ("0" + (1 + today.getDate())).slice(-2);
            let tomorrow = year + "-" + month + "-" + day;
            tomorrow_Date = tomorrow;
            startPoint[tomorrow] = node;
            console.log("다음날 시작지점 노드");
            console.log(startPoint[tomorrow]);
            return;
        }
        return;
    }
    if (node.categoryGroupName == '숙박') {
        endPoint[selDateList] = node;
        let today = new Date(selDateList);
        var year = today.getFullYear();
        var month = ("0" + (1 + today.getMonth())).slice(-2);
        var day = ("0" + (1 + today.getDate())).slice(-2);
        let tomorrow = year + "-" + month + "-" + day;
        tomorrow_Date = tomorrow;
        startPoint[tomorrow] = node;
        console.log("다음날 시작지점 노드");
        console.log(startPoint[tomorrow]);
        return;
    }


    let checkPlan = vrpList[selDateList];
    console.log("addplan");
    console.log(endPoint[selDateList]);
    if (checkPlan == undefined) {
        vrpList[selDateList] = [];
    }
    NODE_MAP[node.id] = node;
    vrpList[selDateList].push(node);

}

// 장소 삭제 함수
function deletePlace(nodeId, deleteEl) {
    if (POLYLINE) {
        POLYLINE.setMap(null);
        POLYLINE = null;
    }
    deleteEl.parentElement.remove();

    if (startPoint[selDateList] != undefined) {
        startdelete = startPoint[selDateList];
        if (startdelete.id == nodeId) {
            startPoint[selDateList] = null;
        }
    }
    if (endPoint[selDateList] != undefined) {
        enddelete = endPoint[selDateList];
        if (enddelete.id == nodeId) {
            endPoint[selDateList] = null;
        }
    }
    let index = -1;
    let i = 0;
    let deleteMarker = null;
    if (planList[selDateList] != undefined) {
        for (let plan of planList[selDateList]) {
            console.log("planList");
            if (plan.id == nodeId) {
                index = i
                break;
            }
            i++;
        }
        planList[selDateList].splice(index, 1);
    }
    if (Array.isArray(vrpList[selDateList]) && vrpList[selDateList].length === 1) {
        vrpList[selDateList] = null;
    } else {
        index = -1;
        if (Array.isArray(vrpList[selDateList])) {
            for (let i = 0; i < vrpList[selDateList].length; i++) {
                if (vrpList[selDateList][i].id === nodeId) {
                    index = i;
                    break;
                }
            }

            // 찾은 인덱스가 유효한 경우 요소 제거
            if (index !== -1) {
                vrpList[selDateList].splice(index, 1);
            }
        }
    }
    index = -1;
    deleteMarker = null;
    if (Array.isArray(MARKERS[selDateList])) {

        for (let i = 0; i < MARKERS[selDateList].length; i++) {
            let marker = MARKERS[selDateList][i];
            if (marker.id == nodeId) {
                index = i;
                deleteMarker = marker;
                break;
            }
        }

        if (deleteMarker) {
            deleteMarker.setMap(null);
            MARKERS[selDateList].splice(index, 1);

            if (MARKER_MAP[nodeId]) {
                MARKER_MAP[nodeId].setMap(null);
                MARKER_MAP[nodeId] = null;
            }

            if (INFO_MAP[nodeId]) {
                INFO_MAP[nodeId].close();
                INFO_MAP[nodeId] = null;
            }
            delete MARKER_MAP[nodeId];
            delete INFO_MAP[nodeId];
            delete NODE_MAP[nodeId];
        }
    }
    if (HOTELMARKERS[selDateList] && HOTELMARKERS[selDateList].id == nodeId) {
        HOTELMARKERS[selDateList].setMap(null);
    }
    // 경로 최적화 버튼 삭제
    let deletePath = document.getElementsByClassName("List");

    console.log(deletePath);
    console.log("버튼삭제 1");

    // deletePath의 길이가 0이면
    if (deletePath.length === 1) {
        console.log("버튼삭제 2");
        $("#govrp").remove();
        $("#save").remove();
    }

}
// 로딩 화면 표시 함수
function loadingOn() {
    document.getElementById('spin').style.display = 'flex';
}

// 로딩 화면 숨김 함수
function loadingOff() {
    document.getElementById('spin').style.display = 'none';
}

// VRP(Vehicle Routing Problem) 실행 함수
function goVrp() {
    console.log("goVrp 호출");
    console.log(planList);
    console.log(selDateList);
    let lastIndex = vrpList[selDateList].length - 1;
    if (endPoint[selDateList] != null) {
        if (vrpList[selDateList][lastIndex].categoryGroupName == '숙박') {
            vrpList[selDateList][lastIndex] = endPoint[selDateList];
        } else {
            vrpList[selDateList].push(endPoint[selDateList]);
        }
    }
    if (startPoint[selDateList] != null) {
        vrpList[selDateList].unshift(startPoint[selDateList]);
    }
    loadingOn();
    $.ajax({
        url: '/travelMap/vrp',
        type: 'post',
        contentType: "application/json",
        data: JSON.stringify(Object.values(vrpList[selDateList])),
        success: function(result) {
            if (result.code != 'SUCC') {
                alert("경로 최적화에 실패 하였습니다.");
                loadingOff();
                return;
            }
            const data = result.data;
            planList[selDateList] = data.nodeList;
            console.log("최적화된 경로 결과");
            pathPoints[selDateList] = data.totalPathPointList;
            selDate(selDateList);
            loadingOff();
        },
        error: function() {
            alert("경로 최적화에 실패 하였습니다.");
            loadingOff();
        }
    });
}
// 경로 그리기 함수
function drawPath(nodeList) {
    console.log("drawPath 호출");
    console.log(nodeList);
    if (nodeList.length > 0) {
        // 선을 구성하는 좌표 배열입니다. 이 좌표들을 이어서 선을 표시합니다
        var linePath = [];
        for (var point of nodeList) {
            linePath.push(new kakao.maps.LatLng(point.y, point.x));
        }
        // 지도에 표시할 선을 생성합니다
        POLYLINE = new kakao.maps.Polyline({
            path: linePath, // 선을 구성하는 좌표배열 입니다
            strokeWeight: 5, // 선의 두께 입니다
            strokeColor: "red", // 선의 색깔입니다
            strokeOpacity: 0.7, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
            strokeStyle: "solid", // 선의 스타일입니다
        });
        // 지도에 선을 표시합니다
        POLYLINE.setMap(map);
    }
}
let j = 1;

function realsave() {
    let dates = document.getElementsByClassName("datebtn");
    if (j < dates.length) {
        
        $(dates[j]).click();
        
        save().then(() => {
            j++; 
            realsave(); 
        }).catch(error => {
            console.error(error);
            loadingOff();
        });
    } else {
        
        location.href = "sharePlanToMember?mid=" + loginId + "&planid=" + planid;
    }
}
// 저장하기
let lastIndex = null;
function save() {
    return new Promise((resolve, reject) => {
        console.log("save 호출");
        lastIndex = vrpList[selDateList].length - 1;
        if (endPoint[selDateList] != null) {
            if (vrpList[selDateList][lastIndex].categoryGroupName == '숙박') {
                vrpList[selDateList][lastIndex] = endPoint[selDateList];
            } else {
                vrpList[selDateList].push(endPoint[selDateList]);
            }
        }
        if (startPoint[selDateList] != null) {
            if (startPoint[selDateList] == vrpList[selDateList][lastIndex]) {
                vrpList[selDateList].splice(lastIndex, 1);
            }
            vrpList[selDateList].unshift(startPoint[selDateList]);
        }
        loadingOn();
        $.ajax({
            url: '/travelMap/vrp',
            type: 'post',
            contentType: "application/json",
            data: JSON.stringify(Object.values(vrpList[selDateList])),
            success: function(result) {
                if (result.code != 'SUCC') {
                    alert("경로 최적화에 실패 하였습니다.");
                    loadingOff();
                    return;
                }
                const data = result.data;
                planList[selDateList] = data.nodeList;
                pathPoints[selDateList] = data.totalPathPointList;
                loadingOff();
                let requestData = { 'detailDate': selDateList, 'planList': planList[selDateList], 'pathPoints': pathPoints[selDateList] };
                $.ajax({
                    url: '/travelMap/save',
                    type: 'post',
                    contentType: "application/json",
                    data: JSON.stringify(requestData),
                    async: false,
                    success: function(res) {
                        resolve(); 
                    },
                    error: function() {
                        alert("경로 최적화에 실패 하였습니다.");
                        loadingOff();
                        reject("Route optimization failed");
                    }
                });
            },
            error: function() {
                alert("경로 최적화에 실패 하였습니다.");
                loadingOff();
                reject("Route optimization failed");
            }
        });
    });
}