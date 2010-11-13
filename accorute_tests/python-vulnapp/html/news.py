import sys, settings, preprocess, postprocess, utils, const
import MySQLdb, re, md5, os
from Cookie import SimpleCookie


expected_args = ["skip"]
template_path = utils.cut_file_extension(sys.modules[__name__].__file__) + ".tpl"

def handler(req, args):

        notice_message = ""
        if args.has_key("notice_msg"):
                notice_message = args["notice_msg"]

        filter = ""
        if args.has_key("q"):
                filter = args["q"]

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

		if filter == "":
			cur.execute("""SELECT title, text, date, name, surname FROM news
				JOIN (logins, emails) ON (news.author = logins.id AND logins.emails_ref = emails.id )
				ORDER BY news.id DESC LIMIT %s, %s""" % (QueryOffset, NewsToQuery))
		else:
                        cur.execute("""SELECT title, text, date, name, surname FROM news
                                JOIN (logins, emails) ON (news.author = logins.id AND logins.emails_ref = emails.id )
                                WHERE MATCH (title, text) AGAINST (%s) 
				ORDER BY news.id DESC LIMIT %s, %s""", (filter, QueryOffset, NewsToQuery))

		row = cur.fetchone()
                
                title = "<h2>Current news</h2>"
                if len(filter) > 0:
                        title = "<h2>Search results for: %s</h2>" % filter
		contents = '''<table width="100%%" cellspacing="3" cellpadding="3">
                                        <tr>
                                        <td align="left" width = "50%%">%s</td>
                                        <td align="right" width="50%%">
                                        <form action="news.html" method="GET">
                                        Search in news: <input type="text" name="q" value="%s">
                                        <input type="submit" value="Search">
                                        </form>
                                        </td>
                                        </tr>
                                </table>
                                ''' % (title, filter) + os.linesep

		contents = contents + "<table border=\"0\" cellspacing=\"3\" cellpadding=\"3\" width=\"100%\">" + os.linesep
		i = 1
		while row and i < NewsToQuery:
			contents = contents + "<tr>" + os.linesep 
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
		contents = contents + "<td align=\"left\">" + os.linesep
		if skip > 0:
			contents = contents + "<a href=\"news.html?skip=%s\">Previous %s news...</a> <br/>" % (str(skip-1), str(settings.general_settings["max_news_per_page"])) + os.linesep
		
		contents = contents + "</td>" + os.linesep
		
		contents = contents + "<td align=\"right\">" + os.linesep
		if int(cur.rowcount) == NewsToQuery:
			contents = contents + "<a href=\"news.html?skip=%s\">Next %s news...</a> <br/>" % (str(skip+1), str(settings.general_settings["max_news_per_page"])) + os.linesep

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
		contents = contents + "<a href=\"editnews.html\"><h3>Edit news</h3></a>" 
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

	

