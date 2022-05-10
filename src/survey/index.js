/**
 * Gets question/answer metadata and vertically formats the question/answer file.
 */

const read = 'ans.dat'; // answer file to read from
const write = 'survey.txt'; // survey file formatted
const counts = 'counts.dat'; // survey answer counts file
const fs = require('fs');

let countList = "";
let setList = "";

/**
 * Parses the answer file.
 * @returns the sets of questions and answers
 */
function getData() {
    let data = fs.readFileSync(read, 'utf8');
    data = data.substring(0, data.length - 1); // remove extra newline
    return data.split("\n\n"); // sets are separated by 2 newlines
}

/**
 * Save the formatted survey file and metadata.
 */
function writeFile() {
    fs.writeFile(write, setList, (e)=>{if (e) console.log(e)})
    countList = countList.substring(0, countList.length - 1);
    fs.writeFile(counts, countList, (e)=>{if (e) console.log(e)})
}

/**
 * Splits the data set so the HTML form can be built.
 * A form object is created for every question and answer set.
 */
async function generateHTML() {
    let data = getData();
    data.forEach(set => {
        console.log(set);
        if (set.includes("\n")) { // ignore sections for countList
            let question = set.split("\n")[0];
            let answers = set.split("\n")[1].split(" | ");
            if (question.charAt(0) == '(') {
                let decimal = question.substring(1, question.indexOf(')'));
                countList += decimal + ":";
                question = question.substring(question.indexOf(" ") + 1);
            }
            countList += answers.length + ",";

            setList += question + "\n";
            answers.forEach(ans => {
                setList += ans + "\n";
            })
            setList += "\n";
        }
        else { // add sections to the formatted file, but no counts
            setList += set + "\n\n";
        }
    })
    writeFile();
}

generateHTML();
