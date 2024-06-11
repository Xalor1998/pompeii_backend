document.addEventListener('DOMContentLoaded', () => {
    const tokens = [
        { pair: 'SOL/TOPG', price: '45.12', change: '5.23%' },
        { pair: 'SOL/boden', price: '0.001', change: '-1.02%' },
        { pair: 'SOL/tremp', price: '0.025', change: '3.45%' },
        { pair: 'boden/tremp', price: '10.25', change: '-2.50%' },
        { pair: 'GME/WIF', price: '100.00', change: '7.80%' },
        { pair: 'WIF/Pepe', price: '200.50', change: '-3.20%' },
        { pair: 'SOL/Pepe', price: '150.20', change: '2.75%' },
        { pair: 'EGG/Pepe', price: '50.75', change: '-1.50%' },
        { pair: 'MOTHER/boden', price: '80.10', change: '4.90%' },
        { pair: 'DUKO/WIF', price: '70.30', change: '-0.80%' }
    ];

    // Function to populate a table body with token data
    function populateTableBody(tableBody, data) {
        tableBody.innerHTML = ''; // Clear existing content
        data.forEach(token => {
            const row = document.createElement('tr');
            const change = parseFloat(token.change);
            let changeClass = '';
            if (change > 0) {
                changeClass = 'positive-change';
            } else if (change < 0) {
                changeClass = 'negative-change';
            }
            row.innerHTML = `
                <td>${token.pair}</td>
                <td>${token.price}</td>
                <td class="${changeClass}">${token.change}</td>
            `;
            tableBody.appendChild(row);
        });
    }

    // Populate the top trending pairs table
    const topTrendingTableBody = document.getElementById('topTrendingTable').querySelector('tbody');
    populateTableBody(topTrendingTableBody, tokens.slice(0, 3));

    // Populate the top gainer pairs table
    const topGainersTableBody = document.getElementById('topGainersTable').querySelector('tbody');
    populateTableBody(topGainersTableBody, tokens.slice().sort((a, b) => parseFloat(b.change) - parseFloat(a.change)).slice(0, 3));

    // Populate the top loser pairs table
    const topLosersTableBody = document.getElementById('topLosersTable').querySelector('tbody');
    populateTableBody(topLosersTableBody, tokens.slice().sort((a, b) => parseFloat(a.change) - parseFloat(b.change)).slice(0, 3));

    // Populate the all tokens table
    const allTokensTableBody = document.getElementById('allTokensTable').querySelector('tbody');
    populateTableBody(allTokensTableBody, tokens);

    // Navigation logic
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            const view = this.getAttribute('data-view');
            document.querySelectorAll('.content-view').forEach(view => {
                view.style.display = 'none';
            });
            document.getElementById(view).style.display = 'block';
        });
    });

    // Add event listener to each nav link
    document.querySelectorAll('.navbar-nav .nav-link').forEach(link => {
        link.addEventListener('click', () => {
            // Remove 'active' class from all nav links
            document.querySelectorAll('.navbar-nav .nav-link').forEach(link => {
                link.classList.remove('active');
            });
            // Add 'active' class to the clicked nav link
            link.classList.add('active');

            // Get the view to display based on the data-view attribute of the clicked link
            const view = link.getAttribute('data-view');
            // Hide all content views
            document.querySelectorAll('.content-view').forEach(view => {
                view.style.display = 'none';
            });
            // Display the selected content view
            document.getElementById(view).style.display = 'block';
        });
    });
});
