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
    download(output.substring(0, output.length - 1), 'ans.dat'); // rid of the last new line
}

var txt = '';
var xmlhttp = new XMLHttpRequest();
xmlhttp.onreadystatechange = function(){
  if(xmlhttp.status == 200 && xmlhttp.readyState == 4){
    txt = xmlhttp.responseText;
  }
};
xmlhttp.open("GET","ans.dat",true);
xmlhttp.send();
