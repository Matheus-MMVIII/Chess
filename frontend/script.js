const pieces = {
    r: "/images/white-rook.png",
    h: "/images/white-horse.png",
    b: "/images/white-bishop.png",
    q: "/images/white-queen.png",
    k: "/images/white-king.png",
    p: "/images/white-pawn.png",

    R: "/images/blue-rook.png",
    H: "/images/blue-horse.png",
    B: "/images/blue-bishop.png",
    Q: "/images/blue-queen.png",
    K: "/images/blue-king.png",
    P: "/images/blue-pawn.png"
};

let initialLine = -1;
let initialColumn = -1;
const url = "http://localhost:8081/api/chess/";

async function loadChess() {
    table = document.getElementById("table");
    let tableText = `<div class="pieces">`;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`);
        }

        const result = await response.json();

        for (i = 0; i <= 7; i++) {
            if (i % 2 === 0) {
                for (j = 0; j <= 7; j++) {
                    if (i === initialLine && j === initialColumn && result[i][j] !== ".") {
                        tableText += `<button onclick="handleClick(${i}, ${j}, \'${result[i][j]}\')" class="piece-select"><img src="${pieces[result[i][j]]}"></button>`;
                    } else if (j % 2 === 0) {
                        if (result[i][j] === ".")
                            tableText += `<button onclick="handleClick(${i}, ${j}, \'${result[i][j]}\')" class="piece-back-white"></button>`;
                        else
                            tableText += `<button onclick="handleClick(${i}, ${j}, \'${result[i][j]}\')" class="piece-back-white"><img src="${pieces[result[i][j]]}"></button>`;
                    } else {
                        if (result[i][j] === ".")
                            tableText += `<button onclick="handleClick(${i}, ${j}, \'${result[i][j]}\')" class="piece-back-black"></button>`;
                        else
                            tableText += `<button onclick="handleClick(${i}, ${j}, \'${result[i][j]}\')" class="piece-back-black"><img src="${pieces[result[i][j]]}"></button>`;
                    }
                }
            }else {
                for (j = 0; j <= 7; j++) {
                    if (i === initialLine && j === initialColumn && result[i][j] !== ".") {
                        tableText += `<button onclick="handleClick(${i}, ${j}, \'${result[i][j]}\')" class="piece-select"><img src="${pieces[result[i][j]]}"></button>`;
                    } else if (j % 2 !== 0) {
                        if (result[i][j] === ".")
                            tableText += `<button onclick="handleClick(${i}, ${j}, \'${result[i][j]}\')" class="piece-back-white"></button>`;
                        else
                            tableText += `<button onclick="handleClick(${i}, ${j}, \'${result[i][j]}\')" class="piece-back-white"><img src="${pieces[result[i][j]]}"></button>`;
                    } else {
                        if (result[i][j] === ".")
                            tableText += `<button onclick="handleClick(${i}, ${j}, \'${result[i][j]}\')" class="piece-back-black"></button>`;
                        else
                            tableText += `<button onclick="handleClick(${i}, ${j}, \'${result[i][j]}\')" class="piece-back-black"><img src="${pieces[result[i][j]]}"></button>`;
                    }
                }
            }
        }
        tableText += `</div>`;
        table.innerHTML = tableText;
    } catch (error) {
        console.error(error.message);
    }
}

async function movePiece(endLine, endColumn, pieceType) {
    console.log(`iLine: ${initialLine}, iColumn: ${initialColumn}, eLine: ${endLine}, eColumn: ${endColumn}`);
    try {
        if (initialLine === -1 && initialColumn === -1) {
            return;
        }
        console.log(pieceType);
        if (pieceType === 'p' || pieceType === 'P') {
            if (endLine === 0 || endLine === 7) {
                console.log("TEste");
            }
            if (initialLine === 0 || initialLine === 7) {
                console.log("TesTE");
            }
        }
        const response = await fetch(url, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ "InitialLine": initialLine,
                "InitialColumn": initialColumn,
                "EndLine": endLine,
                "EndColumn": endColumn}),
        });
        setPos(-1, -1);
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`);
        }
        loadChess();
    } catch (error) {
        console.error(error.message);
    }
}

function setPos(posLine, posColumn) {
    initialLine = posLine;
    initialColumn = posColumn;
    loadChess();
}

function handleClick(line, column, piece) {
    if (initialLine === -1) {
        setPos(line, column);
    } else {
        movePiece(line, column, piece);
    }
}

loadChess();