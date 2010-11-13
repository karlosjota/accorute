import sys, settings, preprocess, postprocess, utils
import MySQLdb, re, md5, guid, logging
from Cookie import SimpleCookie


expected_args = ["login", "passwd"]
template_path = utils.cut_file_extension(sys.modules[__name__].__file__) + ".tpl"

def handler(req, args):
	if args.has_key("error_msg"):
		error_msg = args["error_msg"]
	else:
		error_msg = ""
		

        if not preprocess.input_matches(req, args, expected_args):
	        return postprocess.fill_page(template_path, "", error_msg, utils.build_dict(expected_args, []))
	elif args.has_key("error_msg"):
		return postprocess.fill_page(template_path, "", error_msg, args)
		
	
        login = args["login"].strip()
        passwd = args["passwd"]

        if login == "" or passwd == "":
                return postprocess.fill_page(template_path, "", "Username or password not specified", args)


        con = MySQLdb.connect(  host = settings.database_settings["host"],
                                user = settings.database_settings["login"],
                                passwd = settings.database_settings["password"],
                                db = settings.database_settings["database"])

        cur = con.cursor()
        try:
                cur.execute("""	SELECT logins.id, login, passwd, name, surname 
				FROM logins JOIN emails
				ON logins.emails_ref = emails.id 
				WHERE login=%s and passwd=%s""", (login, md5.new(passwd).digest()) )
                result = cur.fetchone()
                if result is None:
                        return postprocess.fill_page(template_path, "", "Bad username or password", args)

		name = result[3]
		surname = result[4]

		#generate session id
		session_id = guid.generate()
		expire_time = utils.get_session_expire_time()
		cur.execute("""DELETE FROM sessions WHERE expire_time < now()""")
		cur.execute("""INSERT INTO sessions (session_id, expire_time, user_id) VALUES (%s, %s, %s) """, (session_id, expire_time, result[0]))
		
		#set cookie
		req.headers_out.add( "Set-Cookie", utils.forge_cookie("session", session_id, "/"))

		#process statistics
		UserAgent = ""
		if req.headers_in.has_key("User-Agent"):
			UserAgent = req.headers_in["User-Agent"]
		if UserAgent.find("/") > 0:
			UserAgent = UserAgent[0:UserAgent.find("/")]

		if len(UserAgent) > 0:
			cur.execute("SELECT id FROM stat_browser WHERE browser = '%s'" % (UserAgent,))
			result = cur.fetchone()
	                if result is None:
				cur.execute("INSERT INTO stat_browser (browser, counter) VALUES (%s, %s)", (UserAgent, 1))
			else:
				cur.execute("UPDATE stat_browser SET counter = counter + 1 WHERE id = %s", (result[0], ))
        finally:
		con.commit()
                cur.close()
                con.close()

	logging.info('Login of %s from %s' % (login, req.connection.remote_ip))
	return {"Location":"news.html", "notice_msg":"Hello, %s %s!" % (name, surname)}


