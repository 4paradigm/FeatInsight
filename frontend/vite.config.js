import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  base: '/openmldb-featinsight',
  build: {
    outDir: '../src/main/resources/static/'
  },
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0', // 或者 'localhost' 或者你想要绑定的其他可用的IP
    port: 5173,
  },
})
