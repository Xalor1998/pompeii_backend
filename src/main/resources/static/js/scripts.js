// Define the API URL
const apiUrl = 'api/trade/pairs?pairIds=' +
'JUPyiwrYJFskUPiHa7hkeR8VUtAeFoSYbKedZNsDvCN,' +
'EKpQGSJtjMFqKZ9KQanSqYXRcF8fBopzLHYxdM65zcjm,' +
'7GCihgDB8fe6KNjn2MYtkzZcRjQy3t9GHdC8uHYmW2hr,' +
'8wXtPeU6557ETkp9WHFY1n1EcU6NxDvbAggHGsMYiHsB,' +
'A3eME5CetyZPBoWbRUwY3tSe25S6tb18ba9ZPbWk9eFJ,' +
'DezXAZ8z7PnrnRJjz3wXBoRgixCa6xjnB7YaB1pPB263,' +
'85VBFQZC9TZkfaptBWjvUw7YbZjy52A6mjtPGjstQAmQ,' +
'WENWENvqqNya429ubCdR81ZmD69brwQaaBYY6p3LCpk,' +
'METAewgxyPbgwsseH8T16a39CQ5VyVxZi9zXiDPY18m,' +
'HLptm5e6rTgh4EKgDpYFrnRHbjpkMyVdEeREEa2G7rf9,' +
'MangoCzJ36AjZyKwVj3VnYU4GTonjfVEnJmvvWaxLac,' +
'CxrhHSqyW8YTDWc4csJMMgo7uBUJSXzNzrWhtw9ULdru,' +
'7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU';

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

const getProvider = () => {
    const provider = window.phantom?.solana;
    if (provider) {
        if (provider.isPhantom) {
            return provider;
        } else {
            return 'Please log in to your Phantom Wallet.';
        }
    } else {
        return 'Phantom Wallet is not installed.';
    }
};

let walletAddresses = [];

document.querySelectorAll('.content-view#wallets-content .btn-primary').forEach(button => {
    button.addEventListener('click', async () => {
        const panel = button.closest('.panel');
        const walletAddressElement = panel.querySelector('p');

        console.log(walletAddresses);

        const providerMessage = getProvider();
        if (typeof providerMessage === 'string') {
            walletAddressElement.textContent = providerMessage;
        } else {
            try {
                const provider = await providerMessage.connect();
                const publicKey = provider.publicKey.toString();

                if (walletAddresses.includes(publicKey)) {
                    walletAddressElement.textContent = 'Wallet address already used';
                    return;
                }

                walletAddresses.push(publicKey); // Add the new wallet address to the set

                walletAddressElement.textContent = publicKey;

                const walletHeader = panel.querySelector('h4');
                walletHeader.textContent = `Connected Wallet`;

                // Transform the button into a textfield-like element
                button.textContent = 'Loading balance...';
                button.disabled = true;
                button.style.backgroundColor = '#2ecc71';
                button.style.border = '1px solid #000000';
                button.style.color = '#000000';
                button.style.opacity = 1;
                button.classList.add('balance-button');

                const balance = await getBalance(publicKey);

                // Update the transformed button with the balance
                button.textContent = `SOL Balance: ${balance}`;
            } catch (err) {
                walletAddressElement.textContent = `Error: ${err.message}`;
            }
        }
    });
});


async function getBalance(publicKey) {
    try {
        const response = await fetch('https://api.devnet.solana.com', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                jsonrpc: '2.0',
                id: 1,
                method: 'getBalance',
                params: [publicKey]
            })
        });

        const data = await response.json();

        if (data && data.result && data.result.value !== undefined) {
            return data.result.value;
        } else {
            console.error('Unexpected response format:', data);
            throw new Error('Unexpected response format.');
        }
    } catch (error) {
        console.error('Error:', error);
        throw new Error(error.message);
    }
}
