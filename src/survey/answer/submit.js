/**
 * Save the survey answers. TODO this is temporary, just for testing the matchmaking
 * @param {string} data
 * @param {string} fileName
 * @param {string} type
 */
 function download(data, fileName, type="text/plain") {
    const a = document.createElement("a");
    a.style.display = "none";
    document.body.appendChild(a);

    a.href = window.URL.createObjectURL(new Blob([data], {type}));
    a.setAttribute("download", fileName);
    a.click(); // simulate a click

    window.URL.revokeObjectURL(a.href);
    document.body.removeChild(a);
}

/**
 * Turns survey answers into a list of the indices of the selected answers.
 */
function submit() {
    let output = document.getElementById('name').value + '\n';
    if (output == '\n') return alert('You must enter a name.');
    let flag = false;
    let index = 0;
    document.querySelectorAll('form').forEach(form => {
        if (flag) return;
        let inputs = form.querySelectorAll('input');
        for (var i = 0; i < inputs.length; i++) {
            if (inputs[i].checked) {
                break;
            }
        }
        if (i == inputs.length) {
            flag = true;
            return alert("Missing question " + index + ".");
        }
        index++;
        output += i + ",";
    });
    if (flag) {
        return;
    }
    output = output.substring(0, output.length - 1); // remove last comma
    let random = Math.random().toString(36).substring(3);
    download(output, random + ".txt"); // TODO submit. Might need to convert to JSON
}
