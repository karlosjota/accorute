# Create your views here.
from django.http import HttpResponse, Http404, HttpResponseRedirect
from django.contrib.auth.decorators import login_required
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.contrib.auth.models import User
from django.contrib.auth.models import Group
from django_accorute_tests.test1.models import warning as warnin

@login_required
def index(request):
    actions = []
    actions.append({
            "link" : "/test1/warnings/%s/" % (request.user.username,),
            "name" : "List your warnings"
            })
    if  request.user.groups.filter(name="moderators") :
        actions.append({
                "link" : "/test1/make_warning/", 
                "name" : "Make a warning"
                })
    return render_to_response("base.html",{"actions":actions},context_instance=RequestContext(request))

@login_required
def warnings(request, username):
    actions = []
    actions.append({
            "link" : "/test1/warnings/%s/" % (request.user.username,),
            "name" : "List your warnings"
            })
    if  request.user.groups.filter(name="moderators") :
        actions.append({
                "link" : "/test1/make_warning/", 
                "name" : "Make a warning"
                })
    if request.user.username == username:
        if User.objects.all().filter(username=username):
            wrns = warnin.objects.all().filter(toUser = User.objects.all().get(username=username))
            return render_to_response("warnings.html",{"actions":actions,"warns":wrns},context_instance=RequestContext(request))
        else:
            raise Http404
    else:
        raise Http404

def warning(request, warnID):
    actions = []
    actions.append({
            "link" : "/test1/warnings/%s/" % (request.user.username,),
            "name" : "List your warnings"
            })
    if  request.user.groups.filter(name="moderators") :
        actions.append({
                "link" : "/test1/make_warning/", 
                "name" : "Make a warning"
                })
    if warnin.objects.all().filter(id=warnID):
        warn = warnin.objects.all().get(id=warnID)
        return render_to_response("warning.html",{"actions":actions,"warn":warn},context_instance=RequestContext(request))
    else:
        raise Http404
        

@login_required
def make_warning(request):
    actions = []
    actions.append({
            "link" : "/test1/warnings/%s/" % (request.user.username,),
            "name" : "List your warnings"
            })
    if  request.user.groups.filter(name="moderators") :
        actions.append({
                "link" : "/test1/make_warning", 
                "name" : "Make a warning"
                })
    if request.method == 'GET':    
        return render_to_response("make_msg.html",{"actions":actions, "users" : User.objects.all()},context_instance=RequestContext(request))
    elif request.method == 'POST':
        user = User.objects.all().get(username = request.POST["to"])
        message = request.POST["message"]
        fromUser = User.objects.all().get(username = request.POST["from"])
        if not user or  not fromUser:
            raise Http404
        warn = warnin.objects.create(fromUser = fromUser, toUser=user, message = message)
        warn.save()
        return  HttpResponseRedirect("/test1/")

