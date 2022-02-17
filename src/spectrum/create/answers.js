const answers = document.querySelectorAll('.answer')
const containers = document.querySelectorAll('.container')

function createAnswer(submit_button) { // this is creating only for the submit button
    let container = submit_button.parentNode;
    const text = container.querySelector('.answer-box').value;
    if (text == '') return alert('You cannot add an empty answer');
    const answer = document.createElement('p');
    answer.classList.add('answer');
    answer.draggable = true;
    answer.addEventListener('dragstart', () => {
        answer.classList.add('dragging')
    })
    answer.addEventListener('dragend', () => {
        answer.classList.remove('dragging')
    })
    answer.innerHTML = text;
    container.querySelector('.spectrum').append(answer);
}

// do the spectrum of the one available question
containers.forEach(container => {
    const spectrum = container.querySelector('.spectrum')
    spectrum.addEventListener('dragover', e => { // ordering functionality
        e.preventDefault()
        const afterElement = getDragAfterElement(spectrum, e.clientX)
        const draggable = document.querySelector('.dragging')
        if (afterElement == null) {
            spectrum.appendChild(draggable)
        } else {
            spectrum.insertBefore(draggable, afterElement)
        }
    })
})

function getDragAfterElement(container, x) {
    const draggableElements = [...container.querySelectorAll('.answer:not(.dragging)')]
    return draggableElements.reduce((closest, child) => {
        const box = child.getBoundingClientRect()
        const offset = x - box.left - box.width / 2;
        if (offset < 0 && offset > closest.offset) {
            return { offset: offset, element: child }
        } else {
            return closest
        }
    }, { offset: Number.NEGATIVE_INFINITY }).element
}

function createQuestion() {
    const container = document.createElement('div');
    container.classList.add('container');
    
    const qLabel = document.createElement('label');
    qLabel.htmlFor = 'question';
    qLabel.innerHTML = 'Question: '
    const question = document.createElement('input');
    question.classList.add('question');

    const spectrum = document.createElement('div');
    spectrum.classList.add('spectrum');
    spectrum.style = 'display:flex';
    spectrum.addEventListener('dragover', e => { // ordering functionality
        e.preventDefault()
        const afterElement = getDragAfterElement(spectrum, e.clientX)
        const draggable = document.querySelector('.dragging')
        if (afterElement == null) {
            spectrum.appendChild(draggable)
        } else {
            spectrum.insertBefore(draggable, afterElement)
        }
    })

    const aLabel = document.createElement('label');
    aLabel.htmlFor = 'answer-box';
    aLabel.innerHTML = 'Add answer: '
    const answer_box = document.createElement('input');
    answer_box.classList.add('answer-box');

    const addAnswer = document.createElement('input');
    addAnswer.classList.add('answer-button');
    addAnswer.type = 'submit';
    addAnswer.value = 'Add Answer';

    container.appendChild(qLabel);
    container.appendChild(question);
    container.appendChild(spectrum);
    container.appendChild(aLabel);
    container.appendChild(answer_box);
    container.appendChild(addAnswer);
    document.body.appendChild(container);

    addAnswer.onclick = () => createAnswer(addAnswer);
}
