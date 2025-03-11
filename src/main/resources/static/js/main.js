const API_BASE_URL = 'http://localhost:8080';

// 날짜 포맷팅 함수
function formatDate(date) {
  return new Date(date).toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}

// 시간 포맷팅 함수
function formatTime(time) {
  return time.substring(0, 5);
}

// 회의실 목록을 가져오는 함수
async function loadRooms(retryCount = 0) {
  try {
    console.log("회의실 목록 조회 시작");
    const response = await fetch(`${API_BASE_URL}/rooms`, {
      headers: {
        Accept: "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`서버 오류: ${response.status} ${response.statusText}`);
    }

    const rooms = await response.json();
    console.log("회의실 목록 조회 성공:", rooms);

    if (!Array.isArray(rooms)) {
      throw new Error("서버 응답이 배열 형식이 아닙니다");
    }

    if (rooms.length === 0) {
      console.warn("사용 가능한 회의실이 없습니다");
    }

    displayRooms(rooms);
  } catch (error) {
    console.error("회의실 목록 조회 오류:", error);

    // 재시도 로직 (3회까지)
    if (retryCount < 3) {
      console.log(`회의실 목록 재조회 시도 (${retryCount + 1}/3)...`);
      setTimeout(() => loadRooms(retryCount + 1), 1000 * (retryCount + 1));
      return;
    }

    const roomList = document.getElementById("roomList");
    if (roomList) {
      roomList.innerHTML = `
        <div class="alert alert-danger" role="alert">
          회의실 목록을 불러오는데 실패했습니다.<br>
          잠시 후 다시 시도해주세요.
        </div>
      `;
    }
  }
}

// 서버에서 팀 목록을 가져오는 함수
async function loadTeams(retryCount = 0) {
  try {
    console.log("팀 목록 조회 시작");
    const response = await fetch(`${API_BASE_URL}/teams`);

    if (!response.ok) {
      throw new Error(`서버 오류: ${response.status} ${response.statusText}`);
    }

    const teams = await response.json();
    console.log("팀 목록 조회 성공:", teams);

    if (!Array.isArray(teams)) {
      throw new Error("서버에서 받은 데이터가 유효하지 않습니다");
    }

    const teamSelect = document.getElementById("teamId");
    teamSelect.innerHTML = '<option value="">팀을 선택하세요</option>';

    if (teams.length === 0) {
      teamSelect.innerHTML += `<option value="" disabled>등록된 팀이 없습니다</option>`;
    } else {
      teams.forEach((team) => {
        if (!team.id || !team.teamName) {
          console.warn("유효하지 않은 팀 정보:", team);
          return;
        }
        teamSelect.innerHTML += `<option value="${team.id}">${team.teamName}</option>`;
      });
    }
  } catch (error) {
    console.error("팀 목록 조회 오류:", error);

    // 재시도 로직 (3회까지)
    if (retryCount < 3) {
      console.log(`팀 목록 재조회 시도 (${retryCount + 1}/3)...`);
      setTimeout(() => loadTeams(retryCount + 1), 1000 * (retryCount + 1));
      return;
    }

    alert(
      "팀 목록을 불러오는데 실패했습니다.\n\n새로고침하여 다시 시도해주세요."
    );
  }
}

// 회의실 목록을 화면에 표시하는 함수
function displayRooms(rooms) {
  const roomList = document.getElementById("roomList");
  if (!roomList) {
    console.error("roomList 엘리먼트를 찾을 수 없습니다.");
    return;
  }

  roomList.innerHTML = "";

  if (!Array.isArray(rooms) || rooms.length === 0) {
    roomList.innerHTML = `
      <div class="alert alert-info" role="alert">
        사용 가능한 회의실이 없습니다.
      </div>
    `;
    return;
  }

  rooms.forEach((room) => {
    if (!room || (!room.name && !room.roomName)) {
      console.warn("유효하지 않은 회의실 정보:", room);
      return;
    }

    const roomContainer = document.createElement("div");
    roomContainer.className = "mb-3";

    const button = document.createElement("button");
    button.className = "btn btn-outline-primary w-100";
    button.textContent = `회의실 ${room.name || room.roomName} 예약`;
    button.onclick = () =>
      openReservationModal(room.id, room.name || room.roomName);

    roomContainer.appendChild(button);
    roomList.appendChild(roomContainer);
  });

  // 상태 업데이트 즉시 실행
  updateRoomStatus();
}

// 회의실 예약 상태를 업데이트하는 함수
async function updateRoomStatus() {
  try {
    const now = new Date();
    const today = now.toISOString().split("T")[0];
    const currentTime = now.toTimeString().slice(0, 5); // HH:mm 형식

    console.log("회의실 상태 업데이트:", { today, currentTime });

    // 회의실 목록 가져오기
    const response = await fetch(`${API_BASE_URL}/rooms`);
    if (!response.ok) throw new Error("회의실 목록을 불러오는데 실패했습니다");
    const rooms = await response.json();

    if (!Array.isArray(rooms) || rooms.length === 0) {
      console.warn("사용 가능한 회의실이 없습니다");
      return;
    }

    // room-box 컨테이너 생성 또는 가져오기
    let boxContainer = document.querySelector(".room-box-container");
    if (!boxContainer) {
      boxContainer = document.createElement("div");
      boxContainer.className = "room-box-container d-flex flex-wrap";
      boxContainer.style.width = "75%";
      boxContainer.style.gap = "15px";
      boxContainer.style.padding = "15px";
    } else {
      // 기존 컨테이너의 내용을 유지하면서 스타일만 업데이트
      boxContainer.style.width = "75%";
      boxContainer.style.gap = "15px";
      boxContainer.style.padding = "15px";
    }

    // 각 회의실의 예약 정보 가져오기
    for (const room of rooms) {
      try {
        let roomBox = document.querySelector(
          `.room-box[data-room="${room.name || room.roomName}"]`
        );

        if (!roomBox) {
          roomBox = document.createElement("div");
          roomBox.className = "room-box";
          roomBox.setAttribute("data-room", room.name || room.roomName);
          roomBox.innerHTML = `<h3>${
            room.name || room.roomName
          }</h3><span></span>`;
        }

        const res = await fetch(`${API_BASE_URL}/room/${room.id}/reservations`);
        if (!res.ok) throw new Error(`회의실 ${room.name} 예약 정보 조회 실패`);
        const reservations = await res.json();

        // 현재 시간 기준으로 예약 상태 확인
        const todayReservations = reservations.filter(
          (res) => res.reservationDate === today
        );

        // 현재 예약 확인
        const currentReservation = todayReservations.find((res) => {
          const startTime = res.startTime;
          const endTime = res.endTime;
          return currentTime >= startTime && currentTime < endTime;
        });

        // 다음 예약 확인
        const nextReservation = todayReservations
          .filter((res) => res.startTime > currentTime)
          .sort((a, b) => a.startTime.localeCompare(b.startTime))[0];

        // 상태 업데이트
        let status, statusClass;

        if (currentReservation) {
          status = `사용 중 (${currentReservation.team.teamName}, ~${currentReservation.endTime})`;
          statusClass = "reserved";
        } else if (nextReservation) {
          status = `사용 가능 (다음 예약: ${nextReservation.startTime})`;
          statusClass = "available";
        } else {
          status = "사용 가능";
          statusClass = "available";
        }

        roomBox.className = `room-box ${statusClass} mb-2`;
        roomBox.style.flex = "1 1 calc(50% - 7.5px)";
        roomBox.style.minWidth = "calc(50% - 7.5px)";
        roomBox.style.maxWidth = "calc(50% - 7.5px)";
        roomBox.style.fontSize = "21px";
        roomBox.style.padding = "15px";
        roomBox.style.height = "auto";

        if (!boxContainer.contains(roomBox)) {
          boxContainer.appendChild(roomBox);
        }

        roomBox.querySelector("span").textContent = status;
      } catch (error) {
        console.error(
          `회의실 ${room.name || room.roomName} 상태 업데이트 오류:`,
          error
        );
      }
    }

    // room-grid div에 상태창 추가
    const roomGrid = document.querySelector(".room-grid");
    if (roomGrid) {
      // 기존 room-box 컨테이너가 있다면 교체
      const existingContainer = roomGrid.querySelector(".room-box-container");
      if (existingContainer) {
        existingContainer.replaceWith(boxContainer);
      } else {
        roomGrid.appendChild(boxContainer);
      }
    }
  } catch (error) {
    console.error("회의실 상태 업데이트 오류:", error);
  }
}

// 1분마다 회의실 상태 업데이트
setInterval(updateRoomStatus, 60000);

// 예약 현황을 가져오는 함수
async function loadReservations() {
  try {
    const response = await fetch(`${API_BASE_URL}/rooms`);
    const rooms = await response.json();
    // 각 회의실의 예약 정보를 가져옵니다
    let allReservations = [];
    for (const room of rooms) {
      try {
        const reservationResponse = await fetch(
          `${API_BASE_URL}/room/${room.id}/reservations`
        );
        const roomReservations = await reservationResponse.json();
        allReservations = allReservations.concat(roomReservations);
      } catch (error) {
        console.error(`Error loading reservations for room ${room.id}:`, error);
      }
    }
    displayReservations(allReservations);
  } catch (error) {
    console.error("Error loading reservations:", error);
    alert("예약 현황을 불러오는데 실패했습니다.");
  }
}

// 예약 현황을 화면에 표시하는 함수
function displayReservations(reservations) {
  const reservationList = document.getElementById("reservationList");
  if (!reservationList) return;

  // 기존 내용 초기화
  reservationList.innerHTML = "";

  // 테이블 컨테이너 생성
  const tableContainer = document.createElement("div");
  tableContainer.className = "table-responsive";
  tableContainer.style.width = "100%";
  tableContainer.style.margin = "0 auto";

  // 날짜 필터 UI 생성
  const filterContainer = document.createElement("div");
  filterContainer.className = "mb-4 p-3 border rounded bg-light";
  filterContainer.innerHTML = `
    <div class="d-flex align-items-center gap-3">
      <label for="filterDate" class="form-label mb-0 fw-bold">날짜별 조회:</label>
      <input type="date" id="filterDate" class="form-control" style="width: auto; font-size: 16px;" />
      <button class="btn btn-outline-secondary" onclick="clearDateFilter()">
        전체 보기
      </button>
    </div>
  `;

  // 새로운 테이블 생성
  const table = document.createElement("table");
  table.className = "table table-hover";
  table.style.fontSize = "16px";
  table.style.width = "100%";

  // 테이블 헤더 생성
  const thead = document.createElement("thead");
  thead.className = "table-dark";
  thead.style.fontSize = "18px";
  thead.innerHTML = `
    <tr>
      <th style="width: 15%">회의실</th>
      <th style="width: 20%">팀</th>
      <th style="width: 20%">날짜</th>
      <th style="width: 15%">시작 시간</th>
      <th style="width: 15%">종료 시간</th>
      <th style="width: 15%">작업</th>
    </tr>
  `;

  // tbody 생성
  const tbody = document.createElement("tbody");

  // 테이블 구조 조립
  table.appendChild(thead);
  table.appendChild(tbody);
  tableContainer.appendChild(table);

  // 필터 UI를 먼저 추가
  reservationList.appendChild(filterContainer);
  // 테이블 컨테이너 추가
  reservationList.appendChild(tableContainer);

  // 날짜 선택 이벤트 리스너 추가
  const dateFilter = filterContainer.querySelector("#filterDate");
  dateFilter.addEventListener("change", () => {
    const selectedDate = dateFilter.value;
    const filteredReservations = selectedDate
      ? reservations.filter((res) => res.reservationDate === selectedDate)
      : reservations;
    updateReservationList(filteredReservations);
  });

  // 초기 예약 목록 표시
  updateReservationList(reservations);
}

// 날짜 필터 초기화 함수
function clearDateFilter() {
  const filterDate = document.getElementById("filterDate");
  if (filterDate) {
    filterDate.value = "";
    const tbody = document.querySelector("tbody");
    if (tbody) {
      tbody.innerHTML = "";
      // 한 번만 호출
      loadReservations();
    }
  }
}

// 예약 목록 업데이트 함수
function updateReservationList(reservations) {
  const reservationList = document.getElementById("reservationList");
  if (!reservationList) return;

  const tableBody = reservationList.querySelector("tbody");
  if (!tableBody) return;

  tableBody.innerHTML = "";

  if (!Array.isArray(reservations) || reservations.length === 0) {
    const emptyRow = document.createElement("tr");
    emptyRow.innerHTML = `
      <td colspan="6" class="text-center" style="padding: 20px; font-size: 16px;">
        예약 내역이 없습니다.
      </td>
    `;
    tableBody.appendChild(emptyRow);
    return;
  }

  // 날짜 내림차순으로 정렬
  reservations.sort((a, b) => {
    const dateA = new Date(a.reservationDate + "T" + a.startTime);
    const dateB = new Date(b.reservationDate + "T" + b.startTime);
    return dateB - dateA;
  });

  reservations.forEach((reservation) => {
    if (!reservation || !reservation.room || !reservation.team) {
      console.warn("예약 정보 유효성 검사 실패:", {
        id: reservation?.id,
        room: reservation?.room,
        team: reservation?.team,
      });
      return;
    }

    const now = new Date();
    const reservationDate = new Date(reservation.reservationDate);
    const startTime = reservation.startTime.split(":").map(Number);
    const endTime = reservation.endTime.split(":").map(Number);

    const reservationStart = new Date(reservationDate);
    reservationStart.setHours(startTime[0], startTime[1] || 0);

    const reservationEnd = new Date(reservationDate);
    reservationEnd.setHours(endTime[0], endTime[1] || 0);

    const isActive = now >= reservationStart && now < reservationEnd;
    const isPast = now > reservationEnd;

    const row = document.createElement("tr");
    row.className = isActive
      ? "table-primary"
      : isPast
      ? "table-secondary"
      : "";
    row.style.height = "50px"; // 행 높이 설정

    row.innerHTML = `
      <td>${reservation.room.name || reservation.room.roomName}</td>
      <td>${reservation.team.teamName}</td>
      <td>${formatDate(reservation.reservationDate)}</td>
      <td>${formatTime(reservation.startTime)}</td>
      <td>${formatTime(reservation.endTime)}</td>
      <td>
        ${
          !isPast
            ? `<button class="btn btn-sm btn-danger" onclick="cancelReservation(${
                reservation.room.id
              }, ${reservation.id})">
               취소
             </button>
             <button class="btn btn-sm btn-primary" onclick="openReservationModal(${
               reservation.room.id
             }, '${reservation.room.name || reservation.room.roomName}', ${
                reservation.id
              })">
               수정
             </button>`
            : "종료됨"
        }
      </td>
    `;
    tableBody.appendChild(row);
  });
}

