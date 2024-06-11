// Define the API URL
const apiUrl = 'api/trade/pairs?pairIds=' +
'JUPyiwrYJFskUPiHa7hkeR8VUtAeFoSYbKedZNsDvCN,' +
'EKpQGSJtjMFqKZ9KQanSqYXRcF8fBopzLHYxdM65zcjm,' +
'7GCihgDB8fe6KNjn2MYtkzZcRjQy3t9GHdC8uHYmW2hr,' +
'8wXtPeU6557ETkp9WHFY1n1EcU6NxDvbAggHGsMYiHsB,' +
'A3eME5CetyZPBoWbRUwY3tSe25S6tb18ba9ZPbWk9eFJ';

let tokens = [];

async function fetchData() {
    try {
        const response = await fetch(apiUrl, { method: 'GET' });
        const data = await response.json();
        tokens = data.pairs.map(pair => ({
            name: pair.name,
            price: `${'$'+pair.priceUsd.toFixed(4).toString()}`,
            change: `${pair.usd_24h_change.toFixed(2)}%`
        }));
        console.log(tokens);
    } catch (error) {
        console.error('Error:', error);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    fetchData().then(() => {
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
                <td>${token.name}</td>
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
});
