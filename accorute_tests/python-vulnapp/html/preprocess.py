
#passed_args is hash map with key - hhtp parameter names
#waited args is list with names of expected arguments
def input_matches(req, passed_args, expected_args):
	for expected_arg in expected_args:
		if not passed_args.has_key(expected_arg):
			return False

	return True
	

