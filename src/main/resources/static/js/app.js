let dashboardKey = "";
const resources = [
    { key: 'campeonatos', label: 'Campeonatos', columns: [['id','ID'], ['nome','Nome']] },
    { key: 'estadios', label: 'Estádios', columns: [['id','ID'], ['nome','Nome'], ['endereco','Endereço']] },
    { key: 'equipes', label: 'Equipes', columns: [['id','ID'], ['nome','Nome'], ['estadio.nome','Estádio']] },
    { key: 'jogadores', label: 'Jogadores', columns: [['id','ID'], ['nome','Nome'], ['posicao','Posição'], ['equipe.nome','Equipe']] },
    { key: 'contratos', label: 'Contratos', columns: [['id','ID'], ['salario','Salário'], ['duracaoMeses','Meses'], ['jogador.nome','Jogador']] },
    { key: 'salarios', label: 'Salários', columns: [['id','ID'], ['valor','Valor'], ['cargo','Cargo']] }
];

function valueAt(obj, path) {
    return path.split('.').reduce((v, k) => v == null ? null : v[k], obj) ?? '—';
}
function extractItems(data) {
    if (!data || !data._embedded) return [];
    const first = Object.values(data._embedded).find(Array.isArray);
    return first || [];
}
function esc(value) {
    return String(value).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#039;'}[c]));
}
async function fetchResource(resource) {
    const response = await fetch(`/${resource.key}?page=0&size=100&sort=id,asc`, { headers: { Accept: 'application/hal+json', ...(dashboardKey ? {'X-API-Key':dashboardKey} : {}) } });
    if (!response.ok) throw new Error(`${resource.label}: HTTP ${response.status}`);
    const data = await response.json();
    return { resource, data, items: extractItems(data), total: data.page?.totalElements ?? 0 };
}
function renderTable(result) {
    const {resource, items, total} = result;
    const head = resource.columns.map(([,label]) => `<th>${esc(label)}</th>`).join('');
    const rows = items.length ? items.map(item => `<tr>${resource.columns.map(([path]) => `<td>${esc(valueAt(item,path))}</td>`).join('')}</tr>`).join('') : `<tr><td colspan="${resource.columns.length}" class="empty">Nenhum cadastro ainda.</td></tr>`;
    return `<section class="data-card"><div class="data-card-head"><div><span class="endpoint">/${resource.key}</span><h3>${resource.label}</h3></div><strong>${total}</strong></div><div class="table-wrap"><table><thead><tr>${head}</tr></thead><tbody>${rows}</tbody></table></div></section>`;
}
async function loadDashboard() {
    const target = document.getElementById('data-sections');
    const summary = document.getElementById('summary');
    target.innerHTML = '<p class="loading">Atualizando cadastros...</p>';
    const results = await Promise.all(resources.map(async r => {
        try { return await fetchResource(r); }
        catch (error) { return {resource:r, error:error.message, items:[], total:0}; }
    }));
    summary.innerHTML = results.map(r => `<div class="summary-card"><span>${esc(r.resource.label)}</span><strong>${r.error ? '!' : r.total}</strong></div>`).join('');
    target.innerHTML = results.map(r => r.error ? `<section class="data-card error-card"><h3>${esc(r.resource.label)}</h3><p>${esc(r.error)}</p></section>` : renderTable(r)).join('');
}

document.getElementById('refresh-data')?.addEventListener('click', loadDashboard);
const keyButton = document.createElement('button'); keyButton.textContent = 'Informar chave de API'; keyButton.addEventListener('click', () => { dashboardKey = prompt('Chave de API para acessar contratos e salários') || ''; loadDashboard(); }); document.getElementById('refresh-data')?.after(keyButton);
loadDashboard();

(async function () {
    try {
        const response = await fetch('/v3/api-docs', { headers: { Accept: 'application/json' } });
        document.documentElement.dataset.apiStatus = response.ok ? 'online' : 'offline';
    } catch (_) { document.documentElement.dataset.apiStatus = 'offline'; }
})();