// 시간 유효성 검사 함수
async function validateTimes(startTime, endTime, roomId, reservationDate) {
  // 시간을 분으로 변환하여 비교
  const [startHour, startMinute] = startTime.split(":").map(Number);
  const [endHour, endMinute] = endTime.split(":").map(Number);
  const start = startHour * 60 + startMinute;
  const end = endHour * 60 + endMinute;
  const teamId = document.getElementById("teamId").value;
  const reservationId = document.getElementById("reservationId").value;

  // 운영 시간 검사 (09:00 ~ 20:00)
  const operatingStart = 9 * 60; // 09:00
  const operatingEnd = 20 * 60; // 20:00

  if (start < operatingStart || end > operatingEnd) {
    alert("에러: 운영 시간 제한\n\n회의실 운영 시간은 09:00~20:00입니다.");
    return false;
  }

  // 기본 유효성 검사
  if (end <= start) {
    alert("에러: 예약 시간 제한\n\n종료 시간은 시작 시간보다 뒤여야 합니다.");
    return false;
  }

  const duration = end - start;
  if (duration < 60) {
    alert("에러: 예약 시간 제한\n\n최소 1시간 이상 예약해야 합니다.");
    return false;
  }

  if (duration > 120) {
    alert("에러: 예약 시간 제한\n\n최대 2시간까지만 예약할 수 있습니다.");
    return false;
  }

  try {
    // 예약 가능 여부 확인 API 호출
    let checkUrl = `${API_BASE_URL}/room/${roomId}/check?date=${reservationDate}&startTime=${startTime}&endTime=${endTime}&teamId=${teamId}`;

    // 수정 모드인 경우 예약 ID 추가
    if (reservationId) {
      checkUrl += `&reservationId=${reservationId}`;
    }

    console.log("예약 가능 여부 확인:", checkUrl);

    const checkResponse = await fetch(checkUrl);
    const responseText = await checkResponse.text();
    console.log("응답:", responseText);

    if (!checkResponse.ok) {
      alert("에러: " + responseText);
      return false;
    }

    return true;
  } catch (error) {
    console.error("예약 가능 여부 확인 오류:", error);
    alert("에러: " + error.message);
    return false;
  }
}

