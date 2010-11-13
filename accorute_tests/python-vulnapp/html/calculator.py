import sys, settings, preprocess, postprocess, utils, const
import MySQLdb, re, md5, os
from Cookie import SimpleCookie
from math import *


expected_args = ["expresion"]
template_path = utils.cut_file_extension(sys.modules[__name__].__file__) + ".tpl"

def handler(req, args):

       	if not preprocess.input_matches(req, args, expected_args):
               	return postprocess.fill_page(template_path, "", "", utils.build_dict(expected_args, []))

	expresion = args["expresion"].strip()
	if expresion == "":
		return postprocess.fill_page(template_path, "", "The expresion is empty", args)
		
	try:
		contents = str(eval(expresion))
	except ZeroDivisionError:
		return postprocess.fill_page(template_path, "", "Division by Zero", args)
	except ValueError, OverflowError:
		return postprocess.fill_page(template_path, "", "Some function in expression does not support specified domain", args)
	except:
		return postprocess.fill_page(template_path, "", "Syntax error in expression", args)

 	return postprocess.fill_page(template_path, "The result of an expression is " + contents, "", args)

