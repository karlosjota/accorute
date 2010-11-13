import os.path, settings

def get_user_home(user_id):
	user_home = os.path.join(settings.general_settings["users_home"], str(user_id))
	if not os.path.exists(user_home):
		os.mkdir(user_home)
	return user_home


#returns tuple: True if session is expired or False otherwise and user_id from logins table 
def is_expired(cur, session_id):
	cur.execute("""SELECT user_id FROM sessions WHERE session_id='%s' AND expire_time > now()""" % (session_id, )) 
	result = cur.fetchone()
	if result is None:
		return True, 0

	#update session id
	expire_time = get_session_expire_time()
	cur.execute("""UPDATE sessions SET expire_time=%s WHERE session_id=%s""", (expire_time, session_id))
	return False, result[0]

def get_session_expire_time():
	import datetime
	now = datetime.datetime.now()
	import settings
	expire = now + datetime.timedelta(0, settings.general_settings["session_timeout"]*60)
	return expire.strftime("%Y-%m-%d %H:%M:%S")


def pathsplit(p, rest=[]):
    (h,t) = os.path.split(p)
    if len(h) < 1: return [t]+rest
    if len(t) < 1: return [h]+rest
    return pathsplit(h,[t]+rest)

def commonpath(l1, l2, common=[]):
    if len(l1) < 1: return (common, l1, l2)
    if len(l2) < 1: return (common, l1, l2)
    if l1[0] != l2[0]: return (common, l1, l2)
    return commonpath(l1[1:], l2[1:], common+[l1[0]])

def relpath(p1, p2):
    (common,l1,l2) = commonpath(pathsplit(p1), pathsplit(p2))
    p = []
    if len(l1) > 0:
        p = [ '../' * len(l1) ]
    p = p + l2
    return os.path.join( *p )


#keys and values - are list
def build_dict(keys, values):
	ret = {}
	for i in range(len(keys)):
		if i < len(values):
			ret[keys[i]] = str(values[i])
		else:
			ret[keys[i]] = ""
	return ret

#cuts of any file extension
def cut_file_extension(filename):
	ret = os.path.splitext(filename)
	return ret[0]


import os, time, string

def delete_cookie(name):
	return "%s=abc; path=/; expires=Fri, 31-Dec-1900 00:00:00 GMT;" % name

def forge_cookie(name, value, path):
	import urllib
	value = urllib.quote(value)
	return "%s=%s; path=%s;" % (name, value, path)
 
def load_cookies(req):
	if not req.headers_in.has_key('Cookie'):
		return {}
	raw = req.headers_in['Cookie']
	words = map(string.strip, string.split(raw, ';'))
	cookies = {}
	for word in words:
		i = string.find(word, '=')
		if i >= 0:
			key, value = word[:i], word[i+1:]
		cookies[key] = value
	return cookies
 
def get_cookie(req, name):
	cookies = load_cookies(req)
	try:
		value = cookies[name]
	except KeyError:
		return ""
	import urllib
	return urllib.unquote(value)

