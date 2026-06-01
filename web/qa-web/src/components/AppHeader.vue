<template>
  <a-layout-header class="header">
    <div class="header-content">
      <div class="logo">
        <img src="https://images.pexels.com/photos/40568/medical-appointment-doctor-healthcare-40568.jpeg?auto=compress&cs=tinysrgb&w=100" alt="QA Live Healthcare" />
        <span>QA Live Healthcare</span>
      </div>
      <a-menu v-model:selectedKeys="selectedKeys" mode="horizontal" class="nav-menu">
        <a-menu-item key="home" @click="navigateTo('/')">
          <HomeOutlined />
          首页
        </a-menu-item>
        <a-menu-item key="consultation" @click="navigateTo('/consultation')">
          <MessageOutlined />
          问诊
        </a-menu-item>
        <a-menu-item key="doctors" @click="navigateTo('/doctors')">
          <TeamOutlined />
          医生
        </a-menu-item>
        <a-menu-item key="about" @click="navigateTo('/about')">
          <InfoCircleOutlined />
          关于
        </a-menu-item>
      </a-menu>
      <div class="header-right">
        <a-dropdown :trigger="['click']">
          <a-button class="lang-btn" @click.prevent>
            <GlobalOutlined />
            {{ currentLangLabel }}
            <DownOutlined />
          </a-button>
          <template #overlay>
            <a-menu @click="handleLangChange" :selected-keys="[locale]">
              <a-menu-item key="zh">中文</a-menu-item>
              <a-menu-item key="en">English</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
        <a-button v-if="!currentDoctor" type="primary" class="login-btn" @click="navigateTo('/doctor/login')">
          <UserOutlined />
          医生登录
        </a-button>
        <a-button v-else class="logout-btn" @click="handleDoctorLogout">
          <LogoutOutlined />
          退出 ({{ currentDoctor.name }})
        </a-button>
        <a-button v-if="!currentPatient" class="patient-login-btn" @click="navigateTo('/consultation')">
          <HeartOutlined />
          问诊登录
        </a-button>
        <a-button v-else class="logout-btn" @click="handlePatientLogout">
          <LogoutOutlined />
          退出问诊 ({{ currentPatient.name }})
        </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { HomeOutlined, MessageOutlined, TeamOutlined, InfoCircleOutlined, UserOutlined, GlobalOutlined, DownOutlined, LogoutOutlined, HeartOutlined } from '@ant-design/icons-vue';
import { store } from '../store';

const router = useRouter();
const route = useRoute();
const { locale } = useI18n();
const selectedKeys = ref<string[]>(['home']);

const currentLangLabel = computed(() => locale.value === 'zh' ? '中文' : 'English');

const handleLangChange = ({ key }: { key: string }) => {
  locale.value = key;
};

watch(() => route.path, (newPath) => {
  if (newPath === '/') {
    selectedKeys.value = ['home'];
  } else if (newPath.startsWith('/consultation')) {
    selectedKeys.value = ['consultation'];
  } else if (newPath.startsWith('/doctors')) {
    selectedKeys.value = ['doctors'];
  } else if (newPath.startsWith('/about')) {
    selectedKeys.value = ['about'];
  }
}, { immediate: true });

const currentDoctor = computed(() => store.state.currentDoctor);
const currentPatient = computed(() => store.state.currentPatient);

const handleDoctorLogout = () => {
  store.logoutDoctor();
  router.push('/');
};

const handlePatientLogout = () => {
  store.logoutPatient();
  router.push('/consultation');
};

const navigateTo = (path: string) => {
  router.push(path);
};
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 0;
  height: 64px;
  line-height: 64px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo img {
  height: 40px;
  width: 40px;
  border-radius: 8px;
  object-fit: cover;
}

.logo span {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.nav-menu {
  flex: 1;
  border: none;
  margin: 0 40px;
  line-height: 64px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.lang-btn {
  display: flex;
  align-items: center;
  gap: 4px;
}

.login-btn {
  background: #52c41a;
  border-color: #52c41a;
}

.login-btn:hover {
  background: #73d13d;
  border-color: #73d13d;
}

.logout-btn {
  color: #666;
  border-color: #d9d9d9;
}

.logout-btn:hover {
  color: #ff4d4f;
  border-color: #ff4d4f;
}

.patient-login-btn {
  background: #1890ff;
  border-color: #1890ff;
  color: #fff;
}

.patient-login-btn:hover {
  background: #40a9ff;
  border-color: #40a9ff;
}
</style>
