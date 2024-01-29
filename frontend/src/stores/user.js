import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    isAuthenticated: false,
    user: null,
    token: null,
  }),
  actions: {
    login(userDetails, token) {
      this.isAuthenticated = true;
      this.user = userDetails;
      this.token = token;
      localStorage.setItem('token', token); // Optionally store the token
    },
    logout() {
      this.isAuthenticated = false;
      this.user = null;
      this.token = null;
      localStorage.removeItem('token'); // Remove the token
    },
    checkAuth() {
      const token = localStorage.getItem('token');
      if (token) {
        // Perform token validation or user fetching logic here
        // If valid, set isAuthenticated to true
        this.isAuthenticated = true;
        this.token = token;
      }
    }
  }
});
