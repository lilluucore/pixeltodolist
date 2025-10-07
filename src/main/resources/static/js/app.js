let currentFilter = 'all';

document.addEventListener('DOMContentLoaded', function() {
    loadStats();
    loadTasks();

    const addTaskBtn = document.getElementById('addTaskBtn');
    const taskTitleInput = document.getElementById('taskTitle');
    const filterButtons = document.querySelectorAll('.filter-btn');

    addTaskBtn.addEventListener('click', addTask);

    taskTitleInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            addTask();
        }
    });

    filterButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            filterButtons.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            currentFilter = this.dataset.filter;
            loadTasks();
        });
    });
});

async function loadStats() {
    try {
        const response = await fetch('/api/dashboard/stats');
        if (response.ok) {
            const stats = await response.json();
            document.getElementById('activeCount').textContent = stats.activeTasks;
            document.getElementById('completedCount').textContent = stats.completedTasks;
            document.getElementById('streakCount').textContent = stats.currentStreak;
        }
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

async function addTask() {
    const title = document.getElementById('taskTitle').value.trim();
    const description = document.getElementById('taskDescription').value.trim();
    const priority = document.getElementById('taskPriority').value;
    const dueDate = document.getElementById('taskDueDate').value;

    if (!title) {
        alert('Введите название задачи / Please enter task title');
        return;
    }

    try {
        const response = await fetch('/api/tasks', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                title,
                description: description || null,
                priority,
                dueDate: dueDate || null
            })
        });

        if (response.ok) {
            document.getElementById('taskTitle').value = '';
            document.getElementById('taskDescription').value = '';
            document.getElementById('taskDueDate').value = '';
            document.getElementById('taskPriority').value = 'MEDIUM';

            await loadTasks();
            await loadStats();

            showNotification('success');
        }
    } catch (error) {
        console.error('Error adding task:', error);
        showNotification('error');
    }
}

async function loadTasks() {
    try {
        let url = '/api/tasks';
        if (currentFilter !== 'all') {
            url += `?filter=${currentFilter}`;
        }

        const response = await fetch(url);
        if (response.ok) {
            const tasks = await response.json();
            displayTasks(tasks);
        }
    } catch (error) {
        console.error('Error loading tasks:', error);
    }
}

function displayTasks(tasks) {
    const taskList = document.getElementById('taskList');

    if (tasks.length === 0) {
        taskList.innerHTML = `
            <div class="empty-message">
                <span class="jp">タスクがありません</span><br>
                <span class="en">No tasks / Нет задач</span>
            </div>
        `;
        return;
    }

    taskList.innerHTML = tasks.map(task => {
        const dueDate = task.dueDate ? new Date(task.dueDate).toLocaleDateString('ru-RU') : '';
        const priorityText = {
            'HIGH': '高 / High / Высокий',
            'MEDIUM': '中 / Medium / Средний',
            'LOW': '低 / Low / Низкий'
        }[task.priority];

        return `
            <div class="task-item priority-${task.priority} ${task.completed ? 'completed' : ''}" data-task-id="${task.id}">
                <div class="task-header">
                    <div class="task-title">${escapeHtml(task.title)}</div>
                </div>
                ${task.description ? `<div class="task-description">${escapeHtml(task.description)}</div>` : ''}
                
                <div class="task-meta">
                    <div class="task-priority">
                        <span class="jp">重要度:</span>
                        <span class="priority-badge ${task.priority}">${priorityText}</span>
                    </div>
                    ${dueDate ? `
                        <div class="task-due-date">
                            <span class="jp">期限:</span>
                            <span>${dueDate}</span>
                        </div>
                    ` : ''}
                </div>
                
                <div class="task-actions">
                    <button class="action-btn complete" onclick="toggleTask(${task.id})">
                        <span class="jp">${task.completed ? '未完了' : '完了'}</span>
                        <span class="en">${task.completed ? 'Undo / Отменить' : 'Done / Готово'}</span>
                    </button>
                    <button class="action-btn delete" onclick="deleteTask(${task.id})">
                        <span class="jp">削除</span>
                        <span class="en">Delete / Удалить</span>
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

async function toggleTask(taskId) {
    try {
        const response = await fetch(`/api/tasks/${taskId}/toggle`, {
            method: 'PUT'
        });

        if (response.ok) {
            await loadTasks();
            await loadStats();
        }
    } catch (error) {
        console.error('Error toggling task:', error);
    }
}

async function deleteTask(taskId) {
    if (!confirm('本当に削除しますか？ / Delete this task? / Удалить эту задачу?')) {
        return;
    }

    try {
        const response = await fetch(`/api/tasks/${taskId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            await loadTasks();
            await loadStats();
        }
    } catch (error) {
        console.error('Error deleting task:', error);
    }
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

function showNotification(type) {
    const taskItem = document.querySelector('.task-item:first-child');
    if (taskItem && type === 'success') {
        taskItem.style.animation = 'slideIn 0.3s ease';
    }
}
