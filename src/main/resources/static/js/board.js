let from = "";
let to = "";
let roomName = "{{roomName}}";

fetchPieces();
fetchScores();
fetchTurn();

chessBoard.addEventListener("click", ({target: {id}}) => {
    if (from === "") {
        from = id;
    } else if (to === "") {
        to = id;
        move();
        from = "";
        to = "";
    }
})

async function move() {
    document.getElementById("error").innerText = "";
    const pathName = window.location.pathname;

    const res = await fetch(`${pathName}/pieces`, {
        headers: {
            'Content-Type': 'application/json'
        },
        method: "PUT",
        body: JSON.stringify({from: `${from}`, to: `${to}`})
    });
    const data = await res.json();

    if (!res.ok) {
        document.getElementById("error").innerText = data.message;
    }

    fetchPieces();
    fetchScores();
    fetchTurn();
}

async function end() {
    const pathName = window.location.pathname;

    const res = await fetch(`${pathName}/result`);
    const data = await res.json();

    if (!res.ok) {
        document.getElementById("error").innerText = data.message;
        return;
    }

    let winColor = "";
    if (data.score.whiteScore > data.score.blackScore) {
        winColor = "white";
    } else if (data.score.whiteScore < data.score.blackScore) {
        winColor = "black";
    } else {
        winColor = "draw";
    }
    let element = document.getElementById(winColor);
    if (winColor !== "draw") {
        element = document.getElementById(winColor + "_win");
    }
    element.style.visibility = "visible";
}

function exitRoom() {
    const pathName = window.location.pathname;
    window.location.href = "/";
    fetch(`${pathName}`, {method: "DELETE"})
}

async function fetchPieces() {
    const pathName = window.location.pathname;
    clearBoard();

    const res = await fetch(`${pathName}/pieces`);
    const datas = await res.json();

    datas.forEach(data => {
        const element = document.getElementById(data.position);
        const img = document.createElement("img");
        img.src = `/images/${data.pieceType}_${data.color.toLocaleLowerCase()}.png`;
        img.id = `${data.position}`;
        img.alt = "piece";
        img.className = "piece_img";
        element.appendChild(img);
    });
}


async function fetchScores() {
    const pathName = window.location.pathname;

    const res = await fetch(`${pathName}/scores`);
    const data = await res.json();

    if (!res.ok) {
        document.getElementById("error").innerText = data.message;
        return;
    }

    document.getElementById('whiteScore').innerText = `WHITE 점수 : ${data.whiteScore}`;
    document.getElementById('blackScore').innerText = `BLACK 점수 : ${data.blackScore}`;
}

async function fetchTurn() {
    const pathName = window.location.pathname;

    let res = await fetch(`${pathName}/turn`);
    res = await res.json();

    const turns = document.getElementById("turn");
    for (let i = 0; i < 2; i++) {
        turns.children[i].style.boxShadow = "none";
    }
    const currentTurn = document.getElementById(res.currentTurn.toLowerCase() + "_turn");
    currentTurn.style.boxShadow = "0 0 10px 2px darkred";
}

function clearBoard() {
    const chessBoard = document.getElementById("chessBoard");
    for (let i = 0; i < chessBoard.children.length; i++) {
        chessBoard.children[i].innerHTML = `<div id=${chessBoard.children[i].id}></div>`
    }
}