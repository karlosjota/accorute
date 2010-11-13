import sys, settings, preprocess, postprocess, utils, const
import MySQLdb, re, md5, logging

expected_args = []
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
	        
		mode = ""
		if args.has_key("mode"):
			mode = args["mode"].strip()

		if mode.lower() == "admin":
			cur.execute("""	SELECT logins.id, login, email, name, surname 
					FROM logins JOIN emails
					ON logins.emails_ref = emails.id """)
		else:
			cur.execute("""	SELECT logins.id, login, email, name, surname 
					FROM logins JOIN emails
					ON logins.emails_ref = emails.id 
					WHERE logins.id = %s""", (user_id))

		contents = '''
			<table border="0" width="50%" cellspacing="3" cellpadding="3"> '''

		row = cur.fetchone()
		while row:
			hidden = ""
			if row[0] != user_id:
				hidden = '''<input type="hidden" name="uid" value="%s">''' % row[0]

			contents = contents + '''
			<tr>
			<td width="50%%" align="right"><b>Login:</b></td>
			<td width="50%%" align="left">%s</td>
			</tr>
			<tr>
			<td align="right"><b>Name:</b></td>
			<td align="left">%s</td>
			</tr>
			<tr>
			<td align="right"><b>Surname:</b></td>
			<td align="left">%s</td>
			</tr>
			<tr>
			<td align="right"><b>Email:</b></td>
			<td align="left">%s</td>
			</tr>
			<tr>
			<td colspan="2" align="center">
			<form action="profile.html" method="GET">
			%s<input type="submit" value="Edit">
			<hr>
			<br>
			</form>
			</td>
			</tr>
			''' % (row[1], row[3], row[4], row[2], hidden)

			row = cur.fetchone()		

		contents = contents + '''
			</table>
			'''

        finally:
                con.commit()
                cur.close()
                con.close()


        contents = const.html_template.replace("%contents%", contents)
        contents = contents.replace(const.notice_pattern, const.notice_message % notice_message)
        contents = contents.replace(const.error_pattern, const.error_message % error_message)
        return contents

