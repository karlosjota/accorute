import sys, settings, preprocess, postprocess, utils, const
import MySQLdb, re, md5, os, logging
from Cookie import SimpleCookie


expected_args = ["title", "body"]
template_path = utils.cut_file_extension(sys.modules[__name__].__file__) + ".tpl"

def handler(req, args):
	
	req.headers_out.add( "Set-Cookie", utils.delete_cookie("session"))

        session_id = utils.get_cookie(req, "session").strip()
	if session_id == "":
		return {"Location":"login.html"}

        con = MySQLdb.connect(  host = settings.database_settings["host"],
                                user = settings.database_settings["login"],
                                passwd = settings.database_settings["password"],
                                db = settings.database_settings["database"])

        cur = con.cursor()
        try:
                expired, user_id = utils.is_expired(cur, session_id)
		cur.execute("SELECT login FROM logins WHERE id = %s", (user_id, ))
		result = cur.fetchone()
		
		login = ""
		if result:
			login = result[0]
        finally:
                con.commit()
                cur.close()
                con.close()
	

	logging.info('Logout of %s from %s' % (login, req.connection.remote_ip))
	return {"Location":"login.html"}
