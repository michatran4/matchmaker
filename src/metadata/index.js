/**
 * Extracts question weight data from the survey draft.
 * This also writes to a specified directory so that the main Matchmaker can run properly.
 */

const fs = require('fs');
let weightList = ""; // extracted weight information from questions
let survey = ""; // final survey for Matchmaker.java, stripped of weights

const readline = require('readline').createInterface({
	input: process.stdin,
	output: process.stdout
});

/**
 * Special input function that allows for multiple inputs
 */
function input(prompt) {
    return new Promise((callback, error) => {
        readline.question(prompt, (userInput)=> {
            callback(userInput); // do not remove
        },
        ()=> {});
    });
}

/**
 * Parses the answer file.
 * @returns the sets of questions and answers
 */
function getData(filePath) {
    let data = fs.readFileSync(filePath, 'utf8');
    if (/\s$/.test(data)) {
        data = data.substring(0, data.length - 1); // remove extra newline at the bottom
    }
    return data.split("\r\n\r\n\r\n"); // sets in Google Docs are separated by these
}

/**
 * Save the formatted survey file and weight metadata.
 */
function writeFile(rootDir) {
    let dataDir = rootDir + "/data";
    if (!fs.existsSync(dataDir)) {
      fs.mkdirSync(dataDir, { recursive: true});
    }
    survey = survey.trim(); // trim trailing double newlines
    fs.writeFile(dataDir + "/survey.txt", survey, (e)=>{if (e) console.log(e)})
    weightList = weightList.trim(); // trim trailing space
    fs.writeFile(dataDir + "/weights.dat", weightList, (e)=>{if (e) console.log(e)})
}

const main = async () => {
    let surveyFile = await input("Absolute file path to the survey file with weights included: ");
    let rootDir = await input("Absolute file path to the root directory of the matchmaker: ");
    let data = getData(surveyFile);
    data = data.filter(set => set.trim().split("\r\n").length >= 3); // trim() for excess newlines
    /*
        keep questions and answers, get rid of sections
        questions have at least 2 answers, so there should be 3 lines per question/answer set
        however, sections only have a description, so at most they are 2 line sets
    */
    data.forEach(set => { // 'set' of questions and answers; connected by newlines
        set = set.replace("/\r/g", ""); // remove all \r, keeping the \n for the extracted survey.txt file
        // add the weight of the question to the weight metadata
        let question = set.split("\n")[0];
        if (question.charAt(0) == '(') {
            let decimal = question.substring(1, question.indexOf(')'));
            weightList += decimal;
            set = set.substring(set.indexOf(" ") + 1); // remove the weight from the question
        }
        else {
            weightList += "1.0"; // TODO: can this just be an integer 1
        }
        weightList += " ";

        // add the questions and answers to a survey string, stripped of weights
        survey += set + "\n\n";
    })
    writeFile(rootDir);
    readline.close();
};

main();
