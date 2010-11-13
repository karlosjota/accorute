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

        notice_message = ""
        if args.has_key("notice_msg"):
                notice_message = args["notice_msg"]

        error_message = ""
        if args.has_key("error_msg"):
                error_message = args["error_msg"]

        filter = ""
        if args.has_key("q"):
                filter = args["q"]
	

        con = MySQLdb.connect(  host = settings.database_settings["host"],
                                user = settings.database_settings["login"],
                                passwd = settings.database_settings["password"],
                                db = settings.database_settings["database"])

        cur = con.cursor()
        try:
                expired, user_id = utils.is_expired(cur, session_id)
                if expired:
                        return {"Location":"login.html", "error_msg":"You session has expired. Please log in"}

		#construct user home
		user_home = utils.get_user_home(user_id)

		file_list = os.popen("ls -l %s" % os.path.join(user_home, "*%s*" % filter)).read().split(os.linesep)

		title = "<h2>List of Your files</h2>"
		if len(filter) > 0:
			title = "<h2>Search results for: %s</h2>" % filter

		contents = '''<table width="100%%" cellspacing="3" cellpadding="3">
					<tr>
					<td align="left" width = "50%%">%s</td>
					<td align="right" width="50%%">
					<form action="files.html" method="GET">
					Search files: <input type="text" name="q" value="%s">
					<input type="submit" value="Search">
					</form>
					</td>
					</tr>
				</table>
				''' % (title, filter) + os.linesep
		
		contents = contents + "<table width=\"100%\" border=\"1\" cellspacing=\"3\" cellpadding=\"3\">" + os.linesep
		contents = contents + "<form action=\"deletefiles.html\" method=\"POST\" >" + os.linesep
		contents = contents + "<tr>"  + os.linesep
		contents = contents + "<td align=\"center\" colspan=\"2\">Name</td>"  + os.linesep
		contents = contents + "<td align=\"center\" width=\"200\" >Size (bytes)</td>"  + os.linesep
		contents = contents + "<td align=\"center\" width=\"200\">Uploaded</td>"  + os.linesep
		contents = contents + "</tr>"  + os.linesep
		
		n_files = 0
		for file_entry in file_list:
			if file_entry.strip() == "":
				break

			file_attrs = file_entry.split(" ")
			i = 0
			while i < len(file_attrs):
				if len(file_attrs[i].strip()) == 0:
					del file_attrs[i]
				else:
					i = i + 1
			
			try:
				file_perm = file_attrs[0]
				file_id = file_attrs[1]
				file_owner = file_attrs[2]
				file_grp = file_attrs[3]
				file_size = file_attrs[4]
				file_date = file_attrs[5]
				file_time = file_attrs[6]
				file_name = os.path.basename(file_attrs[7])
			except KeyError:
				break

			contents = contents + "<tr>" + os.linesep
			contents = contents + "<td width=\"30\">" + os.linesep
			contents = contents + "<input type=\"checkbox\" name=\"%s\">" % (file_name, ) + os.linesep
			contents = contents + "</td>" + os.linesep
			
			contents = contents + "<td>" + os.linesep
			file_relative = utils.relpath(os.path.dirname(template_path), os.path.join(user_home, file_name))
			contents = contents + "<a target=\"_blank\" href=\"showfile.html?file=%s\">%s</a>" % (file_name, file_name) + os.linesep
			contents = contents + "</td>" + os.linesep
			
			contents = contents + "<td align=\"center\">" + os.linesep
			contents = contents + file_size + os.linesep
			contents = contents + "</td>" + os.linesep
			
			contents = contents + "<td align=\"center\">" + os.linesep
			contents = contents + file_date + " " + file_time + os.linesep
			contents = contents + "</td>" + os.linesep
			contents = contents + "</tr>" + os.linesep

			n_files = n_files + 1

		contents = contents + "<tr>" + os.linesep
		contents = contents + "<td colspan=\"4\" align=\"center\">" + os.linesep
		if n_files > 0:
			contents = contents + "<input type=\"submit\" value=\"Delete files\">" + os.linesep
		contents = contents + "</td>" + os.linesep
		contents = contents + "</tr>" + os.linesep
		contents = contents + "</form>" + os.linesep
		contents = contents + "</table>" + os.linesep

		contents = contents + "<p align=\"right\">" + os.linesep
		contents = contents + "<b><i>Add more files:</i></b><br>" + os.linesep
		contents = contents + "<form action=\"addfiles.html\" method=\"POST\" ENCTYPE=\"multipart/form-data\">" + os.linesep
		contents = contents + "<input type=\"file\" name=\"upload\">" + os.linesep
		contents = contents + "<input type=\"submit\"  value=\"Upload file\">" + os.linesep
		contents = contents + "</form>" + os.linesep
		contents = contents + "</p>" + os.linesep
        finally:
                con.commit()
                cur.close()
                con.close()

        contents = const.html_template.replace("%contents%", contents)
        contents = contents.replace(const.notice_pattern, const.notice_message % notice_message)
        contents = contents.replace(const.error_pattern, const.error_message % error_message)
        return contents

