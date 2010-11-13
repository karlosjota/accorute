def generate_error(request, message):
	out = 	"""<html>
		<head>
		<script>alert(\"%s\");</script>
		<META HTTP-EQUIV=\"Refresh\" CONTENT=\"0; URL=%s \">" 
		</head>
		<body>
		</body>
		</html>""" % (message, request.headers_in["Referer"])

	return out;







