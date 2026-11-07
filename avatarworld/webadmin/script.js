const API = 'http://localhost:8081/api';

function showTab(name) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.sidebar nav a').forEach(el => el.classList.remove('active'));
    document.getElementById(name).classList.add('active');
    document.querySelector(`.sidebar nav a[data-tab="${name}"]`).classList.add('active');
    document.getElementById('pageTitle').textContent = name.charAt(0).toUpperCase() + name.slice(1);
    refreshAll();
}

async function fetchAPI(endpoint, options = {}) {
    try {
        const res = await fetch(API + endpoint, options);
        return await res.json();
    } catch (err) {
        console.error('API Error:', err);
        return null;
    }
}

async function refreshAll() {
    const active = document.querySelector('.tab-content.active');
    if (!active) return;
    const id = active.id;
    switch (id) {
        case 'dashboard': refreshDashboard(); break;
        case 'users': refreshUsers(); break;
        case 'items': refreshItems(); break;
        case 'rooms': refreshRooms(); break;
        case 'logs': refreshLogs(); break;
    }
}

async function refreshDashboard() {
    const stats = await fetchAPI('/stats');
    if (stats) {
        document.getElementById('statUsers').textContent = stats.totalUsers || 0;
        document.getElementById('statRooms').textContent = stats.totalRooms || 0;
        document.getElementById('statItems').textContent = stats.totalItems || 0;
    }
    const logs = await fetchAPI('/logs');
    const recentDiv = document.getElementById('recentLogs');
    recentDiv.innerHTML = '';
    if (logs && logs.logs) {
        logs.logs.slice(0, 10).forEach(log => {
            const div = document.createElement('div');
            div.className = 'log-entry';
            div.innerHTML = `<span class="time">${log.date || ''}</span><span class="action">[${log.action}]</span> ${log.details || ''}`;
            recentDiv.appendChild(div);
        });
    }
}

async function refreshUsers() {
    const data = await fetchAPI('/users');
    const tbody = document.getElementById('usersBody');
    tbody.innerHTML = '';
    if (data && data.users) {
        data.users.forEach(u => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${u.id}</td>
                <td>${u.username}</td>
                <td>${u.coins}</td>
                <td>${u.isAdmin ? '✅' : '❌'}</td>
                <td>${u.isBanned ? '🚫 Yes' : '✅ No'}</td>
                <td>${u.isMuted ? '🔇 Yes' : '✅ No'}</td>
                <td>
                    ${!u.isBanned ? `<button class="btn-ban" onclick="banUser(${u.id})">Ban</button>` : `<button class="btn-unban" onclick="unbanUser(${u.id})">Unban</button>`}
                    ${!u.isMuted ? `<button class="btn-mute" onclick="muteUser(${u.id})">Mute</button>` : `<button class="btn-unban" onclick="unmuteUser(${u.id})">Unmute</button>`}
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function refreshItems() {
    const data = await fetchAPI('/items');
    const tbody = document.getElementById('itemsBody');
    tbody.innerHTML = '';
    if (data && data.items) {
        data.items.forEach(item => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${item.id}</td>
                <td>${item.name}</td>
                <td>${item.type}${item.isFurniture ? ' 🪑' : ''}</td>
                <td>💰${item.price}</td>
                <td>${getRarityBadge(item.rarity)}</td>
                <td><button class="btn-delete" onclick="deleteItem(${item.id})">Delete</button></td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function refreshRooms() {
    const data = await fetchAPI('/rooms');
    const tbody = document.getElementById('roomsBody');
    tbody.innerHTML = '';
    if (data && data.rooms) {
        data.rooms.forEach(r => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${r.id}</td>
                <td>${r.name}</td>
                <td>${r.ownerName || 'ID: ' + r.ownerId}</td>
                <td>${r.maxUsers}</td>
                <td><button class="btn-delete" onclick="deleteRoom(${r.id})">Delete</button></td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function refreshLogs() {
    const data = await fetchAPI('/logs');
    const tbody = document.getElementById('logsBody');
    tbody.innerHTML = '';
    if (data && data.logs) {
        data.logs.forEach(log => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${log.action}</td>
                <td>${log.details || ''}</td>
                <td>${log.ip || ''}</td>
                <td>${log.date || ''}</td>
            `;
            tbody.appendChild(tr);
        });
    }
}

function getRarityBadge(rarity) {
    const colors = { common: '#888', rare: '#4fc3f7', vip: '#ab47bc', legendary: '#ffa726' };
    return `<span style="color:${colors[rarity] || '#888'};font-weight:bold">${rarity.toUpperCase()}</span>`;
}

function filterUsers() {
    const query = document.getElementById('userSearch').value.toLowerCase();
    document.querySelectorAll('#usersBody tr').forEach(tr => {
        const name = tr.cells[1]?.textContent.toLowerCase() || '';
        tr.style.display = name.includes(query) ? '' : 'none';
    });
}

function showAddItemForm() {
    document.getElementById('addItemForm').style.display = 'flex';
}
function hideAddItemForm() {
    document.getElementById('addItemForm').style.display = 'none';
}

async function addItem() {
    const data = {
        name: document.getElementById('itemName').value,
        type: document.getElementById('itemType').value,
        category: document.getElementById('itemCategory').value || 'general',
        price: parseInt(document.getElementById('itemPrice').value) || 0,
        rarity: document.getElementById('itemRarity').value,
        isFurniture: document.getElementById('itemIsFurniture').checked,
        data: '{}'
    };
    const result = await fetchAPI('/items', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({action: 'create', ...data})
    });
    hideAddItemForm();
    refreshItems();
}

async function deleteItem(id) {
    if (!confirm('Delete item ' + id + '?')) return;
    await fetchAPI('/items', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({action: 'delete', id})
    });
    refreshItems();
}

async function banUser(id) {
    if (!confirm('Ban user ' + id + '?')) return;
    await fetchAPI('/users', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({action: 'ban', userId: id})
    });
    refreshUsers();
}

async function unbanUser(id) {
    await fetchAPI('/users', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({action: 'unban', userId: id})
    });
    refreshUsers();
}

async function muteUser(id) {
    await fetchAPI('/users', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({action: 'mute', userId: id})
    });
    refreshUsers();
}

async function unmuteUser(id) {
    await fetchAPI('/users', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({action: 'unmute', userId: id})
    });
    refreshUsers();
}

async function deleteRoom(id) {
    if (!confirm('Delete room ' + id + '?')) return;
    await fetchAPI('/rooms', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({action: 'delete', id})
    });
    refreshRooms();
}

// Initial load
refreshAll();
setInterval(refreshAll, 30000);
