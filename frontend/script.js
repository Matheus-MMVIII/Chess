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
let board = [];
let promotionPiece;
const baseUrl = "http://localhost:8081/api/chess/";
let url = "http://localhost:8081/api/chess/";
createBoard();

async function createBoard() {
    try {
        console.log("POST: "+baseUrl)
        const response = await fetch(baseUrl, {
            method: "POST"
        });
        const result = await response.json();
        console.log(result);
        url += result.id;
        loadChess();
    } catch (error) {
        console.error(error.message);
    }
}

async function loadChess() {
    const table = document.getElementById("table");
    let tableText = `<div class="pieces">`;

    try {
        console.log("GET: "+url);
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`);
        }

        const result = await response.json();
        board = result;

        for (let i = 0; i < 8; i++) {
            for (let j = 0; j < 8; j++) {
                const isWhite = (i + j) % 2 === 0;
                const squareClass = isWhite ? "piece-back-white" : "piece-back-black";
                const piece = result[i][j];
                const selected = i === initialLine && j === initialColumn && piece !== ".";

                tableText += `
                <button
                    onclick="handleClick(${i}, ${j})"
                    class="${selected ? 'piece-select' : squareClass}">
                    ${piece !== "." ? `<img src="${pieces[piece]}">` : ""}
                </button>`;
            }
        }
        tableText += `</div>`;
        table.innerHTML = tableText;
    } catch (error) {
        console.error(error.message);
    }
}

async function movePiece(endLine, endColumn) {
    let endpoint = url;
    console.log(`iLine: ${initialLine}, iColumn: ${initialColumn}, eLine: ${endLine}, eColumn: ${endColumn}`);
    try {
        if (initialLine === -1 || initialColumn === -1) {
            return;
        }
        let pieceType = board[initialLine][initialColumn];
        if (pieceType === 'p' || pieceType === 'P') {
            let isWhite = pieceType === pieceType.toLowerCase();
            if (endLine === 0 || endLine === 7) {
                if ((initialLine === 1 && isWhite) || (initialLine === 6 && !isWhite)) {
                    const promotionPiece =
                        await openPromotionModal(isWhite);

                    endpoint += "/"+promotionPiece;
                    console.log(endpoint);
                }
            }
        }
        console.log("PUT: "+endpoint);
        const response = await fetch(endpoint, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ "InitialLine": initialLine,
                "InitialColumn": initialColumn,
                "EndLine": endLine,
                "EndColumn": endColumn}),
        });
        clearSelection();
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`);
        }
        await loadChess();
    } catch (error) {
        console.error(error.message);
    }
}

function setPos(posLine, posColumn) {
    initialLine = posLine;
    initialColumn = posColumn;
    loadChess();
}

function handleClick(line, column) {
    if (initialLine === -1 || initialColumn === -1) {
        setPos(line, column);
    } else {
        movePiece(line, column);
    }
}

function clearSelection() {
    initialLine = -1;
    initialColumn = -1;
}

function openPromotionModal(white) {
    return new Promise(resolve => {
        document.getElementById("promotionModal").style.display = "flex";

        const whiteContent = document.querySelector(".promotion-content-white");
        const blackContent = document.querySelector(".promotion-content-black");

        if (white) {
            whiteContent.style.display = "flex";
            blackContent.style.display = "none";
        } else {
            whiteContent.style.display = "none";
            blackContent.style.display = "flex";
        }
        window.resolvePromotion = resolve;
    });
}

function closePromotionModal() {
    document.getElementById("promotionModal").style.display = "none";

    document.querySelector(".promotion-content-white").style.display = "none";
    document.querySelector(".promotion-content-black").style.display = "none";
}

async function selectPromotion(piece) {
    closePromotionModal();

    window.resolvePromotion(piece);
}