// 공통 API 호출 함수
async function fetchAPI(url, options = {}) {
  try {
    const response = await fetch(url, {
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
        ...options.headers,
      },
      ...options,
    });

    const responseText = await response.text();
    let responseData;
    try {
      responseData = JSON.parse(responseText);
    } catch (e) {
      responseData = responseText;
    }

    if (!response.ok) {
      throw new Error(
        typeof responseData === "object"
          ? responseData.message || responseData.error
          : responseData
      );
    }

    return responseData;
  } catch (error) {
    console.error("API 호출 오류:", error);
    throw error;
  }
}

// 공통 모달 초기화 함수
function initializeModal(modalId) {
  const oldModal = document.getElementById(modalId);
  const newModal = oldModal.cloneNode(true);
  oldModal.parentNode.replaceChild(newModal, oldModal);
  return new bootstrap.Modal(newModal);
}

// 공통 이벤트 리스너 등록 함수
function addEventListenerOnce(element, eventType, handler) {
  const newElement = element.cloneNode(true);
  element.parentNode.replaceChild(newElement, element);
  newElement.addEventListener(eventType, handler);
  return newElement;
}

// 예약 데이터 검증 함수
async function validateReservationData(
  roomId,
  startTime,
  endTime,
  reservationDate,
  teamId,
  password,
  reservationId = null
) {
  // 입력값 유효성 검사
  const validationErrors = validateReservation(teamId, password);
  if (validationErrors.length > 0) {
    alert(validationErrors.join("\n"));
    return false;
  }

  // 시간 유효성 검사
  if (!(await validateTimes(startTime, endTime, roomId, reservationDate))) {
    return false;
  }

  return true;
}

