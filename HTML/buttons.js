// Get the JSON and CSV file input elements
const jsonFileInput = document.getElementById("json-file");
const csvFileInput = document.getElementById("csv-file");

// Get the JSON and CSV buttons
const jsonButton = document.getElementById("json-button");
const csvButton = document.getElementById("csv-button");

// Add event listeners to the JSON and CSV buttons to trigger the file input element
jsonButton.addEventListener("click", () => {
    jsonFileInput.click();
});

csvButton.addEventListener("click", () => {
    csvFileInput.click();
});

// Add an event listener to the JSON file input element to handle the file input change event
jsonFileInput.addEventListener("change", (event) => {
    const file = event.target.files[0];

    // Log the file content to the console
    const reader = new FileReader();
    reader.readAsText(file);
    reader.onload = (event) => {
        console.log(event.target.result);
    };
});

// Add an event listener to the CSV file input element to handle the file input change event
csvFileInput.addEventListener("change", (event) => {
    const file = event.target.files[0];
    const reader = new FileReader();
    reader.readAsText(file);
    console.log("Sent POST request")
    reader.onload = (event) => {
        const fileContent = event.target.result;

        //Request
        fetch("http://localhost:8080/ES-2023-2Sem-Terca-Feira-LEIPL-GrupoF-1.0-SNAPSHOT/main.html", {
            method: "POST",
            body: fileContent,
            headers: {
                "Content-Type": "text/csv"
            }
            //Response
        }).then(response => {
            //Add the recieved data to the table
            response.text().then(table => {
                const tableContainer = document.getElementById("bottomContainer");
                tableContainer.innerHTML = table;
            });

        });
    };
});

// Add an event listener to the form to prevent the default form submission behavior when the generate button is clicked
const form = document.querySelector("form");
form.addEventListener("submit", (event) => {
    event.preventDefault();
});