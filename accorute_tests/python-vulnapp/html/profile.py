import sys, settings, preprocess, postprocess, utils, validator, cgi
import MySQLdb, re, md5, logging

expected_args = ["passwd", "passwd_confirm", "name", "surname"]
template_path = utils.cut_file_extension(sys.modules[__name__].__file__) + ".tpl"

def handler(req, args):
        session_id = utils.get_cookie(req, "session").strip()

        if session_id == "":
                logging.warning('Unathorized attempt to access %s from %s' % (req.the_request, req.connection.remote_ip))
                return {"Location":"login.html", "error_msg":"Authorization required!"}

        notice_message = ""
        if args.has_key("notice_msg"):
                notice_message = args["notice_msg"]

        error_message = ""
        if args.has_key("error_msg"):
                error_message = args["error_msg"]

        con = MySQLdb.connect(  host = settings.database_settings["host"],
                                user = settings.database_settings["login"],
                                passwd = settings.database_settings["password"],
                                db = settings.database_settings["database"])

        cur = con.cursor()
        try:
                expired, user_id = utils.is_expired(cur, session_id)
                if expired:
                        return {"Location":"login.html", "error_msg":"You session has expired. Please log in"}

		if args.has_key("uid"):
			try:
				user_id = int(args["uid"])
			except:
				logging.warning("Request to edit profile %s specifiied wrong user id. Client %s" % (req.the_request, req.connection.remote_ip))


		cur.execute(""" SELECT logins.id, login, email, name, surname
                                FROM logins JOIN emails
                                ON logins.emails_ref = emails.id
                                WHERE logins.id = %s""", (user_id))

                row = cur.fetchone()
                if row is None:
                	return {"Location":"viewprofile.html", "error_msg":"Profile to edit specified wrongly"}


		if not preprocess.input_matches(req, args, expected_args):
			return postprocess.fill_page(template_path, notice_message, error_message, {"login":row[1], "passwd":"", "passwd_confirm":"", "email":row[2], "name":row[3], "surname":row[4]})
	
		name = cgi.escape(args["name"].strip())
		surname = cgi.escape(args["surname"].strip())
		passwd = args["passwd"]
		email = row[2]

		if passwd != args["passwd_confirm"]:
			args["login"] = row[1]
			args["email"] = row[2]
			return postprocess.fill_page(template_path, "", "Entered passwords do not match", args )

		if validator.invalid_passwd_re.search(passwd):
			args["login"] = row[1]
			args["email"] = row[2]
			return postprocess.fill_page(template_path, "", "Whitespaces are not allowed in passwords", args)

		
		cur.execute(""" UPDATE emails SET name=%s, surname=%s WHERE email=%s""", (name, surname, email))
		if passwd != "":
			cur.execute(""" UPDATE logins SET passwd=%s WHERE id=%s""", (md5.new(passwd).digest(), user_id))

	finally:
		con.commit()
		cur.close()
		con.close()

	logging.warning("Request to edit profile %s succeded. Client %s" % (req.the_request, req.connection.remote_ip))
	return {"Location":"viewprofile.html", "notice_msg":"Profile saved successful!"}


