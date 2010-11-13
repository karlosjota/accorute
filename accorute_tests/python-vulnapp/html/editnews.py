import sys, settings, preprocess, postprocess, utils, const
import MySQLdb, re, md5, os, logging
from Cookie import SimpleCookie


expected_args = ["skip"]
template_path = utils.cut_file_extension(sys.modules[__name__].__file__) + ".tpl"

def handler(req, args):
	session_id = utils.get_cookie(req, "session").strip()

	if session_id == "":
		logging.warning('Unathorized attempt to access %s from %s' % (req.the_request, req.connection.remote_ip))
		return {"Location":"login.html", "error_msg":"Authorization required!"}

	notice_message = ""
	if args.has_key("notice_msg"):
		notice_message = args["notice_msg"]
		
	try:
		skip = int(args["skip"])
	except:
		skip = 0

        NewsToQuery = settings.general_settings["max_news_per_page"] + 1;
	QueryOffset = (NewsToQuery-1)*skip;

	
	con = MySQLdb.connect(  host = settings.database_settings["host"],
                                user = settings.database_settings["login"],
                                passwd = settings.database_settings["password"],
                                db = settings.database_settings["database"])

        cur = con.cursor()
        try:
		expired, user_id = utils.is_expired(cur, session_id)
		if expired:
			return {"Location":"login.html", "error_msg":"You session has expired. Please log in"}

		cur.execute("""SELECT title, text, date, name, surname, news.id FROM news
				JOIN (logins, emails) ON (news.author = logins.id AND logins.emails_ref = emails.id )
				WHERE news.author=%s 
				ORDER BY news.id DESC LIMIT %s, %s""" % (user_id, QueryOffset, NewsToQuery))
		row = cur.fetchone()
		contents = "<h2>Edit news</h2>" + os.linesep
		contents = contents + "<table border=\"0\" cellspacing=\"3\" cellpadding=\"3\" width=\"100%\">" + os.linesep
		i = 1
		while row and i < NewsToQuery:
			contents = contents + "<tr>" + os.linesep 
			contents = contents + "<td width=\"10%\" rowspan=\"3\" valign=\"center\">"  + os.linesep
			contents = contents + "<a href=\"doeditnews.html?id=%s\">Edit</a>" % (row[5], ) + os.linesep
			contents = contents + "</td>" + os.linesep
			contents = contents + "<td colspan=\"2\" align=\"left\">" + os.linesep
			contents = contents + "<hr/>"
			contents = contents + """<b><i>%s</i></b>""" % row[0] + os.linesep
			contents = contents + "</td>" + os.linesep 
			contents = contents + "</tr>" + os.linesep 

			contents = contents + "<tr>" + os.linesep 
			contents = contents + "<td colspan=\"2\">" + os.linesep 
			contents = contents + row[1] + os.linesep
			contents = contents + "</td>" + os.linesep 
			contents = contents + "</tr>" + os.linesep 
			
			contents = contents + "<tr>" + os.linesep 
			contents = contents + "<td  colspan=\"2\" align=\"right\">" + os.linesep 
			contents = contents + """by <i>%s %s</i> at %s""" % (row[3], row[4], row[2].strftime('%Y-%m-%d %H:%M:%S')) + os.linesep
			contents = contents + "<hr/>"
			contents = contents + "</td>" + os.linesep 
			contents = contents + "</tr>" + os.linesep 
			
			row = cur.fetchone()
			i = i + 1

		contents = contents + "<tr>" + os.linesep
		contents = contents + "<td colspan=\"2\" align=\"left\">" + os.linesep
		if skip > 0:
			contents = contents + "<a href=\"editnews.html?skip=%s\">Previous %s news...</a> <br/>" % (str(skip-1), str(settings.general_settings["max_news_per_page"])) + os.linesep
		
		contents = contents + "</td>" + os.linesep
		
		contents = contents + "<td align=\"right\">" + os.linesep
		if int(cur.rowcount) == NewsToQuery:
			contents = contents + "<a href=\"editnews.html?skip=%s\">Next %s news...</a> <br/>" % (str(skip+1), str(settings.general_settings["max_news_per_page"])) + os.linesep

		contents = contents + "</td>" + os.linesep
		contents = contents + "</tr>" + os.linesep

		contents = contents + "<tr>" + os.linesep
		contents = contents + "<td width=\"10%\">"  + os.linesep
		contents = contents + "</td>" + os.linesep
		contents = contents + "<td width=\"45%\" align=\"right\">"  + os.linesep
		contents = contents + "</td>" + os.linesep
		contents = contents + "<td width=\"45%\">"  + os.linesep
		contents = contents + "</td>" + os.linesep
		contents = contents + "</tr>" + os.linesep

		contents = contents + "</table>" + os.linesep

		contents = contents + "<br/><hr/>" + os.linesep 
		contents = contents + "<table border=\"0\" cellspacing=\"3\" cellpadding=\"3\" width=\"100%\">" + os.linesep
		contents = contents + "<tr>" + os.linesep
		contents = contents + "<td align=\"left\">" + os.linesep
		contents = contents + "<a href=\"addnews.html\"><h3>Post news</h3></a>" 
		contents = contents + "</td>" + os.linesep

		contents = contents + "<td align=\"center\">" + os.linesep
		contents = contents + "<a href=\"news.html\"><h3>View news</h3></a>" 
		contents = contents + "</td>" + os.linesep
		
		contents = contents + "<td align=\"right\">" + os.linesep
		contents = contents + "<a href=\"deletenews.html\"><h3>Delete news</h3></a>" 
		contents = contents + "</td>" + os.linesep
		contents = contents + "</tr>" + os.linesep
		
        finally:
		con.commit()
                cur.close()
                con.close()

        contents = const.html_template.replace("%contents%", contents)
        contents = contents.replace(const.notice_pattern, const.notice_message % notice_message)
        contents = contents.replace(const.error_pattern, const.error_message % "")
        return contents

	

