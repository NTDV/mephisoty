export class ToastService {
  constructor(toast) {
    this.toast = toast;
  }

  isServerError(res) {
    if (!res || res.err) {
      this.showServerError(res);
      return true;
    }
    return false;
  }

  showServerError(err) {
    console.error(err);
    this.toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Данные не изменены', life: 3000});
  }

  showClientError(err) {
    console.error(err);
    this.toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Данные не изменены', life: 3000});
  }

  showValidationWarn() {
    this.toast.add({severity: 'warn', summary: 'Внимание', detail: 'Неверное заполнение', life: 3000});
  }

  showAuditError(err) {
    console.error(err);
    this.toast.add({severity: 'error', summary: 'Ошибка', detail: 'Данные аудита не получены', life: 3000});
  }

  showEditedSuccess() {
    this.toast.add({severity: 'success', summary: 'Успешно', detail: 'Данные изменены', life: 3000});
  }

  showDeletedSuccess() {
    this.toast.add({severity: 'success', summary: 'Успешно', detail: 'Данные удалены', life: 3000});
  }

  showAccessError(e) {
    console.error(e);
    this.toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Не удается получить доступ', life: 3000 });
  }
}