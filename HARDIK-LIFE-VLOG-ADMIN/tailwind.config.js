/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        brand: {
          orange: '#FF5722',
          amber: '#FFB300',
          dark: '#0D0D0D',
          surface: '#161616',
          card: '#1E1E1E',
          border: '#2C2C2C',
        }
      }
    },
  },
  plugins: [],
}
