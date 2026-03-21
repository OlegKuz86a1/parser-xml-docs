function tabContentId(tabName) {
    return 'tab' + tabName.charAt(0).toUpperCase() + tabName.slice(1);
}

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
}

function updatePageHeader(page) {
    var titles = {
        documents: { title: 'Документы', desc: 'Список документов; версии и история — в карточке документа' },
        upload: { title: 'Загрузка документов', desc: 'Загрузка и валидация XML-файлов' },
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

    document.getElementById('validationResult').addEventListener('click', function (e) {
        var btn = e.target.closest('button');
        if (!btn) return;
        var text = (btn.textContent || '').trim();
        if (text === 'Загрузить другой' || text === 'Сохранить документ') {
            if (text === 'Загрузить другой') {
                resetUploadPanels();
                showToast('Можно выбрать новый файл');
            } else {
                showToast('Документ сохранён (демо)');
            }
        }
    });

    document.getElementById('validationError').addEventListener('click', function (e) {
        var btn = e.target.closest('button');
        if (!btn) return;
        resetUploadPanels();
        showToast('Выберите файл для загрузки');
    });
}

function handleFileUpload(file) {
    if (!file.name.toLowerCase().endsWith('.xml')) {
        showToast('Пожалуйста, загрузите XML-файл', 'error');
        return;
    }

    resetUploadPanels();
    document.getElementById('uploadProgress').classList.remove('hidden');
    document.getElementById('uploadFileName').textContent = file.name;

    var progress = 0;
    var interval = setInterval(function () {
        progress += 10;
        document.getElementById('progressBar').style.width = progress + '%';
        document.getElementById('uploadPercent').textContent = progress + '%';

        if (progress >= 100) {
            clearInterval(interval);
            setTimeout(function () {
                document.getElementById('uploadProgress').classList.add('hidden');
                if (Math.random() > 0.3) {
                    showValidationSuccess(file);
                } else {
                    showValidationError('Элемент \'constructionObject\' обязателен, но не найден');
                }
            }, 500);
        }
    }, 200);
}

function showValidationSuccess(file) {
    document.getElementById('docNumber').textContent = 'АО-2024-001';
    document.getElementById('docDate').textContent = new Date().toLocaleDateString('ru-RU');
    document.getElementById('docObject').textContent = 'ЖК "Северный"';
    document.getElementById('docType').textContent = 'Акт разбивки осей';
    document.getElementById('validationResult').classList.remove('hidden');
    showToast('Документ успешно валидирован');
}

function showValidationError(message) {
    document.getElementById('errorMessage').textContent = message;
    document.getElementById('validationError').classList.remove('hidden');
    showToast('Обнаружены ошибки валидации', 'error');
}

function renderDocumentList() {
    var table = document.getElementById('allDocsTable');
    table.innerHTML = mockData.documents.map(function (doc) {
        return (
            '<tr class="hover:bg-gray-50 cursor-pointer" onclick="openDocumentModal(' + doc.id + ')">' +
            '<td class="px-6 py-4 whitespace-nowrap"><input type="checkbox" class="rounded"></td>' +
            '<td class="px-6 py-4 whitespace-nowrap">' +
            '<div class="flex items-center">' +
            '<div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center mr-3">' +
            '<i data-lucide="file-text" class="w-5 h-5 text-blue-600"></i></div>' +
            '<div><p class="font-medium text-gray-900">' + escapeHtml(doc.number) + '</p>' +
            '<p class="text-sm text-gray-500">' + escapeHtml(doc.type) + '</p></div></div></td>' +
            '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">' + escapeHtml(doc.number) + '</td>' +
            '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">' + escapeHtml(doc.object) + '</td>' +
            '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">' + escapeHtml(doc.date) + '</td>' +
            '<td class="px-6 py-4 whitespace-nowrap">' +
            '<span class="status-badge status-' + doc.status + '">' + getStatusText(doc.status) + '</span></td>' +
            '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">v' + doc.version + '</td>' +
            '<td class="px-6 py-4 whitespace-nowrap">' +
            '<div class="flex items-center space-x-2">' +
            '<button type="button" class="text-blue-600 hover:text-blue-800" onclick="event.stopPropagation(); openDocumentModal(' + doc.id + ')"><i data-lucide="eye" class="w-5 h-5"></i></button>' +
            '<button type="button" class="text-green-600 hover:text-green-800" onclick="event.stopPropagation()"><i data-lucide="download" class="w-5 h-5"></i></button>' +
            '<button type="button" class="text-gray-600 hover:text-gray-800" onclick="event.stopPropagation()"><i data-lucide="more-vertical" class="w-5 h-5"></i></button>' +
            '</div></td></tr>'
        );
    }).join('');
}

function escapeHtml(s) {
    if (!s) return '';
    return String(s)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function getStatusText(status) {
    if (status === 'valid') return 'Валидный';
    if (status === 'invalid') return 'Ошибка';
    return 'На проверке';
}

function initializeModal() {
    document.getElementById('closeModal').addEventListener('click', function () {
        document.getElementById('documentModal').classList.add('hidden');
    });

    document.getElementById('documentModal').addEventListener('click', function (e) {
        if (e.target === document.getElementById('documentModal')) {
            document.getElementById('documentModal').classList.add('hidden');
        }
    });

    document.querySelectorAll('#documentModal [data-tab]').forEach(function (tab) {
        tab.addEventListener('click', function () {
            document.querySelectorAll('#documentModal [data-tab]').forEach(function (t) {
                t.classList.remove('tab-active');
                t.classList.add('text-gray-500');
            });
            tab.classList.add('tab-active');
            tab.classList.remove('text-gray-500');

            document.querySelectorAll('#documentModal .tab-content').forEach(function (c) {
                c.classList.add('hidden');
            });
            var id = tabContentId(tab.getAttribute('data-tab'));
            var panel = document.getElementById(id);
            if (panel) panel.classList.remove('hidden');
        });
    });
}

window.openDocumentModal = function (docId) {
    var doc = mockData.documents.find(function (d) { return d.id === docId; });
    if (!doc) return;

    document.getElementById('modalDocTitle').textContent = doc.number;
    document.getElementById('modalDocNumber').textContent = doc.number;
    document.getElementById('modalDocDate').textContent = doc.date;
    document.getElementById('modalDocType').textContent = doc.type;
    document.getElementById('modalDocObject').textContent = doc.object;
    document.getElementById('modalDocDescription').textContent = doc.description || '';
    var statusEl = document.getElementById('modalDocStatus');
    statusEl.textContent = getStatusText(doc.status);
    statusEl.className = 'status-badge status-' + doc.status;

    var versionsList = document.getElementById('versionsList');
    var prevVersion = Math.max(1, doc.version - 1);
    versionsList.innerHTML =
        '<div class="border border-gray-200 rounded-lg p-4">' +
        '<div class="flex items-center justify-between">' +
        '<div><p class="font-medium text-gray-900">Версия ' + doc.version + '</p>' +
        '<p class="text-sm text-gray-500">Загружено ' + escapeHtml(doc.date) + ' пользователем ' + escapeHtml(doc.author) + '</p></div>' +
        '<span class="status-badge status-valid">Текущая</span></div></div>' +
        '<div class="border border-gray-200 rounded-lg p-4 opacity-60">' +
        '<div class="flex items-center justify-between">' +
        '<div><p class="font-medium text-gray-900">Версия ' + prevVersion + '</p>' +
        '<p class="text-sm text-gray-500">Загружено 10.01.2024 пользователем ' + escapeHtml(doc.author) + '</p></div>' +
        '<button type="button" class="text-blue-600 hover:text-blue-800 text-sm">Скачать</button></div></div>';

    var historyList = document.getElementById('historyList');
    historyList.innerHTML = mockData.auditLogs
        .filter(function (l) { return l.document === doc.number; })
        .map(function (log) {
            return (
                '<div class="flex items-start space-x-3 p-3 bg-gray-50 rounded-lg">' +
                '<div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center flex-shrink-0">' +
                '<i data-lucide="user" class="w-4 h-4 text-blue-600"></i></div>' +
                '<div class="flex-1">' +
                '<p class="text-sm font-medium text-gray-900">' + escapeHtml(log.event) + '</p>' +
                '<p class="text-xs text-gray-500">' + escapeHtml(log.time) + ' • ' + escapeHtml(log.user) + '</p>' +
                '<p class="text-sm text-gray-700 mt-1">' + escapeHtml(log.details) + '</p></div></div>'
            );
        }).join('');

    document.getElementById('documentModal').classList.remove('hidden');
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
};

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
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
    initializeNavigation();
    initializeRoleToggle();
    initializeUpload();
    initializeModal();
    renderDocumentList();
});
