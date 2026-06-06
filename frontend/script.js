const pieces = {
    r: "/images/white-rook.png",
    h: "/images/white-horse.png",
    b: "/images/white-bishop.png",
    q: "/images/white-queen.png",
    k: "/images/white-king.png",
    p: "/images/white-pawn.png",

    R: "/images/black-rook.png",
    H: "/images/black-horse.png",
    B: "/images/black-bishop.png",
    Q: "/images/black-queen.png",
    K: "/images/black-king.png",
    P: "/images/black-pawn.png"
};

async function loadChess() {
    const url = "http://localhost:8080/api/chess/";
    table = document.getElementById("table");
    let tableText = `<div class="pieces">`;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`);
        }

        const result = await response.json();
        console.log(result);

        for (i = 1; i <= 8; i++) {
            if (i % 2 === 0) {
                for (j = 0; j <= 7; j++) {
                    if (j % 2 === 0) {
                        if (result["line" + i][j] === ".")
                            tableText += `<div class="piece-back-white"></div>`;
                        else
                            tableText += `<div class="piece-back-white"><img src="${pieces[result["line" + i][j]]}"></div>`;
                    } else {
                        if (result["line" + i][j] === ".")
                            tableText += `<div class="piece-back-black"></div>`;
                        else
                            tableText += `<div class="piece-back-black"><img src="${pieces[result["line" + i][j]]}"></div>`;
                    }
                }
            }else {
                for (j = 0; j <= 7; j++) {
                    if (j % 2 !== 0) {
                        if (result["line" + i][j] === ".")
                            tableText += `<div class="piece-back-white"></div>`;
                        else
                            tableText += `<div class="piece-back-white"><img src="${pieces[result["line" + i][j]]}"></div>`;
                    } else {
                        if (result["line" + i][j] === ".")
                            tableText += `<div class="piece-back-black"></div>`;
                        else
                            tableText += `<div class="piece-back-black"><img src="${pieces[result["line" + i][j]]}"></div>`;
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

loadChess();