var form = FormApp.openById('FORM-ID');
let survey = "";

/**
 * Main function that iterates over all form items.
 */
function main() {
    survey = "";
    form.getItems().forEach(callback);
    console.log(survey);
}

/**
 * Determines if a form item is a multiple choice question.
 * If so, it logs the question and the answers.
 */
function callback(item){
    if (item.getType() == FormApp.ItemType.MULTIPLE_CHOICE) {
        var question = item.asMultipleChoiceItem(); // change the type from Item to MultipleChoiceItem
        var choices = question.getChoices();

        let title = question.getTitle();
        survey += title + "\n";
        for (let i = 0; i < choices.length; i++){
          let choice = choices[i].getValue();
          survey += choice + "\n";
        }
        survey += "\n";
    }
}