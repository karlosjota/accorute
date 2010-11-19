from django.conf.urls.defaults import *
urlpatterns = patterns( 'django_accorute_tests.test1.views',    
    (r'^warning/(\d*)/$', 'warning'),
    (r'^warnings/(\w*)/$', 'warnings'),
    (r'^make_warning/$', 'make_warning'),
    (r'^$', 'index'),
)
