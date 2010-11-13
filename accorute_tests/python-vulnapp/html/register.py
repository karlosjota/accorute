import sys, settings, preprocess, postprocess, utils, validator, cgi
import MySQLdb, re, md5

expected_args = ["login", "passwd", "passwd_confirm", "email", "name", "surname"]
template_path = utils.cut_file_extension(sys.modules[__name__].__file__) + ".tpl"

def handler(req, args):

	if not preprocess.input_matches(req, args, expected_args):
		return postprocess.fill_page(template_path, "", "", utils.build_dict(expected_args, []))
	
	login = args["login"].strip()
	name = cgi.escape(args["name"].strip())
	surname = cgi.escape(args["surname"].strip())
	email = args["email"].strip()
	passwd = args["passwd"]

	if login == "":
		return postprocess.fill_page(template_path, "", "'login' is required field and cannot be empty", args)

	if validator.invalid_login_re.search(login):
		return postprocess.fill_page(template_path, "", "Only characters, numbers and underscore are allowed in login", args)

	if passwd == "":
		return postprocess.fill_page(template_path, "", "'password' is required field and cannot be empty", args)
	
	if passwd != args["passwd_confirm"]:
		return postprocess.fill_page(template_path, "", "Entered passwords do not match", args)

	if validator.invalid_passwd_re.search(passwd):
		return postprocess.fill_page(template_path, "", "Whitespaces are not allowed in passwords", args)

	if email == "":
		return postprocess.fill_page(template_path, "", "'email' is required field and cannot be empty", args)
	
	if not validator.valid_email_re.match(email):
		return postprocess.fill_page(template_path, "", "You have entered email address in bad format", args)


	con = MySQLdb.connect(	host = settings.database_settings["host"], 
				user = settings.database_settings["login"],
				passwd = settings.database_settings["password"],
				db = settings.database_settings["database"])

	cur = con.cursor()
	try:
		#check if this login was not used
		cur.execute("SELECT login FROM logins WHERE login=%s", (login, ) )
		result = cur.fetchone()
		if result:
			return postprocess.fill_page(template_path, "", "The specified login is already used by someone", args)

		#check if this email was already inserted
		cur.execute("""SELECT id, email FROM emails WHERE email='%s'""" % (email, ) )
		result = cur.fetchone()

		if result is None:
			cur.execute("""INSERT INTO emails (email, name, surname) VALUES (%s, %s, %s)""", (email, name, surname))
			cur.execute("""SELECT LAST_INSERT_ID() """)
			result = cur.fetchone()

		cur.execute("""INSERT INTO logins (login, passwd, emails_ref) VALUES (%s, %s, %s)""", (login, md5.new(passwd).digest(), int(result[0])))
	finally:
		con.commit()
		cur.close()
		con.close()

	return {"Location":"login.html", "notice_msg":"Registration successful!"}


