function sendEqualityTest() {
        alert("Equality Test Request Sent");
        var urlString = document.getElementById("url").value;
        var arg1 = document.getElementById("firstArg").value;
        var arg2 = document.getElementById("secondArg").value;
        var val1 = document.getElementById("firstValue").value;
        var val2 = document.getElementById("secondValue").value;
        alert(arg1);
        var ajax = new XMLHttpRequest();
        ajax.open('GET', 'http://localhost:8090/backend/equality?'+'urlBase=' + urlString + '&urlArgs=' +arg1 + ',' + arg2 + '&argValues=' + val1 + ',' + val2 , false);
        ajax.send();
        alert(ajax.responseText);
}

function sendEquivalenceTest() {
    alert("Equivalence Test Request Sent");
    var arg1 = document.getElementById("url1").value;
    var arg2 = document.getElementById("url2").value;
    var path = document.getElementById("path").value;
    var ajax = new XMLHttpRequest();
    ajax.open('GET', 'http://localhost:8090/backend/equivalence?'+'urlOne=' + arg1 + '&urlTwo=' +arg2 +'&checkPath=' + path, false);
    ajax.send();
    alert(ajax.responseText);
}

function sendSubsetTest() {
    alert("Subset Test Request Sent");
    var urlbase = document.getElementById("subsetURLBase").value;
    var values = document.getElementById("subsetValues").value;
    var path = document.getElementById("subsetPath").value;
    var ajax = new XMLHttpRequest();
    ajax.open('GET', 'http://localhost:8090/backend/subset?'+'urlBase=' + urlbase + '&values=' +values +'&checkPath=' + path, false);
    ajax.send();
    alert(ajax.responseText);
}

function sendDisjointTest() {
    alert("Disjoint Test Request Sent");
    var arg1 = document.getElementById("disjointURL1").value;
    var arg2 = document.getElementById("disjointURL2").value;
    var path = document.getElementById("disjointPath").value;
    var ajax = new XMLHttpRequest();
    ajax.open('GET', 'http://localhost:8090/backend/disjoint?'+'urlOne=' + arg1 + '&urlTwo=' +arg2 +'&checkPath=' + path, false);
    ajax.send();
    alert(ajax.responseText);
}

function sendCompleteTest() {
    alert("Complete Test Request Sent");
    var urlbase = document.getElementById("completeURLBase").value;
    var values = document.getElementById("completeURLValues").value;
    var path = document.getElementById("completeURLPath").value;
    var ajax = new XMLHttpRequest();
    ajax.open('GET', 'http://localhost:8090/backend/complete?'+'urlBase=' + urlbase + '&values=' +values +'&checkPath=' + path, false);
    ajax.send();
    alert(ajax.responseText);
}

function spotifyAPIAuth(){
    const hash = window.location.hash
        .substring(1)
        .split('&')
        .reduce(function (initial, item) {
            if (item) {
                var parts = item.split('=');
                initial[parts[0]] = decodeURIComponent(parts[1]);
            }
            return initial;
        }, {});
    window.location.hash = '';

// Set token
    let _token = hash.access_token;

    const authEndpoint = 'https://accounts.spotify.com/authorize';

// Replace with your spotify's client ID, redirect URI and desired scopes
    const clientId = 'df9f159b4ce540f8822fb7d00655ac50';
    const redirectUri = 'http://127.0.0.1:8887/testing.html';
    // const redirectUri = 'http://localhost:63342/Api%20Testing/APITestBackend/src/main/java/backend/Testing.html';
    const scopes = [
        'user-read-birthdate',
        'user-read-email',
        'user-read-private'
    ];

// If there is no token, redirect to Spotify authorization
    if (!_token) {
        window.location = `${authEndpoint}?client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scopes.join('%20')}&response_type=token`;
    }
}


function getArtistAlbum() {
    var access_token = $("#access-token").val();
    $.ajax({
        //  url: 'https://api.spotify.com/v1/me',
        url: 'https://api.spotify.com/v1/artists/1vCWHaC5f2uS3yhpwWbIA6/albums',
        headers: {
            'Authorization': 'Bearer ' + access_token
        },
        success: function (response) {
            console.log(response);
        }
    });
}