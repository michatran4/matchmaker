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
    console.log(output); // TODO submit
}
