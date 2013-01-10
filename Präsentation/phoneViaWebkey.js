//Functions to show and rotate phone-screen via WebKey
var ipaddr;
var imgParams = "vnnnnn";
function phoneConnect() {
	ipaddr = document.getElementById("ipaddr").value;
	document.getElementById("ipaddr").disabled = true;
	document.getElementById("pconn").disabled = true;
	document.getElementById("pdisconn").disabled = false;
	document.getElementById("phone").onload = function() {
		window.setTimeout("phoneRefresh()", 10);
	}
	phoneRefresh();
}
function phoneRotate(orientation) {
	switch(orientation) {
		case 0:
			//imgParams = "vnnnnn";
			document.getElementById("protateleft").disabled = false;
			document.getElementById("protatemiddle").disabled = true;
			document.getElementById("protateright").disabled = false;
			document.getElementById("phonediv").setAttribute("class", "");
			break;
		case -1:
			//imgParams = "hnnnnn";
			document.getElementById("protateleft").disabled = true;
			document.getElementById("protatemiddle").disabled = false;
			document.getElementById("protateright").disabled = false;
			document.getElementById("phonediv").setAttribute("class", "phoneleftrotate");
			break;
		case 1:
			//imgParams = "hnnfnn";
			document.getElementById("protateleft").disabled = false;
			document.getElementById("protatemiddle").disabled = false;
			document.getElementById("protateright").disabled = true;
			document.getElementById("phonediv").setAttribute("class", "phonerightrotate");
			break;
		case 2:
			//imgParams = "vnnfnn";
			break;
	}
}
function phoneRefresh() {
	var link = "http://bla:blub@"+ipaddr+"/screenshot.jpg?"+imgParams+(new Date()).getTime();
	document.getElementById("phone").setAttribute("src", link);
}
function phoneDisconnect() {
	document.getElementById("phone").onload = null;
	document.getElementById("phone").setAttribute("src", "images/black.png");
	document.getElementById("ipaddr").disabled = false;
	document.getElementById("pdisconn").disabled = true;
	document.getElementById("pconn").disabled = false;
}