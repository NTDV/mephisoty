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

  download(fileId) {
    return fetchApi('/file/' + fileId)
      .then(async (resp) => {
        const contentDisposition = resp.headers.get('Content-Disposition');
        let filename = contentDisposition.split('"')[1];

        return [filename, await resp.blob()];
      })
      .then(([filename, blob]) => {
        const fileURL = URL.createObjectURL(blob);

        const fileLink = document.createElement('a');
        fileLink.href = fileURL;
        fileLink.download = filename;

        fileLink.click();
        return fileURL;
      })
      .then(fileUrl => URL.revokeObjectURL(fileUrl));
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

  decodeMimeEncodedWord(encodedWord) {
    const match = encodedWord.match(/=\?([^?]+)\?([BQ])\?([^?]+)\?=/i);
    if (!match) {
      throw new Error('Invalid encoded word format');
    }

    const charset = match[1];
    const encoding = match[2].toUpperCase();
    const encodedText = match[3];

    let decodedText;
    if (encoding === 'B') {
      decodedText = atob(encodedText);
    } else if (encoding === 'Q') {
      decodedText = encodedText.replace(/_/g, ' ').replace(/=([A-Fa-f0-9]{2})/g, (match, hex) => String.fromCharCode(parseInt(hex, 16)));
    } else {
      throw new Error('Unsupported encoding');
    }

    return new TextDecoder(charset).decode(new Uint8Array([...decodedText].map(char => char.charCodeAt(0))));
  }
}
