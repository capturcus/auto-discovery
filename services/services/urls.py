from django.conf.urls import include, url
from django.contrib import admin

urlpatterns = [
    # Examples:
    url(r'^volume/', include('volume.urls')),
    url(r'^admin/', include(admin.site.urls)),
]
