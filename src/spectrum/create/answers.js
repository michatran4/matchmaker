const answers = document.querySelectorAll('.answer')
const containers = document.querySelectorAll('.container')
const answer_box = document.querySelector('#answer-box')
const create_answer = document.querySelector("#addAnswer")

function createAnswer(submit_button) { // this is creating only for the submit button
    const answer = document.createElement('p');
    answer.classList.add('answer');
    answer.draggable = true;
    answer.addEventListener('dragstart', () => {
        answer.classList.add('dragging')
    })
    answer.addEventListener('dragend', () => {
        answer.classList.remove('dragging')
    })
    let container = submit_button.parentNode;
    answer.innerHTML = container.querySelector("#answer-box").value;
    container.querySelector('.spectrum').append(answer);
}

containers.forEach(container => {
    const spectrum = container.querySelector('.spectrum')
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
