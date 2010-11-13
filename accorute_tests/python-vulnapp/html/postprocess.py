import const

#routines to postprocess a template page before it returns to user
def fill_page(template_path, notice_message, error_message, field_values):
	f = open(template_path)
	try:
		contents = f.read()
	finally:
    		f.close()

	contents = const.html_template.replace("%contents%", contents)
	contents = contents.replace(const.notice_pattern, const.notice_message % notice_message)
	contents = contents.replace(const.error_pattern, const.error_message % error_message)

	for key in field_values.keys():
		contents = contents.replace("%%%s%%" % key, field_values[key])
	
	return contents



