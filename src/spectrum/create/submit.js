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
            answer_output += answer.innerHTML + ' | ';
        })
        output += document.querySelector('#question').value
            + '\n'
            + answer_output.substring(0, answer_output.length - 3)
            + '\n\n';
    })
    if (flag) return;
    alert('Successfully submitted your proposed answers!');
    console.log(output); // TODO, submit to a server
}
