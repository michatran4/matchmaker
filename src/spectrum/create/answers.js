const answers = document.querySelectorAll('.answer')
const spectrum = document.querySelector('.spectrum')
const answer_box = document.querySelector('#answer-box')
const create_answer = document.querySelector("#addAnswer")

function createAnswer(value) {
    if (value == null) { // for the submit button; no provided value. just an onClick.
        // However, this will be reused for loading up answers, too.
        value = answer_box.value;
    }
    const answer = document.createElement('p');
    answer.classList.add('answer');
    answer.innerHTML = value;
    answer.draggable = true;
    answer.addEventListener('dragstart', () => {
        answer.classList.add('dragging')
    })
    answer.addEventListener('dragend', () => {
        answer.classList.remove('dragging')
    })
    spectrum.append(answer);
}

// ordering functionality
spectrum.addEventListener('dragover', e => {
    e.preventDefault()
    const afterElement = getDragAfterElement(spectrum, e.clientX)
    const draggable = document.querySelector('.dragging')
    if (afterElement == null) {
        spectrum.appendChild(draggable)
    } else {
        spectrum.insertBefore(draggable, afterElement)
    }
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
