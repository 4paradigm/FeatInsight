import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    isAuthenticated: false,
    user: null,
    token: null,
    uuid: null,
  }),
  actions: {
    login(userDetails, token, uuid) {
      this.isAuthenticated = true;
      this.user = userDetails;
      this.token = token;
      this.uuid = uuid;
      localStorage.setItem('token', token); // Optionally store the token
      localStorage.setItem('uuid', uuid);
    },
    logout() {
      this.isAuthenticated = false;
      this.user = null;
      this.token = null;
      this.uuid = null;
      localStorage.removeItem('token'); // Remove the token
      localStorage.removeItem('uuid');
    },
    checkAuth() {
      const token = localStorage.getItem('token');
      if (token) {
        // Perform token validation or user fetching logic here
        // If valid, set isAuthenticated to true
        this.isAuthenticated = true;
        this.token = token;
        this.uuid = localStorage.getItem('uuid');
      }
    }
  }
});
