var http = new XMLHttpRequest();
var url = '%(url)s';
var data = '%(data)s';
http.open("POST", url, true);

http.setRequestHeader("Content-Type", "application/json");
http.setRequestHeader("VGS-Log-Request", "all");

http.onreadystatechange = function () {
    if (http.readyState == 4 && http.status == 200) {
        var data = JSON.parse(http.responseText).data;
        document.write('<div name="data">' + JSON.stringify(data) + "</div>");
    }
    else {
        document.write("<div>ERROR</div>");
    }
};

http.send(data);