<script setup>
import { useToast } from 'primevue/usetoast';
import axios from 'axios';

const toast = useToast();

const uploader = async (event) => {
  const form = new FormData();
  form.append('file', event.files[0]);
  form.append('delimiter', ';');
  form.append('hasHeader', true);
  axios.post('http://localhost:8080/admin/achievement/import/1', form, {
    headers: {
      'Authorization': `Bearer ${localStorage.jwt}`,
      'Content-Type': 'multipart/form-data'
    }
  }).then((e) => toast.add({ severity: 'info', summary: 'Success', detail: e, life: 3000 }));
};
</script>

<template>
  <div class="grid">
    <div class="col-12">
      <div class="card">
        <h5>Advanced</h5>
        <FileUpload :maxFileSize="30 * (8 * 1024 * 1024)" :multiple="false" accept=".csv"
                    customUpload name="file"
                    @uploader="uploader">
          <template #empty>
            <p>Перетащите файл, чтобы загрузить его.</p>
          </template>
        </FileUpload>
      </div>
    </div>
  </div>
</template>
