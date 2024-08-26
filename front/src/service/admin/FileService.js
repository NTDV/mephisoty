import fetchApi from '@/main';

export class FileService {
  getFileUrl(fileId) {
    return fetchApi('/file/' + fileId)
      .then((resp) => resp.blob())
      .then(blob => URL.createObjectURL(blob));
  }

  getAll(lazyParams) {
    return fetchApi('/file/all/', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  upload(file, accessPolicy = null, code = null, newFileName = null) {
    const form = new FormData();
    form.append('file', file);

    if (accessPolicy) form.append('accessPolicy', accessPolicy);
    if (code) form.append('code', code);
    if (newFileName) form.append('newFileName', newFileName);

    return fetchApi('/file/', form, true)
      .then((resp) => resp.data);
  }

  edit(fileId, file, accessPolicy = null, code = null, newFileName = null) {
    const form = new FormData();
    form.append('file', file);

    if (accessPolicy) form.append('accessPolicy', accessPolicy);
    if (code) form.append('code', code);
    if (newFileName) form.append('newFileName', newFileName);

    return fetchApi('/file/' + fileId, form, true)
      .then((resp) => resp.data);
  }
}
