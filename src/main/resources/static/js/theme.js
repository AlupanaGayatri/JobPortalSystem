/**
 * Theme Management for Job Portal
 * Handles theme switching, persistence, and custom theme logic.
 */

const ThemeManager = {
    // Available themes
    themes: {
        'default': 'Old Photograph',
        'ocean': 'Ocean Breeze',
        'sunset': 'Sunset Gold',
        'midnight': 'Midnight Violet',
        'berry': 'Berry Blast',
        'custom': 'Custom Theme'
    },

    init() {
        // Load saved theme
        const savedTheme = localStorage.getItem('theme') || 'default';
        this.applyTheme(savedTheme);

        // Load custom colors if custom theme is active
        if (savedTheme === 'custom') {
            this.applyCustomColors();
        }

        // Expose global function for UI
        window.setTheme = (themeName) => this.setTheme(themeName);
        window.updateCustomColor = (key, value) => this.updateCustomColor(key, value);
    },

    setTheme(themeName) {
        if (!this.themes[themeName]) return;

        localStorage.setItem('theme', themeName);
        this.applyTheme(themeName);

        if (themeName === 'custom') {
            this.applyCustomColors();
        } else {
            this.clearCustomColors();
        }
    },

    applyTheme(themeName) {
        const root = document.documentElement;
        if (themeName === 'default') {
            root.removeAttribute('data-theme');
        } else {
            root.setAttribute('data-theme', themeName);
        }
    },

    updateCustomColor(key, value) {
        const customColors = JSON.parse(localStorage.getItem('customColors') || '{}');
        customColors[key] = value;
        localStorage.setItem('customColors', JSON.stringify(customColors));

        if (localStorage.getItem('theme') === 'custom') {
            this.applyCustomColors();
        }
    },

    applyCustomColors() {
        const customColors = JSON.parse(localStorage.getItem('customColors') || '{}');
        const root = document.documentElement;

        if (customColors.primary) root.style.setProperty('--primary', customColors.primary);
        if (customColors.accent) root.style.setProperty('--accent', customColors.accent);
        if (customColors.bgLight) root.style.setProperty('--bg-light', customColors.bgLight);
        if (customColors.textDark) root.style.setProperty('--text-dark', customColors.textDark);

        // Auto-generate hover shades if not provided (simple darkening)
        // This is a basic implementation. Ideally, use a color manipulation lib.
    },

    clearCustomColors() {
        const root = document.documentElement;
        root.style.removeProperty('--primary');
        root.style.removeProperty('--accent');
        root.style.removeProperty('--bg-light');
        root.style.removeProperty('--text-dark');
        // Add others as needed
    },

    // Helper to get current custom color or default
    getCustomColor(key, defaultVal) {
        const customColors = JSON.parse(localStorage.getItem('customColors') || '{}');
        return customColors[key] || defaultVal;
    }
};

// Initialize on load
document.addEventListener('DOMContentLoaded', () => ThemeManager.init());

// Also run immediately to prevent flash
(function () {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme && savedTheme !== 'default') {
        document.documentElement.setAttribute('data-theme', savedTheme);
        if (savedTheme === 'custom') {
            const customColors = JSON.parse(localStorage.getItem('customColors') || '{}');
            const root = document.documentElement;
            if (customColors.primary) root.style.setProperty('--primary', customColors.primary);
            if (customColors.accent) root.style.setProperty('--accent', customColors.accent);
            if (customColors.bgLight) root.style.setProperty('--bg-light', customColors.bgLight);
        }
    }
})();
