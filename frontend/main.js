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
                        tableText += `<a class="piece-back-white">${result["line" + i][j]}</a>`;
                    } else
                        tableText += `<a class="piece-back-black">${result["line" + i][j]}</a>`;
                }
            }else {
                for (j = 0; j <= 7; j++) {
                    if (j % 2 !== 0) {
                        tableText += `<a class="piece-back-white">${result["line" + i][j]}</a>`;
                    } else
                        tableText += `<a class="piece-back-black">${result["line" + i][j]}</a>`;
                }
            }
        }
        tableText += `</div>`;
        table.innerHTML = tableText;
    } catch (error) {
        console.error(error.message);
    }
}