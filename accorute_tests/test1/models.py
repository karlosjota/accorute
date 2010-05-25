from django.db import models
from django.contrib.auth.models import User
# Create your models here.

class warning(models.Model):
    fromUser = models.ForeignKey(User, null=False, related_name='issued_warnings')
    toUser = models.ForeignKey(User,null=False, related_name='received_warnings')
    message = models.TextField()
