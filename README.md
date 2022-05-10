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
the survey manually using Google Forms.

### Obtaining survey input
For answer analysis and compatibility with `src/Matchmaker.java`, the following files need to 
be in the specified places:
- The `counts.dat` file generated when the index was generated should be moved to the `data` 
directory.
- User submissions should be moved to the `data/users` directory. 

### Matching
Run `src/Matchmaker.java`. The output will be written to `data-out`.
- There will be a spreadsheet of the preferences.
- There will be information for each person regarding whom he/she matched with. The name of the 
file will be the name of the person the information should be sent to.
