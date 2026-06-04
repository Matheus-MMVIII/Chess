async function loadChess() {
    const url = "http://localhost:8080/api/chess/";
    table = document.getElementById("table");
    let tableText = ``;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`);
        }

        const result = await response.json();
        console.log(result);

        for (i = 1; i <= 8; i++) {
            tableText += `<div class="line">`;
            for (j = 0; j <= 7; j++) {
                tableText += `<a class="piece">${result["line" + i][j]}</a>`;
            }
            tableText += `</div>`;
        }
        table.innerHTML = tableText;
    } catch (error) {
        console.error(error.message);
    }
}