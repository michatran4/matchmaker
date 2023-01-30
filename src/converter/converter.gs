/**
 * Converter that takes formatted sections, questions, and answers from a Google Doc and turns it into a Google Form.
 * This automatically creates a Google Form for you and logs the link for you to edit and view.
 */
let docId = 'DOC-ID'; // Google Docs with the questions/answers and sections
function main() {
    // Google Form Auto Creator
    var form = FormApp.create('Matchmaker Survey');
    form.setTitle("Matchmaker Survey");
    form.setDescription('This is the survey for the matchmaker fundraiser.')
    form.setAllowResponseEdits(true); // if the user decides to edit their response
    form.setLimitOneResponsePerUser(true); // only one response per person
    form.setRequireLogin(true); // Restrict to users in the organization
    form.setCollectEmail(true); // Collect email addresses

    var nameValidation = FormApp.createTextValidation()
        .requireTextContainsPattern("[A-Z][a-z]+")
        .setHelpText("Invalid name.")
        .build();

    form.addTextItem()
        .setTitle("What's your first name?")
        .setValidation(nameValidation)
        .setRequired(true);

    form.addTextItem()
        .setTitle("What's your last name?")
        .setValidation(nameValidation)
        .setRequired(true);
    
    form.addTextItem()
        .setTitle("Who is your representative (who collected your survey fee)?")
        .setRequired(true);

    var idValidation = FormApp.createTextValidation()
        .requireNumberBetween(100000, 999999)
        .setHelpText('Invalid student ID.')
        .build();
    form.addTextItem()
        .setTitle("Enter your student ID without the S in front.")
        .setValidation(idValidation)
        .setRequired(true);

    var gender = form.addMultipleChoiceItem();
    gender.setTitle("What's your gender?");
    gender.setChoices([
        gender.createChoice('Male'),
        gender.createChoice('Female')
    ]);
    gender.setRequired(true);

    var emailValidation = FormApp.createTextValidation()
        .requireTextContainsPattern("^[^\s@]+@[^\s@]+\.[^\s@]+$")
        .setHelpText('Invalid email.')
        .build();
    form.addTextItem()
        .setTitle("Provide your personal email.")
        .setValidation(emailValidation)
        .setRequired(true);

    form.addParagraphTextItem()
        .setTitle("Provide your public contact information for your matches to connect with you.")
        .setRequired(true);

    form.addPageBreakItem().setTitle('Questions');

    // Google Doc to Google Form Conversion
    var doc = DocumentApp.openById(docId);
    let content = doc.getBody().getText();
    content = content.trim().split("\n\n"); // remove trailing newlines

    for (let i = 0; i < content.length; i++) {
        var split = content[i].split("\n");
        if (split.length > 2) { // questions must have at least 2 answers
            var question = form.addMultipleChoiceItem();
            question.setRequired(true);

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
        else { // it is a section if there's a single line description following it
            var section = form.addSectionHeaderItem(); // create the section
            section.setTitle(split[0]); // add title
            section.setHelpText(split[1]); // add description
        }
    }

    Logger.log('Editor URL: ' + form.getEditUrl());
    Logger.log('Published URL: ' + form.getPublishedUrl());
}