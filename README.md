# matchmaker-tm
finding your other bit

## Reading research
Compile `docs/matchmaker.tex`.

## Hosting your own instance
Obtain nodejs from binaries and add the directory to the PATH environment variable.
npm isn't needed for this.

### Obtaining survey content
Use `src/spectrum` to accept sets of questions and answers, and revise them if
necessary.

Clicking submit should download the data.

### Creating the survey
This data should be moved to `src/survey`. Run `index.js` to generate the
metadata and a vertically formatted list of the questions and answers. Create
the survey manually, using the formatted list, with Google Forms.

#### Form requirements
The following options on the Google Form need to be enabled:
- Allow response editing
- Limit to 1 response
- Make questions required by default
The following screening questions need to be asked:
- What's your name?
  - Short answer text
- Enter your student ID.
  - Short answer text
- What's your gender?
  - Multiple choice: Male, Female
- Provide your personal email.
  - Short answer text
- Provide your public contact information.
  - Short answer text

### Specifying CSV headers
The matchmaker needs a little bit of help with knowing what information is what for the 
form questions that don't contribute to the actual matchmaking, like name etc.

There should be a file, `/data/headers.txt`, specifying column indices for headers.
Each line in the file should point a header to an index for the column where the info can be 
found.
- Example: `name 1`

The following header information needs to be specified:
- name (One's name)
- id (One's unique student ID)
- gender (One's gender)
- email (One's private email for the CS club to contact and send results to)
- public (One's public contact information to give to matched people)

### Generating preferences
For answer analysis and compatibility with `src/Matchmaker.java`, the following files need to 
be in the `data` directory:
- The `weights.dat` and `survey.txt` files generated when `index.js` was ran,
- The downloaded CSV file with survey input as `forms.csv`, and
- The file stating the column indices for headers as `headers.txt`.

Run `src/matchmaker/Matchmaker.java`. The output will be written to `data-out`.
- There will be a spreadsheet of the preferences.
- There will be information for each person regarding whom he/she matched with. The name of the 
file will be the ID of the person the information should be sent to.

### Sending out results
Results should be sent out by email in waves due to gmail's rate limit.
