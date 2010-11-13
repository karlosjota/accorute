import sys, settings, preprocess, postprocess, utils, const
import MySQLdb, re, md5, os
from Cookie import SimpleCookie


expected_args = []
template_path = utils.cut_file_extension(sys.modules[__name__].__file__) + ".tpl"

def handler(req, args):

        notice_message = ""
        if args.has_key("notice_msg"):
                notice_message = args["notice_msg"]

        con = MySQLdb.connect(  host = settings.database_settings["host"],
                                user = settings.database_settings["login"],
                                passwd = settings.database_settings["password"],
                                db = settings.database_settings["database"])

        cur = con.cursor()
        try:
		cur.execute("SELECT browser, counter FROM stat_browser")
		row = cur.fetchone()
 
                contents = "<center><h2>Statistics of User Agents used by registered users</h2>" + os.linesep
                contents = contents + "<table border=\"1\" cellspacing=\"3\" cellpadding=\"3\" width=\"50%\" border=\"1\">" + os.linesep
		contents = contents + "<tr>" + os.linesep
		contents = contents + "<td width=\"75%\">User Agent</td>" + os.linesep
		contents = contents + "<td width=\"25%\">Logons</td>" + os.linesep
		contents = contents + "</tr>" + os.linesep

		while row:
			contents = contents + "<tr>" + os.linesep
			contents = contents + "<td>%s</td>" % row[0] + os.linesep
			contents = contents + "<td>%d</td>" % row[1] + os.linesep
			contents = contents + "</tr>" + os.linesep

			row = cur.fetchone()
		
		contents = contents + "</table></center>" + os.linesep

        finally:
                con.commit()
                cur.close()
                con.close()

        contents = const.html_template.replace("%contents%", contents)
        contents = contents.replace(const.notice_pattern, const.notice_message % notice_message)
        contents = contents.replace(const.error_pattern, const.error_message % "")
        return contents