// 예약 처리 함수 (생성/수정 공통)
async function processReservation(isUpdate = false) {
  const roomId = document.getElementById("roomId").value;
  const reservationId = document.getElementById("reservationId").value;
  const startTime = document.getElementById("startTime").value;
  const endTime = document.getElementById("endTime").value;
  const reservationDate = document.getElementById("reservationDate").value;
  const teamId = document.getElementById("teamId").value;
  const password = document.getElementById("password").value;

  if (
    !(await validateReservationData(
      roomId,
      startTime,
      endTime,
      reservationDate,
      teamId,
      password,
      reservationId
    ))
  ) {
    return;
  }

  const reservationData = {
    teamId: parseInt(teamId),
    reservationDate: reservationDate,
    startTime: startTime,
    endTime: endTime,
    password: password,
  };

  try {
    const url = isUpdate
      ? `${API_BASE_URL}/room/${roomId}/reservation/${reservationId}`
      : `${API_BASE_URL}/room/${roomId}`;

    const method = isUpdate ? "PUT" : "POST";

    await fetchAPI(url, {
      method: method,
      body: JSON.stringify(reservationData),
    });

    alert(`예약이 성공적으로 ${isUpdate ? "수정" : "생성"}되었습니다.`);
    const modal = bootstrap.Modal.getInstance(
      document.getElementById("reservationModal")
    );
    modal.hide();
    document.getElementById("reservationForm").reset();

    // 예약 목록과 상태 한 번만 업데이트
    await Promise.all([loadReservations(), updateRoomStatus()]);
  } catch (error) {
    alert(
      `예약 ${isUpdate ? "수정" : "생성"}에 실패했습니다:\n\n${error.message}`
    );
  }
}

