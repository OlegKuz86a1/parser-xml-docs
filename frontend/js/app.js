var listState = {
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0
};

window.__modalDocumentId = null;

function navigateTo(page) {
    document.querySelectorAll('.sidebar-item').forEach(function (i) {
        i.classList.remove('active');
    });
    var nav = document.querySelector('.sidebar-item[data-page="' + page + '"]');
    if (nav) {
        nav.classList.add('active');
    }

    document.querySelectorAll('.page-content').forEach(function (p) {
        p.classList.add('hidden');
    });
    var pageEl = document.getElementById(page + 'Page');
    if (pageEl) {
        pageEl.classList.remove('hidden');
    }
    updatePageHeader(page);
    if (page === 'documents') {
        loadDocumentList();
    }
}

function updatePageHeader(page) {
    var titles = {
        documents: { title: 'Документы', desc: 'Список документов; версии — в карточке документа' },
        upload: { title: 'Загрузка документов', desc: 'Загрузка и валидация XML по XSD (POST /documents/upload)' },
        settings: { title: 'Настройки', desc: 'Управление профилем и доступом' }
    };
    var t = titles[page];
    if (!t) return;
    document.getElementById('pageTitle').textContent = t.title;
    document.getElementById('pageDescription').textContent = t.desc;
}

function initializeNavigation() {
    document.getElementById('app').addEventListener('click', function (e) {
        var link = e.target.closest('[data-page]');
        if (!link) return;
        e.preventDefault();
        var page = link.getAttribute('data-page');
        if (!page) return;
        document.querySelectorAll('.sidebar-item').forEach(function (i) {
            i.classList.remove('active');
        });
        var sidebarMatch = document.querySelector('.sidebar-item[data-page="' + page + '"]');
        if (sidebarMatch) {
            sidebarMatch.classList.add('active');
        }
        document.querySelectorAll('.page-content').forEach(function (p) {
            p.classList.add('hidden');
        });
        var pageEl = document.getElementById(page + 'Page');
        if (pageEl) {
            pageEl.classList.remove('hidden');
        }
        updatePageHeader(page);
        if (page === 'documents') {
            loadDocumentList();
        }
    });

    document.getElementById('uploadBtn').addEventListener('click', function () {
        navigateTo('upload');
    });
}

function initializeRoleToggle() {
    document.getElementById('roleToggle').addEventListener('click', function () {
        currentUser.role = currentUser.role === 'Подрядчик' ? 'Заказчик' : 'Подрядчик';
        document.getElementById('userRole').textContent = currentUser.role;
        showToast('Роль изменена на ' + currentUser.role);
    });
}

function resetUploadPanels() {
    document.getElementById('uploadProgress').classList.add('hidden');
    document.getElementById('validationResult').classList.add('hidden');
    document.getElementById('validationError').classList.add('hidden');
    var bar = document.getElementById('progressBar');
    if (bar) bar.style.width = '0%';
    var pct = document.getElementById('uploadPercent');
    if (pct) pct.textContent = '0%';
    var msgList = document.getElementById('validationMessagesList');
    if (msgList) {
        msgList.innerHTML = '';
        msgList.classList.add('hidden');
    }
}

function apiStatusToUiClass(status) {
    if (!status) return 'valid';
    var u = String(status).toUpperCase();
    if (u === 'INVALID') return 'invalid';
    return 'valid';
}

function getStatusText(status) {
    var u = status ? String(status).toUpperCase() : '';
    if (u === 'INVALID') return 'Ошибка';
    if (status === 'invalid') return 'Ошибка';
    return 'Валидный';
}

function formatDateRu(iso) {
    if (!iso) return '—';
    var d = new Date(iso);
    if (!isNaN(d.getTime())) return d.toLocaleDateString('ru-RU');
    return String(iso);
}

