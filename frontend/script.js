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

var initialLine = -1;
var initialColumn = -1;
const url = "http://localhost:8080/api/chess/";

async function loadChess() {
    table = document.getElementById("table");
    let tableText = `<div class="pieces">`;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`);
        }

        const result = await response.json();
        console.log(result);

        for (i = 0; i <= 7; i++) {
            if (i % 2 === 0) {
                for (j = 0; j <= 7; j++) {
                    if (i === initialLine && j === initialColumn && result[i][j] !== ".") {
                        tableText += `<button onclick="movePiece(${i}, ${j}), setPos(${i}, ${j})" class="piece-select"><img src="${pieces[result[i][j]]}"></button>`;
                    } else if (j % 2 === 0) {
                        if (result[i][j] === ".")
                            tableText += `<button onclick="movePiece(${i}, ${j}), setPos(${i}, ${j})" class="piece-back-white"></button>`;
                        else
                            tableText += `<button onclick="movePiece(${i}, ${j}), setPos(${i}, ${j})" class="piece-back-white"><img src="${pieces[result[i][j]]}"></button>`;
                    } else {
                        if (result[i][j] === ".")
                            tableText += `<button onclick="movePiece(${i}, ${j}), setPos(${i}, ${j})" class="piece-back-black"></button>`;
                        else
                            tableText += `<button onclick="movePiece(${i}, ${j}), setPos(${i}, ${j})" class="piece-back-black"><img src="${pieces[result[i][j]]}"></button>`;
                    }
                }
            }else {
                for (j = 0; j <= 7; j++) {
                    if (i === initialLine && j === initialColumn && result[i][j] !== ".") {
                        tableText += `<button onclick="movePiece(${i}, ${j}), setPos(${i}, ${j})" class="piece-select"><img src="${pieces[result[i][j]]}"></button>`;
                    } else if (j % 2 !== 0) {
                        if (result[i][j] === ".")
                            tableText += `<button onclick="movePiece(${i}, ${j}), setPos(${i}, ${j})" class="piece-back-white"></button>`;
                        else
                            tableText += `<button onclick="movePiece(${i}, ${j}), setPos(${i}, ${j})" class="piece-back-white"><img src="${pieces[result[i][j]]}"></button>`;
                    } else {
                        if (result[i][j] === ".")
                            tableText += `<button onclick="movePiece(${i}, ${j}), setPos(${i}, ${j})" class="piece-back-black"></button>`;
                        else
                            tableText += `<button onclick="movePiece(${i}, ${j}), setPos(${i}, ${j})" class="piece-back-black"><img src="${pieces[result[i][j]]}"></button>`;
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

async function movePiece(endLine, endColumn) {
    console.log(`iLine: ${initialLine}, iColumn: ${initialColumn}, eLine: ${endLine}, eColumn: ${endColumn}`);
    try {
        if (initialLine === -1 && initialColumn === -1) {
            throw new Error(`Know initial piece pos. `);
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

loadChess();