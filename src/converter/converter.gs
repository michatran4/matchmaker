/**
 * Converter that takes formatted sections, questions, and answers from a Google Doc and turns it into a Google Form.
 */
let docId = 'DOC-ID'; // Google Docs with the questions/answers and sections
let formId = 'FORM-ID'; // the Google Form to populate
function main() {
    var doc = DocumentApp.openById(docId);
    let content = doc.getBody().getText();
    content = content.trim().split("\n\n"); // remove trailing newlines

    var form = FormApp.openById(formId);
    for (let i = 0; i < content.length; i++) {
        if (content[i].includes("\n")) { // has questions and answers
            var split = content[i].split("\n");
            var question = form.addMultipleChoiceItem();

            // format the question title to remove weights if it has one
            var title = split[0];
            if (title.charAt(0) == '(') {
                title = title.substring(title.indexOf(" ") + 1);
            }
            question.setTitle(title);

            choices = [] // the created choices need to be in an array
            for (let j = 1; j < split.length; j++) { // first item is the question title
                choices.push(question.createChoice(split[j])); // create the choices
            }
            question.setChoices(choices); // add the choices to the question
        }
        else { // it is a section
            var section = form.addSectionHeaderItem(); // create the section
            section.setTitle(content[i]); // add title
            var title = section.getTitle(); // TODO: The API is really dumb so this ensures that the title is filled in
        }
    }
}