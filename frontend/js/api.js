/**
 * Клиент API по artwell-api-openapi-3.0.yaml (только /documents/*).
 */
const API_CONFIG = {
    baseUrl: 'http://localhost:8080/api/v1'
};

function authHeaders(extra) {
    const h = Object.assign(
        {
            Authorization: 'Bearer ' + (localStorage.getItem('authToken') || '')
        },
        extra || {}
    );
    return h;
}

async function parseJsonBody(response) {
    const text = await response.text();
    if (!text) return null;
    try {
        return JSON.parse(text);
    } catch (e) {
        return null;
    }
}

async function buildHttpError(response) {
    const data = await parseJsonBody(response);
    const msg =
        (data && (data.message || data.error)) ||
        'HTTP ' + response.status;
    const err = new Error(msg);
    err.status = response.status;
    err.body = data;
    return err;
}

/**
 * GET /documents?page&size
 */
async function getDocuments(page, size) {
    var p = page !== undefined ? page : 0;
    var s = size !== undefined ? size : 20;
    const params = new URLSearchParams({ page: String(p), size: String(s) });
    const response = await fetch(API_CONFIG.baseUrl + '/documents?' + params, {
        headers: authHeaders()
    });
    if (!response.ok) throw await buildHttpError(response);
    return response.json();
}

/**
 * GET /documents/{documentId}
 */
async function getDocumentById(id) {
    const response = await fetch(API_CONFIG.baseUrl + '/documents/' + encodeURIComponent(id), {
        headers: authHeaders()
    });
    if (!response.ok) throw await buildHttpError(response);
    return response.json();
}

/**
 * GET /documents/{documentId}/versions
 */
async function getDocumentVersions(documentId) {
    const response = await fetch(
        API_CONFIG.baseUrl + '/documents/' + encodeURIComponent(documentId) + '/versions',
        { headers: authHeaders() }
    );
    if (!response.ok) throw await buildHttpError(response);
    return response.json();
}

/**
 * POST /documents/upload
 * @returns {Promise<object>} UploadResult при 201
 */
async function uploadDocument(file, queryParams) {
    const formData = new FormData();
    formData.append('file', file);
    var url = API_CONFIG.baseUrl + '/documents/upload';
    if (queryParams && typeof queryParams === 'object') {
        var q = new URLSearchParams();
        if (queryParams.documentNumber) q.set('documentNumber', queryParams.documentNumber);
        if (queryParams.constructionObjectId) q.set('constructionObjectId', queryParams.constructionObjectId);
        var qs = q.toString();
        if (qs) url += '?' + qs;
    }
    const response = await fetch(url, {
        method: 'POST',
        body: formData,
        headers: authHeaders()
    });
    const data = await parseJsonBody(response);
    if (!response.ok) {
        var err = new Error((data && data.message) || 'Ошибка загрузки (HTTP ' + response.status + ')');
        err.status = response.status;
        err.body = data;
        throw err;
    }
    return data;
}

/**
 * GET /documents/{documentId}/xml
 */
async function downloadDocument(id) {
    const response = await fetch(API_CONFIG.baseUrl + '/documents/' + encodeURIComponent(id) + '/xml', {
        headers: authHeaders()
    });
    if (!response.ok) {
        throw await buildHttpError(response);
    }
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'document-' + id + '.xml';
    a.click();
    window.URL.revokeObjectURL(url);
}

/** Вне минимального OpenAPI; оставлено для совместимости с бэкендом /auth/login */
async function login(username, password) {
    const response = await fetch(API_CONFIG.baseUrl + '/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: username, password: password })
    });
    const data = await parseJsonBody(response);
    if (!response.ok) {
        throw new Error((data && data.message) || 'Ошибка входа');
    }
    if (data && data.accessToken) {
        localStorage.setItem('authToken', data.accessToken);
        if (data.role) localStorage.setItem('userRole', String(data.role));
    }
    return data;
}

function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
}
