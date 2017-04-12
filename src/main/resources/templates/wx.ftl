<!DOCTYPE html>
<html>
<script src="//cdn.jsdelivr.net/jquery/1.12.4/jquery.min.js"></script>
<script src="//cdn.jsdelivr.net/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="//cdn.jsdelivr.net/bootstrap/3.3.7/css/bootstrap-theme.min.css">
<link rel="stylesheet" href="//cdn.jsdelivr.net/bootstrap/3.3.7/css/bootstrap.min.css">
</head>
<body>

<img id="id_qrcode" src="">

<button onclick="addClient();">添加客户端</button>

<script type="text/javascript">

function addClient() {
	$.get("${app.contextPath}/wx/createClient", function(r) {
		if (r.success) {
			$("#id_qrcode").attr("src", "${app.contextPath}/wx/outputQrcode/" + r.result);
		}
	}, "json");
}


</script>
</body>
</html>