/**
 * Save the answer data.
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
 * Properly trim a string to prevent empty answers.
 * @param {string} str 
 * @returns the trimmed string
 */
function trim(str) {
    return str.replace(/&nbsp;/g, ' ').trim();
}

const submitAnswers = () => {
    let output = '';
    let flag = false;
    document.querySelectorAll('.container').forEach(container => {
        if (flag) return;
        const question = container.querySelector('.question');
        if (question.value == '') {
            flag = true; // break out of looping through containers
            return alert('You must fill in the blanks for all questions.');
        }
        const answers = container.querySelectorAll('.answer');
        if (answers.length < 2) {
            flag = true;
            return alert('You must provide at least 2 answers to all questions.');
        }
        let answer_output = '';
        answers.forEach(answer => { // concatenate all answers with a delimiter
            if (flag) return;
            let ans = trim(answer.innerHTML);
            if (ans.length == 0) {
                flag = true;
                return alert("You cannot have an empty answer.");
            }
            answer_output += ans + ' | ';
        })
        if (flag) return;
        output += question.value
            + '\n'
            + answer_output.substring(0, answer_output.length - 3)
            + '\n\n';
    })
    if (flag) return;
    download(output.substring(0, output.length - 1), 'ans-revised.dat'); // rid of the last newline
}
