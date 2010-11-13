import sys, settings, preprocess, postprocess, utils, const, validator
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

                if not args.has_key("upload"):
                        return {"Location":"files.html", "error_msg":"File to upload was not specified"}

		upload = args["upload"]
		if type(upload) is str:
			return {"Location":"files.html", "error_msg":"File to upload was not specified"}

                user_home = utils.get_user_home(user_id)
                os.chdir(user_home)

		fileName = upload.filename.replace('\\','/').split('/')[-1]

		#check filename
		if validator.invalid_filename_re.search(fileName):
			return {"Location":"files.html", "error_msg":"Only number, latin characters and underscores are allowed in filenames"}

		if not fileName.lower().endswith(".jpg"):
			return {"Location":"files.html", "error_msg":"Only JPEG files can be stored on this server"}

		filePath = os.path.join(user_home,  fileName)
    		fileHandle = open(filePath, 'wb')
    		size = 0

    		while True:
		        data = upload.read(8192)
		        if not data:
				break
			fileHandle.write(data)
			size += len(data)
		
		fileHandle.close()
		import stat
		os.chmod(filePath, stat.S_IREAD | stat.S_IWRITE | stat.S_IRGRP | stat.S_IWGRP)
        finally:
                con.commit()
                cur.close()
                con.close()

	return {"Location":"files.html", "notice_msg":"File uploaded successfully"}




