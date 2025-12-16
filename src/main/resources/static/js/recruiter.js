/**
 * Recruiter Portal Interactive Logic
 */

document.addEventListener('DOMContentLoaded', function () {
    console.log('Recruiter Portal Loaded');

    // Mobile Sidebar Toggle
    const sidebar = document.querySelector('.recruiter-sidebar');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const overlay = document.querySelector('.sidebar-overlay');

    if (sidebarToggle && sidebar && overlay) {
        sidebarToggle.addEventListener('click', function () {
            sidebar.classList.add('active');
            overlay.classList.add('active');
        });

        overlay.addEventListener('click', function () {
            sidebar.classList.remove('active');
            overlay.classList.remove('active');
        });
    }
});
