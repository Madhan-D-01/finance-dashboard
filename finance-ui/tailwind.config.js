/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        paper: '#F6F5F1',
        surface: '#FFFFFF',
        ink: '#1C2321',
        'ink-soft': '#5B6460',
        navy: '#1F2A3C',
        'navy-light': '#2C3A52',
        ledger: {
          green: '#1F5C4E',
          'green-soft': '#E4EEE9',
          brick: '#9C4A3A',
          'brick-soft': '#F3E6E1',
          gold: '#B08D4F'
        },
        line: '#DEDBD2'
      },
      fontFamily: {
        display: ['Fraunces', 'serif'],
        body: ['Inter', 'sans-serif'],
        mono: ['"IBM Plex Mono"', 'monospace']
      }
    }
  },
  plugins: []
}