function formatIsoDateTime(iso) {
    if (!iso) return '—';
    var d = new Date(iso);
    if (!isNaN(d.getTime())) return d.toLocaleString('ru-RU');
    return String(iso);
}

function displayDocumentType(doc) {
    if (doc.title) return doc.title;
    if (doc.documentType) return String(doc.documentType).replace(/_/g, ' ');
    return '—';
}

async function loadDocumentList() {
    var table = document.getElementById('allDocsTable');
    var info = document.getElementById('listPageInfo');
    if (!window.__staticDocsTableHtml && table) {
        window.__staticDocsTableHtml = table.innerHTML;
    }
    info.textContent = 'Загрузка списка с сервера…';
    try {
        var page = await getDocuments(listState.page, listState.size);
        listState.totalElements = page.totalElements;
        listState.totalPages = page.totalPages;
        var content = page.content || [];
        var start = content.length === 0 ? 0 : page.number * page.size + 1;
        var end = content.length === 0 ? 0 : page.number * page.size + content.length;
        info.textContent =
            'Показано ' + start + '–' + end + ' из ' + page.totalElements + ' документов (стр. ' + (page.number + 1) + ' из ' + Math.max(1, page.totalPages) + ')';

        if (content.length === 0) {
            table.innerHTML =
                '<tr><td colspan="8" class="px-6 py-8 text-center text-gray-500">Нет документов</td></tr>';
        } else {
            table.innerHTML = content.map(renderDocumentRow).join('');
        }
        if (typeof lucide !== 'undefined') lucide.createIcons();

        document.getElementById('paginationPrev').disabled = page.number <= 0;
        document.getElementById('paginationNext').disabled = page.totalPages <= 0 || page.number >= page.totalPages - 1;
    } catch (e) {
        if (window.__staticDocsTableHtml) {
            table.innerHTML = window.__staticDocsTableHtml;
            if (typeof lucide !== 'undefined') lucide.createIcons();
        }
        info.textContent = 'Сервер недоступен — показаны документы из страницы (без API)';
        showToast(e.message || 'Ошибка API', 'error');
    }
}

