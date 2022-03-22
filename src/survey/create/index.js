/**
 * Manually builds an HTML file according to a question and answers data set.
 * This is to be moved into the directory with submit.js and style.css.
 */

const fs = require('fs');
const pretty = require('pretty'); // beautifies HTML

let html =
`<!DOCTYPE html>
<html>
  <head>
    <title>Matchmaker Survey</title>
    <link rel="stylesheet" href="style.css">
    <script src="./submit.js"></script>
  </head>
  <h1>Matchmaker Survey</h1>
  <label>Name:</label>
  <input id="name">
`;

let index = 0;
/**
 * Appends a set to the html string containing a question and answers.
 * @param {string} question 
 * @param {object} answers 
 */
function addSet(question, answers) {
    html += '<p>' + index++ + '. ' + question + '</p>\n';
    html += '<form>\n'
    answers.forEach(answer => {
        html +=
        `<input type="radio" name="a">\n<label>${answer}</label><br>\n`
    });
    html += '</form>\n'
}

/**
 * Parses the answer file.
 * @returns the sets of questions and answers
 */
function getData() {
    let data = fs.readFileSync('ans.dat', 'utf8');
    data = data.substring(0, data.length - 1); // remove extra newline
    return data.split("\n\n"); // sets are separated by 2 newlines
}

/**
 * Save the HTML string.
 */
function writeFile() {
    html += `<br>
    <input type="submit" id="submit" value="Submit" onclick="submit()">
    </html>\n`
    fs.writeFile('index.html', pretty(html), (e)=>{if (e) console.log(e)})
}

/**
 * Splits the data set so the HTML form can be built.
 * A form object is created for every question and answer set.
 */
async function generateHTML() {
    let data = getData();
    data.forEach(set => {
        let question = set.split("\n")[0];
        let answers = set.split("\n")[1].split(" | ");
        addSet(question, answers);
    })
    writeFile();
}

generateHTML();
