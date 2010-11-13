import re


valid_email_re = re.compile(r"^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$")
invalid_login_re = re.compile("[^a-zA-Z0-9_]")
invalid_filename_re = re.compile("[^a-zA-Z0-9_.]")
invalid_passwd_re = re.compile("\s")



