<script setup>
import {useLayout} from '@/layout/composables/layout';
import {computed, ref} from 'vue';
import AppConfig from '@/layout/AppConfig.vue';
import {useRouter} from "vue-router";

const router = useRouter();
const { layoutConfig } = useLayout();
const email = ref('');
const password = ref('');
const checked = ref(false);
const isWrongCreds = ref(false);

const onSignInClick = () => {
  if (import.meta.env.PROD &&
    (email.value.length < 5 || password.value.length < 5 || isWrongCreds.value)) {
    isWrongCreds.value = true;
    return;
  }

  const myHeaders = new Headers();
  myHeaders.append('Content-Type', 'application/json');
  myHeaders.append('Accept', '*/*');

  const raw = JSON.stringify({
    email: email.value,
    password: password.value
  });

  const requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  fetch(window.$apiHost + '/auth/login', requestOptions)
    .then(res => res.json())
    .then(json => {
      if (!json) {
        isWrongCreds.value = true;
      } else if (json.err) {
        isWrongCreds.value = true;
      } else if (json.token) {
        localStorage.jwt = json.token;
        const from = router.currentRoute.value.query['from'];
        if (from) router.push(from);
        else router.push('/');
      }
    })
    .catch(() => isWrongCreds.value = true);
}

const logoUrl = computed(() => {
    return `/layout/images/${layoutConfig.darkTheme.value ? 'logo-white' : 'logo-dark'}.svg`;
});
</script>

<template>
    <div class="surface-ground flex align-items-center justify-content-center min-h-screen min-w-screen overflow-hidden">
        <div class="flex flex-column align-items-center justify-content-center">
            <img :src="logoUrl" alt="Sakai logo" class="mb-5 w-6rem flex-shrink-0" />
            <div style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
                <div class="w-full surface-card py-6 px-5 sm:px-8" style="border-radius: 53px">
                    <div class="text-center mb-5">
                        <div class="display-grid mb-3">
                            <span class="space-font text-900 text-3xl font-medium text-nowrap">Студент года 3.0</span>
                            <span class="space-font text-900 text-3xl font-medium text-nowrap">НИЯУ МИФИ</span>
                        </div>
                      <div class="display-grid mb-3">
                        <span class="text-600 font-medium">Войдите, чтобы продолжить</span>
                        <span v-if="isWrongCreds" class="font-medium text-red-600">Неверный логин или пароль.</span>
                      </div>
                    </div>

                    <div>
                      <label class="block text-900 text-xl font-medium mb-2" for="email">E-mail</label>
                      <InputText id="email" v-model.trim="email" class="w-full md:w-30rem mb-5" placeholder="noreply@mephi.ru"
                                 style="padding: 1rem" type="text" @change="isWrongCreds = false"/>

                      <label class="block text-900 font-medium text-xl mb-2" for="password">Пароль</label>
                      <Password id="password" v-model.trim="password" :inputStyle="{ padding: '1rem' }" class="w-full mb-3"
                                inputClass="w-full" placeholder="*************" toggleMask
                                @change="isWrongCreds = false"/>

                        <div class="flex align-items-center justify-content-between mb-5 gap-5">
                            <div class="flex align-items-center">
                              <Checkbox id="rememberme" v-model="checked" binary class="mr-2"></Checkbox>
                              <label for="rememberme">Запомнить меня</label>
                            </div>
                            <a class="font-medium no-underline ml-2 text-right cursor-pointer" style="color: var(--primary-color)">Забыли пароль?</a>
                        </div>
                        <Button @click="onSignInClick" label="Войти" class="w-full p-3 text-xl"></Button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <AppConfig simple />
</template>

<style scoped>
.pi-eye {
    transform: scale(1.6);
    margin-right: 1rem;
}

.pi-eye-slash {
    transform: scale(1.6);
    margin-right: 1rem;
}
</style>
