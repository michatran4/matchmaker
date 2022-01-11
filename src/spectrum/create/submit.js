const submitAnswers = () => {
    const question = document.querySelector("#question");
    if (question.value == "") {
        return alert("You must provide a question.");
    }
    const answers = document.querySelectorAll(".answer");
    if (answers.length < 2) {
        return alert("You must provide at least 2 answers.");
    }
    let answer_output = "";
    answers.forEach(answer => { // concatenate all answers with a delimiter
        answer_output += answer.innerHTML + " | ";
    })
    let output = document.querySelector("#question").value
        + "\n"
        + answer_output.substring(0, answer_output.length - 3);
    alert("Successfully submitted your proposed answers!")
    console.log(output); // TODO, submit to a server
}
