# Matchmaker
![heart](heart.png)

Matchmaker transforms a Google Forms survey into an opportunity to match people based on what they
like.

## Reading research
Compile `docs/matchmaker.tex`.

## Instructions
1. [Create a Google Form for the survey.](#creating-the-survey)
2. [Extract the Google Form's questions and answers with Google Apps Script.](#extraction-with-google-apps-script)
3. [Extract metadata for the primary Matchmaker program.](#metadata-extraction)
4. [Gather survey data.](#collecting-surveys)
5. [After gathering survey data, access and download the Google Sheets.](#google-sheets-data)
6. [Create the headers file and label the important headers of the data.](#specifying-csv-headers)
7. [Run the matchmaking analysis.](#matchmaker-analysis)
8. [Send out the results.](#sending-out-results)

### Creating the survey
Create a survey with [Google Forms](https://docs.google.com/forms/).

#### Form requirements
The following settings on the Google Form need to be enabled:
- Responses > Allow response editing
- Responses > Limit to 1 response
- (Optional) Responses > Restrict to users in Cypress-Fairbanks ISD and its trusted organizations
- (Optional) Responses > Collect email addresses
- Question defaults > Make questions required by default

The following screening questions need to be asked:
- What's your first name?
  - Short answer text
  - Response validation (regex): `[A-Z][a-z]+`
- What's your last name?
  - Short answer text
  - Response validation (regex): `[A-Z][a-z]+`
- Enter your student ID without the S in front.
  - Short answer text
  - Response validation: `Number Between 100000 and 999999`
- What's your gender?
  - Multiple choice: Male, Female
- Provide your personal email.
  - Short answer text
- Provide your public contact information for your matches to connect with you.
  - Short answer text

Then, add a section and add questions for the survey. Customize and format appropriately.

**Questions cannot start with a parenthesis, and they should not be numbered.**

### Extraction with Google Apps Script
- Head to [survey.gs](https://raw.githubusercontent.com/michatran4/matchmaker/master/src/survey/survey.gs)
for the script that extracts the Google Form data and vertically formats it.
- Visit https://script.google.com/home/start and create a new project. Make sure
you are signed in to a Google account with access to the survey form.
- Paste `survey.gs` into the development environment.
- Replace FORM-ID in line 1 with the Google Form ID in the URL bar when accessed
in editor mode.
  - The form ID is located here: `docs.google.com/forms/d/FORM-ID-HERE/edit`
- Save the project with `CTRL+S`.
- Click the `Run` button to run the main function. Give permissions if
requested. If it errors, try another browser.
- Copy the output from the execution log and save it to a file. Take note of the
file's name absolute path.

### Metadata Extraction
In the extracted file from the previous step, remove any questions that pertain to the identity 
of an individual rather than their survey answers. This will likely be the question about gender.

Then, prepend any question weights to questions that are more important or less important for
determining compatibility. This step is optional but helps in the compatibility calculation
algorithm.

Question weights should be formatted accordingly:
`(Decimal) [question]`

The question weights should be in parentheses, preceding the question. The weight and the 
question should be separated with a space. For a question that heavily determines compatibility, 
put a weight greater than 1.0; for a question that lightly determines compatibility (if it's for 
fun), put a weight less than 1.0.

Finally, head to the latest releases for this repository:
https://github.com/michatran4/matchmaker/releases. 
Download the appropriate compiled binary for the metadata extractor.
Optionally, you can compile this
[(`src/metadata/index.js`)](https://raw.githubusercontent.com/michatran4/matchmaker/master/src/metadata/index.js)
 on your own with [pkg](https://www.npmjs.com/package/pkg).

Run the binary, specifying the file's absolute path from the previous step. Then, specify the 
root directory of the Matchmaker program (parent directory of `src`). This will automatically 
save the extracted metadata to the correct destination.

### Collecting surveys
Representatives should log names, student IDs, and provide the link to the survey. Before 
running the matchmaker, entries will be verified to see if they paid for the service.

### Google Sheets Data
Head to the Responses tab of the Google Form in editor mode, and click the green Google Sheets 
icon. This will create the spreadsheet.

Then, download the responses as .csv file. Rename the file to `forms.csv` and place it in 
`data/`. There should already be the other extracted metadata files `survey.txt` and `weights.dat`.

### Specifying CSV headers
The matchmaker needs a bit of help with knowing what information is what for the form questions 
that don't contribute to the actual matchmaking, like name etc.

There should be a file, `data/headers.txt`, specifying column indices for
headers. Each line in the file should point a header label to an index for the
column where the info can be found.
- Example: `name 1`

The following header information needs to be specified:
- first (One's first name)
- last (One's last name)
- id (One's unique student ID)
- gender (One's gender)
- email (One's private email for the CS club to contact and send results to)
- public (One's public contact information to give to matched people)

### Matchmaker Analysis
The most preferable IDE would be IntelliJ IDEA Community Edition. Run 
`src/matchmaker/Matchmaker.java`. The output will be written to `data-out`.
- There will be a spreadsheet of the preferences.
- There will be information for each person regarding whom he/she matched with.
The name of the file will be the ID of the person the information should be
sent to. The provided public contact information should be the first line of
the file.

### Sending out results
Results should be sent out by email in waves due to gmail's rate limit.
