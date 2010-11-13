import sys, settings, preprocess, postprocess, utils, const
import MySQLdb, re, md5, os, logging
from Cookie import SimpleCookie


expected_args = []
template_path = utils.cut_file_extension(sys.modules[__name__].__file__) + ".tpl"

def handler(req, args):
        session_id = utils.get_cookie(req, "session").strip()

        if session_id == "":
		logging.warning('Unathorized attempt to access %s from %s' % (req.the_request, req.connection.remote_ip))
                return {"Location":"login.html", "error_msg":"Authorization required!"}

        con = MySQLdb.connect(  host = settings.database_settings["host"],
                                user = settings.database_settings["login"],
                                passwd = settings.database_settings["password"],
                                db = settings.database_settings["database"])

        cur = con.cursor()
        try:
                expired, user_id = utils.is_expired(cur, session_id)
                if expired:
                        return {"Location":"login.html", "error_msg":"You session has expired. Please log in"}
                
		if not args.has_key("file"):
			return {"Location":"files.html", "error_msg":"File to view was not specified"}
		req_file = args["file"]

                user_home = utils.get_user_home(user_id)
                os.chdir(user_home)

		handle = open(req_file)
		contents = handle.read()
                handle.close()
        finally:
                con.commit()
                cur.close()
                con.close()

	req.content_type = 'image/jpeg'
	return contents;

