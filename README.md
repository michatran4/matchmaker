# matchmaker-tm
finding your other bit

## Reading research
Compile `docs/matchmaker.tex`.

## Hosting your own instance
For installing with school wifi:
- Obtain nodejs from binaries and add the directory to the PATH environment variable.
- `npm set strict-ssl false` if npm is complaining about a self signed certificate in certificate
  chain.

Initialize with `npm i` once nodejs and npm have been installed.

### Obtaining survey content
Use `src/spectrum/create` and `src/spectrum/revise` to accept sets of questions and answers, and
revise them if necessary.

Clicking submit should download the data.

### Creating the survey
This data should be moved to `src/survey/create`. Run `index.js` to generate the HTML page.

Use `src/survey/answer` to host the actual survey. Move the generated page to this directory.

### Obtaining survey input
For answer analysis and compatibility with `src/Matchmaker.java`, the following files need to 
be in the specified places:
- The `survey.dat` file generated when the index was generated  should be moved to the `data` 
directory.
- User submissions should be moved to the `data/users` directory. 

### Matching
Run `src/Matchmaker.java`. The output will be written to `data-out`.
- There will be a spreadsheet of the preferences.
- There will be information for each person regarding whom he/she matched with. The name of the 
file will be the name of the person the information should be sent to.