function renderDocumentRow(doc) {
    var id = String(doc.id);
    var idAttr = id.replace(/\\/g, '\\\\').replace(/'/g, "\\'");
    var st = apiStatusToUiClass(doc.status);
    return (
        '<tr class="hover:bg-gray-50 cursor-pointer" onclick="openDocumentModal(\'' +
        idAttr +
        '\')">' +
        '<td class="px-6 py-4 whitespace-nowrap"><input type="checkbox" class="rounded" onclick="event.stopPropagation()"></td>' +
        '<td class="px-6 py-4 whitespace-nowrap">' +
        '<div class="flex items-center">' +
        '<div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center mr-3">' +
        '<i data-lucide="file-text" class="w-5 h-5 text-blue-600"></i></div>' +
        '<div><p class="font-medium text-gray-900">' +
        escapeHtml(doc.documentNumber || '') +
        '</p>' +
        '<p class="text-sm text-gray-500">' +
        escapeHtml(displayDocumentType(doc)) +
        '</p></div></div></td>' +
        '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">' +
        escapeHtml(doc.documentNumber || '') +
        '</td>' +
        '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">' +
        escapeHtml(doc.constructionObjectName || '—') +
        '</td>' +
        '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">' +
        escapeHtml(formatDateRu(doc.documentDate)) +
        '</td>' +
        '<td class="px-6 py-4 whitespace-nowrap">' +
        '<span class="status-badge status-' +
        st +
        '">' +
        getStatusText(doc.status) +
        '</span></td>' +
        '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">v' +
        (doc.currentVersion != null ? doc.currentVersion : '—') +
        '</td>' +
        '<td class="px-6 py-4 whitespace-nowrap">' +
        '<div class="flex items-center space-x-2">' +
        '<button type="button" class="text-blue-600 hover:text-blue-800" onclick="event.stopPropagation(); openDocumentModal(\'' +
        idAttr +
        '\')"><i data-lucide="eye" class="w-5 h-5"></i></button>' +
        '<button type="button" class="text-green-600 hover:text-green-800" onclick="event.stopPropagation(); downloadDocumentFromList(\'' +
        idAttr +
        '\')"><i data-lucide="download" class="w-5 h-5"></i></button>' +
        '</div></td></tr>'
    );
}

window.downloadDocumentFromList = async function (id) {
    try {
        await downloadDocument(id);
        showToast('Файл скачан');
    } catch (e) {
        showToast(e.message || 'Ошибка скачивания', 'error');
    }
};

function initializePagination() {
    document.getElementById('paginationPrev').addEventListener('click', function () {
        if (listState.page > 0) {
            listState.page -= 1;
            loadDocumentList();
        }
    });
    document.getElementById('paginationNext').addEventListener('click', function () {
        if (listState.totalPages > 0 && listState.page < listState.totalPages - 1) {
            listState.page += 1;
            loadDocumentList();
        }
    });
}

function initializeUpload() {
    var uploadZone = document.getElementById('uploadZone');
    var fileInput = document.getElementById('fileInput');

    uploadZone.addEventListener('click', function (e) {
        if (e.target.closest('button')) {
            e.stopPropagation();
            fileInput.click();
            return;
        }
        fileInput.click();
    });

    uploadZone.addEventListener('dragover', function (e) {
        e.preventDefault();
        uploadZone.classList.add('dragover');
    });

    uploadZone.addEventListener('dragleave', function () {
        uploadZone.classList.remove('dragover');
    });

    uploadZone.addEventListener('drop', function (e) {
        e.preventDefault();
        uploadZone.classList.remove('dragover');
        if (e.dataTransfer.files.length > 0) {
            handleFileUpload(e.dataTransfer.files[0]);
        }
    });

    fileInput.addEventListener('change', function (e) {
        if (e.target.files.length > 0) {
            handleFileUpload(e.target.files[0]);
        }
        e.target.value = '';
    });

    document.getElementById('uploadGoToListBtn').addEventListener('click', function () {
        resetUploadPanels();
        navigateTo('documents');
    });

    document.getElementById('uploadAnotherSuccessBtn').addEventListener('click', function () {
        resetUploadPanels();
    });

    document.getElementById('uploadAnotherErrorBtn').addEventListener('click', function () {
        resetUploadPanels();
    });
}

async function handleFileUpload(file) {
    if (!file.name.toLowerCase().endsWith('.xml')) {
        showToast('Пожалуйста, загрузите XML-файл', 'error');
        return;
    }

    resetUploadPanels();
    document.getElementById('uploadProgress').classList.remove('hidden');
    document.getElementById('uploadFileName').textContent = file.name;

    var progress = 0;
    var interval = setInterval(function () {
        progress = Math.min(95, progress + 12);
        document.getElementById('progressBar').style.width = progress + '%';
        document.getElementById('uploadPercent').textContent = progress + '%';
    }, 120);

    try {
        var result = await uploadDocument(file);
        clearInterval(interval);
        document.getElementById('progressBar').style.width = '100%';
        document.getElementById('uploadPercent').textContent = '100%';
        setTimeout(function () {
            document.getElementById('uploadProgress').classList.add('hidden');
            showUploadSuccess(result);
        }, 200);
    } catch (e) {
        clearInterval(interval);
        document.getElementById('uploadProgress').classList.add('hidden');
        showUploadFailure(e);
    }
}

function showUploadSuccess(result) {
    var doc = result.document || {};
    var validation = result.validation || {};
    var status = validation.status || '';

    document.getElementById('docNumber').textContent = doc.documentNumber || '—';
    document.getElementById('docDate').textContent = formatDateRu(doc.documentDate);
    document.getElementById('docObject').textContent = doc.constructionObjectName || '—';
    document.getElementById('docType').textContent = displayDocumentType(doc);

    var messages = validation.messages || [];
    var badge = document.getElementById('validationStatus');
    var stClass = apiStatusToUiClass(status);
    badge.className = 'status-badge status-' + stClass;
    if (status === 'VALID' || !status) {
        badge.textContent =
            messages.length > 0 ? 'Валидный документ (есть предупреждения XSD)' : 'Валидный документ';
    } else {
        badge.textContent = getStatusText(status);
    }

    var msgList = document.getElementById('validationMessagesList');
    if (messages.length > 0) {
        msgList.classList.remove('hidden');
        msgList.innerHTML = messages
            .map(function (m) {
                var line =
                    (m.severity || '') +
                    (m.line != null ? ' (стр. ' + m.line + ')' : '') +
                    ': ' +
                    (m.message || '');
                return '<li>' + escapeHtml(line) + '</li>';
            })
            .join('');
    } else {
        msgList.classList.add('hidden');
        msgList.innerHTML = '';
    }

    document.getElementById('validationResult').classList.remove('hidden');
    showToast('Документ сохранён');
}

function showUploadFailure(err) {
    var body = err.body || {};
    var main = err.message || 'Ошибка валидации';
    var details = body.details;
    var text = main;
    if (details && details.length) {
        text += '\n' + details.join('\n');
    }
    document.getElementById('errorMessage').textContent = text;
    document.getElementById('validationError').classList.remove('hidden');
    showToast('Ошибка валидации', 'error');
}

function setVersionsPanelVisible(show) {
    var panel = document.getElementById('versionsPanel');
    var label = document.getElementById('toggleVersionsBtnLabel');
    if (!panel || !label) return;
    if (show) {
        panel.classList.remove('hidden');
        label.textContent = 'Скрыть версии';
    } else {
        panel.classList.add('hidden');
        label.textContent = 'Показать версии';
    }
}

async function loadVersionsIntoModal(documentId) {
    var listEl = document.getElementById('versionsList');
    listEl.innerHTML = '<p class="text-sm text-gray-500">Загрузка версий…</p>';
    try {
        var versions = await getDocumentVersions(documentId);
        if (!versions.length) {
            listEl.innerHTML = '<p class="text-sm text-gray-500">Версий нет</p>';
            return;
        }
        listEl.innerHTML = versions
            .map(function (v, idx) {
                var isFirst = idx === 0;
                var st = apiStatusToUiClass(v.validationStatus);
                return (
                    '<div class="border border-gray-200 rounded-lg p-4 ' +
                    (isFirst ? '' : 'opacity-90') +
                    '">' +
                    '<div class="flex items-center justify-between flex-wrap gap-2">' +
                    '<div><p class="font-medium text-gray-900">Версия ' +
                    escapeHtml(String(v.versionNumber)) +
                    '</p>' +
                    '<p class="text-sm text-gray-500">' +
                    escapeHtml(formatIsoDateTime(v.createdAt)) +
                    ' · ' +
                    escapeHtml(v.fileName || '') +
                    '</p></div>' +
                    '<div class="flex items-center gap-2">' +
                    (isFirst ? '<span class="status-badge status-valid">Текущая</span>' : '') +
                    '<span class="status-badge status-' +
                    st +
                    '">' +
                    getStatusText(v.validationStatus) +
                    '</span>' +
                    '</div></div></div>'
                );
            })
            .join('');
    } catch (e) {
        listEl.innerHTML = '<p class="text-sm text-red-600">' + escapeHtml(e.message) + '</p>';
    }
    if (typeof lucide !== 'undefined') lucide.createIcons();
}

function initializeModal() {
    document.getElementById('closeModal').addEventListener('click', function () {
        document.getElementById('documentModal').classList.add('hidden');
        setVersionsPanelVisible(false);
        window.__modalDocumentId = null;
    });

    document.getElementById('documentModal').addEventListener('click', function (e) {
        if (e.target === document.getElementById('documentModal')) {
            document.getElementById('documentModal').classList.add('hidden');
            setVersionsPanelVisible(false);
            window.__modalDocumentId = null;
        }
    });

    document.getElementById('toggleVersionsBtn').addEventListener('click', async function () {
        var panel = document.getElementById('versionsPanel');
        var hidden = panel.classList.contains('hidden');
        setVersionsPanelVisible(hidden);
        if (hidden && window.__modalDocumentId) {
            await loadVersionsIntoModal(window.__modalDocumentId);
        }
        if (typeof lucide !== 'undefined') lucide.createIcons();
    });

    document.getElementById('modalDownloadBtn').addEventListener('click', async function () {
        if (!window.__modalDocumentId) return;
        try {
            await downloadDocument(window.__modalDocumentId);
            showToast('Файл скачан');
        } catch (e) {
            showToast(e.message || 'Ошибка скачивания', 'error');
        }
    });

    document.getElementById('modalNewVersionBtn').addEventListener('click', function () {
        document.getElementById('documentModal').classList.add('hidden');
        setVersionsPanelVisible(false);
        window.__modalDocumentId = null;
        navigateTo('upload');
    });
}

window.openDocumentModal = async function (docId) {
    var idStr = typeof docId === 'string' ? docId : String(docId);
    window.__modalDocumentId = idStr;
    document.getElementById('modalDocTitle').textContent = 'Загрузка…';
    document.getElementById('documentModal').classList.remove('hidden');
    setVersionsPanelVisible(false);
    document.getElementById('versionsList').innerHTML = '';

    try {
        var doc = await getDocumentById(idStr);
        document.getElementById('modalDocTitle').textContent = doc.documentNumber || 'Документ';
        document.getElementById('modalDocNumber').textContent = doc.documentNumber || '—';
        document.getElementById('modalDocDate').textContent = formatDateRu(doc.documentDate);
        document.getElementById('modalDocType').textContent = displayDocumentType(doc);
        document.getElementById('modalDocObject').textContent = doc.constructionObjectName || '—';
        var desc =
            doc.extractedMetadata && Object.keys(doc.extractedMetadata).length
                ? JSON.stringify(doc.extractedMetadata, null, 2)
                : doc.title || '';
        document.getElementById('modalDocDescription').textContent = desc || '—';
        var statusEl = document.getElementById('modalDocStatus');
        statusEl.textContent = getStatusText(doc.status);
        statusEl.className = 'status-badge status-' + apiStatusToUiClass(doc.status);
    } catch (e) {
        showToast(e.message || 'Не удалось загрузить карточку', 'error');
        document.getElementById('documentModal').classList.add('hidden');
        window.__modalDocumentId = null;
    }

    if (typeof lucide !== 'undefined') lucide.createIcons();
};

function escapeHtml(s) {
    if (s == null) return '';
    return String(s)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function showToast(message, type) {
    type = type || 'success';
    var toast = document.getElementById('toast');
    document.getElementById('toastMessage').textContent = message;
    var icon = toast.querySelector('i');
    if (type === 'error') {
        icon.setAttribute('data-lucide', 'alert-circle');
        icon.className = 'w-5 h-5 text-red-400';
    } else {
        icon.setAttribute('data-lucide', 'check-circle');
        icon.className = 'w-5 h-5 text-green-400';
    }
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
    toast.classList.remove('hidden');
    setTimeout(function () {
        toast.classList.add('hidden');
    }, 3000);
}

window.showToast = showToast;

document.addEventListener('DOMContentLoaded', function () {
    var tbl = document.getElementById('allDocsTable');
    if (tbl) {
        window.__staticDocsTableHtml = tbl.innerHTML;
    }
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
    initializeNavigation();
    initializeRoleToggle();
    initializeUpload();
    initializeModal();
    initializePagination();
    loadDocumentList();
});