// makeReservation과 updateReservation 함수를 하나로 통합
async function makeReservation() {
  return processReservation(false);
}

async function updateReservation() {
  return processReservation(true);
}

// 예약 모달을 여는 함수
function openReservationModal(roomId, roomName, reservationId = null) {
  document.getElementById("roomId").value = roomId;

  // 기존 모달 제거 및 새로운 모달 생성
  const modal = initializeModal("reservationModal");
  const newModal = document.getElementById("reservationModal");

  // flatpickr 초기화
  const reservationDateInput = document.getElementById("reservationDate");
  if (reservationDateInput._flatpickr) {
    reservationDateInput._flatpickr.destroy();
  }

  flatpickr(reservationDateInput, {
    locale: "ko",
    dateFormat: "Y-m-d",
    minDate: "today",
    maxDate: new Date().fp_incr(1),
    enable: [
      {
        from: "today",
        to: new Date().fp_incr(1),
      },
    ],
    onOpen: function (selectedDates, dateStr, instance) {
      const now = new Date();
      if (now.getHours() >= 21) {
        instance.config.minDate = new Date().fp_incr(1);
        instance.config.maxDate = new Date().fp_incr(2);
      }
    },
  });

  // 수정 모드인 경우
  if (reservationId) {
    document.getElementById("reservationId").value = reservationId;
    newModal.querySelector(
      ".modal-title"
    ).textContent = `회의실 ${roomName} 예약 수정`;
    newModal.querySelector(".btn-primary").textContent = "수정";

    // 기존 예약 정보 불러오기
    fetchAPI(`${API_BASE_URL}/room/${roomId}/reservations`)
      .then((reservations) => {
        const reservation = reservations.find(
          (r) => r.id === parseInt(reservationId)
        );
        if (!reservation) {
          throw new Error("예약 정보가 존재하지 않습니다.");
        }

        document.getElementById("reservationDate").value =
          reservation.reservationDate;
        document.getElementById("startTime").value =
          reservation.startTime.substring(0, 5);
        document.getElementById("endTime").value =
          reservation.endTime.substring(0, 5);

        // 팀 목록을 불러온 후 선택
        return loadTeams().then(() => {
          const teamSelect = document.getElementById("teamId");
          if (reservation.team && reservation.team.id) {
            teamSelect.value = reservation.team.id;
          }
        });
      })
      .catch((error) => {
        console.error("예약 정보 조회 실패:", error);
        alert("예약 정보를 불러오는데 실패했습니다: " + error.message);
        modal.hide();
      });
  } else {
    // 새 예약인 경우
    document.getElementById("reservationId").value = "";
    newModal.querySelector(
      ".modal-title"
    ).textContent = `회의실 ${roomName} 예약`;
    newModal.querySelector(".btn-primary").textContent = "예약";
    document.getElementById("reservationForm").reset();
    loadTeams();
  }

  // 예약 버튼에 이벤트 리스너 등록
  const reserveButton = newModal.querySelector(".btn-primary");
  reserveButton.onclick = async (e) => {
    e.preventDefault();

    const formData = {
      reservationId: document.getElementById("reservationId").value,
      startTime: document.getElementById("startTime").value,
      endTime: document.getElementById("endTime").value,
      roomId: document.getElementById("roomId").value,
      reservationDate: document.getElementById("reservationDate").value,
      teamId: document.getElementById("teamId").value,
      password: document.getElementById("password").value,
    };

    // 입력값 유효성 검사
    const validationErrors = validateReservation(
      formData.teamId,
      formData.password
    );
    if (validationErrors.length > 0) {
      alert(validationErrors.join("\n"));
      return;
    }

    // 시간 유효성 검사
    if (
      !(await validateTimes(
        formData.startTime,
        formData.endTime,
        formData.roomId,
        formData.reservationDate
      ))
    ) {
      return;
    }

    try {
      await processReservation(!!formData.reservationId);
      modal.hide();
    } catch (error) {
      console.error("예약 처리 중 오류 발생:", error);
      alert("예약 처리 중 오류가 발생했습니다.");
    }
  };

  // 시간 선택 이벤트 리스너 추가
  const startTimeSelect = document.getElementById("startTime");
  const endTimeSelect = document.getElementById("endTime");

  const timeChangeHandler = () => {
    const startTime = startTimeSelect.value;
    const endTime = endTimeSelect.value;
    const roomId = document.getElementById("roomId").value;
    const reservationDate = document.getElementById("reservationDate").value;

    if (startTime && endTime && roomId && reservationDate) {
      validateTimes(startTime, endTime, roomId, reservationDate);
    }
  };

  startTimeSelect.onchange = timeChangeHandler;
  endTimeSelect.onchange = timeChangeHandler;

  modal.show();
}

