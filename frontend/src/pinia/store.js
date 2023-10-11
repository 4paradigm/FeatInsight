import { defineStore } from 'pinia';

// Define your Pinia store
export const SQLStore = defineStore('SQLStore', {
  state: () => ({
    sharedSQL: null, // Initialize with your desired default value
  }),
  actions: {
    // Define actions to modify the sharedVariable
    setSharedVariable(newValue) {
      this.sharedSQL = newValue;
    },
  },
});

