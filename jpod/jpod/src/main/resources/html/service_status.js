function booleanToString(value) {
    return value ? "Yes" : "No";
}
document.addEventListener('DOMContentLoaded', () => {
  const button = document.getElementById('refreshButton');

  button.addEventListener('click', () => {
    fetch('/status_services')
      .then(response => {
        if (response.ok) {
          return response.json();
        } else {
          throw new Error('Request failed!');
        }
      })
      .then(data => {
        const table = document.getElementById("serviceTable");
        const tbody = table.querySelector("tbody");
        tbody.innerHTML = ""; // clear table
        data.forEach((item) => {
            const row = document.createElement("tr");
            const nameCell = document.createElement("td");
            const isActiveCell = document.createElement("td");

            nameCell.textContent = item.name;
            isActiveCell.textContent = item.isActive;
            
            row.appendChild(nameCell);
            row.appendChild(isActiveCell);
            tbody.appendChild(row);
        });
      })
      .catch(error => {
        console.log(error);
      });
  });
});