// 입력값 유효성 검사 함수
function validateInput(input, name, minLength = 0) {
  if (!input || input.trim() === "") {
    return `${name}을(를) 입력해주세요.`;
  }
  if (minLength > 0 && input.trim().length < minLength) {
    return `${name}은(는) 최소 ${minLength}자 이상이어야 합니다.`;
  }
  return null;
}

// 예약 유효성 검사 함수
function validateReservation(teamId, password) {
  const errors = [];

  if (!teamId) {
    errors.push("팀을 선택해주세요.");
  }

  const passwordError = validateInput(password, "비밀번호", 4);
  if (passwordError) {
    errors.push(passwordError);
  }

  return errors;
}

// 예약을 취소하는 함수
async function cancelReservation(roomId, reservationId) {
  // roomId나 reservationId가 유효하지 않은 경우
  if (!roomId || !reservationId) {
    console.error("예약 취소 실패: 유효하지 않은 ID", {
      roomId,
      reservationId,
    });
    alert("예약 취소에 실패했습니다: 유효하지 않은 예약 정보");
    return;
  }

  const password = prompt("예약 취소를 위한 비밀번호를 입력하세요:");

  // 비밀번호 유효성 검사
  const passwordError = validateInput(password, "비밀번호", 4);
  if (passwordError) {
    alert(passwordError);
    return;
  }

  try {
    console.log("예약 취소 요청:", {
      roomId: roomId,
      reservationId: reservationId,
      password: "****", // 비밀번호 노출 방지
    });

    const response = await fetch(
      `${API_BASE_URL}/room/${roomId}/reservation/${reservationId}?password=${password}`,
      {
        method: "DELETE",
      }
    );

    const responseText = await response.text();
    console.log("예약 취소 응답:", responseText);

    if (!response.ok) {
      throw new Error(responseText || "서버에서 오류가 발생했습니다");
    }

    try {
      const result = JSON.parse(responseText);
      console.log("예약 취소 성공:", result);
    } catch (e) {
      console.warn("응답이 JSON이 아닙니다:", responseText);
    }

    alert("예약이 취소되었습니다.");
    loadReservations();
    updateRoomStatus();
  } catch (error) {
    console.error("예약 취소 오류:", error);
    alert("예약 취소에 실패했습니다:\n\n" + error.message);
  }
}

// 페이지 로드 시 실행 (이벤트 리스너 통합)
document.addEventListener("DOMContentLoaded", () => {
  console.log("페이지 로드됨");

  // 초기 로드
  loadRooms();
  loadReservations();

  // 탭 변경 이벤트 리스너 (한 번만 등록)
  const reservationsTab = document.getElementById("reservations-tab");
  if (reservationsTab) {
    const newReservationsTab = reservationsTab.cloneNode(true);
    reservationsTab.parentNode.replaceChild(
      newReservationsTab,
      reservationsTab
    );
    newReservationsTab.addEventListener("click", () => {
      loadReservations();
    });
  }
});
