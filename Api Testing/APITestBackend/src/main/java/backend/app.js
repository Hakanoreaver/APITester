function sendEqualityTest() {
        alert("Equality Test Request Sent");
        document.getElementById("results").value = "... Waiting for Response ...";
        var urlString = document.getElementById("url").value;

        // Get dynamic values
        var numArguments = document.getElementById("argumentNumber").value;
        var urlArguments;
        var argValues;
        if(numArguments==1){     
            var arg1 = document.getElementById("firstArg").value;
            var val1 = document.getElementById("firstValue").value;
            urlArguments = arg1;
            argValues = val1;
        }else if (numArguments==2){
            var arg1 = document.getElementById("firstArg").value;
            var arg2 = document.getElementById("secondArg").value;
            var val1 = document.getElementById("firstValue").value;
            var val2 = document.getElementById("secondValue").value;
            urlArguments = arg1 + ',' + arg2;
            argValues = val1 + ',' + val2
        }else if (numArguments==3){
            var arg1 = document.getElementById("firstArg").value;
            var arg2 = document.getElementById("secondArg").value;
            var arg3 = document.getElementById("thirdArg").value;
            var val1 = document.getElementById("firstValue").value;
            var val2 = document.getElementById("secondValue").value;
            var val3 = document.getElementById("thirdValue").value;
            urlArguments = arg1 + ',' + arg2 + ',' + arg3;
            argValues = val1 + ',' + val2 + ',' + val3;
        }else if (numArguments==4){
            var arg1 = document.getElementById("firstArg").value;
            var arg2 = document.getElementById("secondArg").value;
            var arg3 = document.getElementById("thirdArg").value;
            var arg4 = document.getElementById("fourthArg").value;
            var val1 = document.getElementById("firstValue").value;
            var val2 = document.getElementById("secondValue").value;
            var val3 = document.getElementById("thirdValue").value;
            var val4 = document.getElementById("fourthValue").value;
            urlArguments = arg1 + ',' + arg2 + ',' + arg3 + ',' + arg4;
            argValues = val1 + ',' + val2 + ',' + val3 + ',' + val4;
        }

        var name = document.getElementById("header-name").value;
        var token = document.getElementById("header-value").value + ' ' + document.getElementById("access-token").value;
        var ajax = new XMLHttpRequest();
        ajax.open('GET', 'http://localhost:8090/backend/equality?'+'urlBase=' + urlString + '&urlArgs=' + urlArguments + '&argValues=' + argValues  + '&name=' +  name + '&token=' + token, false);
        ajax.send();
        document.getElementById("results").value = ajax.responseText;
}

function sendEquivalenceTest() {
    alert("Equivalence Test Request Sent");
    document.getElementById("results").value = '... Waiting for Response ...';
    var arg1 = document.getElementById("url1").value;
    var arg2 = document.getElementById("url2").value;
    var path = document.getElementById("path").value;
    var name = document.getElementById("header-name").value;
    var token = document.getElementById("header-value").value + ' ' + document.getElementById("access-token").value;
    var ajax = new XMLHttpRequest();
    ajax.open('GET', 'http://localhost:8090/backend/equivalence?'+'urlOne=' + arg1 + '&urlTwo=' +arg2 +'&checkPath=' + path + '&name=' +  name + '&token=' + token, false);
    ajax.send();
    document.getElementById("results").value = ajax.responseText;
}

function sendSubsetTest() {
    document.getElementById("results").value = '... Waiting for Response ...';
    alert("Subset Test Request Sent");
    var urlbase = document.getElementById("subsetURLBase").value;
    var values = document.getElementById("subsetValues").value;
    var path = document.getElementById("subsetPath").value;
    var name = document.getElementById("header-name").value;
    var token = document.getElementById("header-value").value + ' ' + document.getElementById("access-token").value;
    var ajax = new XMLHttpRequest();
    ajax.open('GET', 'http://localhost:8090/backend/subset?'+'urlBase=' + urlbase + '&values=' +values +'&checkPath=' + path + '&name=' +  name + '&token=' + token, false);
    ajax.send();
    document.getElementById("results").value = ajax.responseText;
}

