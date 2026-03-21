/**
 * Заготовка API для бэкенда. Без ES modules — подключается обычным <script>.
 * При отсутствии сервера приложение использует mockData из data.js.
 */
const API_CONFIG = {
    baseUrl: 'http://localhost:8080/api/v1',
    timeout: 10000
};

async function apiRequest(endpoint, options = {}) {
    const url = `${API_CONFIG.baseUrl}${endpoint}`;
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + (localStorage.getItem('authToken') || '')
        },
        ...options
    };
    const response = await fetch(url, defaultOptions);
    if (!response.ok) {
        throw new Error('HTTP error! status: ' + response.status);
    }
    return response.json();
}

async function getDocuments(filters) {
    const params = new URLSearchParams(filters || {});
    return apiRequest('/documents?' + params);
}

async function getDocumentById(id) {
    return apiRequest('/documents/' + id);
}

async function uploadDocument(file) {
    const formData = new FormData();
    formData.append('file', file);
    const response = await fetch(API_CONFIG.baseUrl + '/documents/upload', {
        method: 'POST',
        body: formData,
        headers: {
            'Authorization': 'Bearer ' + (localStorage.getItem('authToken') || '')
        }
    });
    return response.json();
}

async function downloadDocument(id) {
    const response = await fetch(API_CONFIG.baseUrl + '/documents/' + id + '/xml', {
        headers: {
            'Authorization': 'Bearer ' + (localStorage.getItem('authToken') || '')
        }
    });
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'document-' + id + '.xml';
    a.click();
}

/** История событий по документу (вкладка «История» в карточке; глобального аудит-лога в UI нет) */
async function getDocumentHistory(documentId) {
    return apiRequest('/documents/' + documentId + '/events');
}

/** Агрегаты по документам; в текущем UI не используется (раздел «Панель управления» снят) */
async function getStatistics() {
    return apiRequest('/statistics');
}

async function login(username, password) {
    const response = await fetch(API_CONFIG.baseUrl + '/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: username, password: password })
    });
    const data = await response.json();
    if (data.token) {
        localStorage.setItem('authToken', data.token);
        localStorage.setItem('userRole', data.role);
    }
    return data;
}

function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
}
