import sys, settings, preprocess, postprocess, utils, const, xss
import MySQLdb, re, md5, os, logging
from Cookie import SimpleCookie


expected_args = ["id", "title", "body"]
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

		if not args.has_key("id"):
			return {"Location":"editnews.html"}
		
		id = args["id"].strip()
		try:
			id = int(id)
		except ValueError:
			return {"Location":"editnews.html"}			

                if not preprocess.input_matches(req, args, expected_args):
                        cur.execute("""SELECT title, text FROM news WHERE id=%s """, id)
			row = cur.fetchone()
			if row is None:
				return {"Location":"editnews.html"}
			title = row[0]
			text = row[1]
			return postprocess.fill_page(template_path, "", "", utils.build_dict(expected_args, [id, title, text]))

                title = args["title"].strip()
                text = args["body"]

                xss_strip = xss.XssCleaner()
                title = xss_strip.strip(title)
                text = xss_strip.strip(text)

                cur.execute("""UPDATE news SET title=%s, text=%s WHERE id=%s""", (title, text, id))
	finally:
                con.commit()
                cur.close()
                con.close()

        return {"Location":"editnews.html", "notice_msg":"Post saved successfully"}