function sendDisjointTest() {
    alert("Disjoint Test Request Sent");
    document.getElementById("results").value = "... Waiting for Response ...";
    var arg1 = document.getElementById("disjointURL1").value;
    var arg2 = document.getElementById("disjointURL2").value;
    var path = document.getElementById("disjointPath").value;
     var name = document.getElementById("header-name").value;
    var token = document.getElementById("header-value").value + ' ' + document.getElementById("access-token").value;
    var ajax = new XMLHttpRequest();
    ajax.open('GET', 'http://localhost:8090/backend/disjoint?'+'urlOne=' + arg1 + '&urlTwo=' +arg2 +'&checkPath=' + path + '&name=' +  name + '&token=' + token, false);
    ajax.send();
    alert(ajax.responseText);
}

function sendCompleteTest() {
    alert("Complete Test Request Sent");
    document.getElementById("results").value = "... Waiting for Response ...";
    var urlbase = document.getElementById("completeURLBase").value;
    var values = document.getElementById("completeURLValues").value;
    var path = document.getElementById("completeURLPath").value;
     var name = document.getElementById("header-name").value;
    var token = document.getElementById("header-value").value + ' ' + document.getElementById("access-token").value;
    var ajax = new XMLHttpRequest();
    ajax.open('GET', 'http://localhost:8090/backend/complete?'+'urlBase=' + urlbase + '&values=' +values +'&checkPath=' + path + '&name=' +  name + '&token=' + token, false);
    ajax.send();
    document.getElementById("results").value = ajax.responseText;
}


function sendDifferenceTest() {
    alert("Difference Test Request Sent");
    document.getElementById("results").value = "... Waiting for Response ...";
    var url1 = document.getElementById("diffrenceFirstURL").value;
    var url2 = document.getElementById("diffrenceSecondURL").value;
    var path = document.getElementById("differenceURLPath").value;
    var name = document.getElementById("header-name").value;
    var token = document.getElementById("header-value").value + ' ' + document.getElementById("access-token").value;
    var ajax = new XMLHttpRequest();
    ajax.open('GET', 'http://localhost:8090/backend/difference?'+'urlOne=' + url1 + '&urlTwo    =' +url2 +'&checkPath=' + path + '&name=' +  name + '&token=' + token, false);
    ajax.send();
    document.getElementById("results").value = ajax.responseText;
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
    alert("Testing Spotify");
    $.ajax({
        //  url: 'https://api.spotify.com/v1/me',
        url: 'https://api.spotify.com/v1/users/2252g3s5kq433fdxtldf2a5bi/playlists',
        headers: {
            'Authorization': 'Bearer ' + access_token,
            'application/json' :   '{name:A New Playlist, public:false}'



        },
        success: function (response) {
            console.log(response);
        }
    });
    alert("Here");
}



function changeBoxes(){
    var numArguments = document.getElementById("argumentNumber").value;
    if(numArguments==1){
        var x = document.getElementById("secondArgumentsDiv");
        x.style.display = "none";
        x = document.getElementById("thirdArgumentsDiv");
        x.style.display = "none";
        x = document.getElementById("fourthArgumentsDiv");
        x.style.display = "none";

    }else if (numArguments==2){
        var x = document.getElementById("secondArgumentsDiv");
        x.style.display = "block";
        x = document.getElementById("thirdArgumentsDiv");
        x.style.display = "none";
        x = document.getElementById("fourthArgumentsDiv");
        x.style.display = "none";
    }else if (numArguments==3){
        var x = document.getElementById("secondArgumentsDiv");
        x.style.display = "block";
        x = document.getElementById("thirdArgumentsDiv");
        x.style.display = "block";
        x = document.getElementById("fourthArgumentsDiv");
        x.style.display = "none";
    }else if (numArguments==4){
        var x = document.getElementById("secondArgumentsDiv");
        x.style.display = "block";
        x = document.getElementById("thirdArgumentsDiv");
        x.style.display = "block";
        x = document.getElementById("fourthArgumentsDiv");
        x.style.display = "block";
    }
}

function populateButton(){
    var apiType = document.getElementById("api-info").value;
    if(apiType == 'spotify'){
        document.getElementById("header-name").value = 'Authorization';
        document.getElementById("header-value").value = 'Bearer';
        var hashValue = location.hash.split('&');
        var accessToken = hashValue[0].substr(14);
        document.getElementById("access-token").value = accessToken;
    }
}
