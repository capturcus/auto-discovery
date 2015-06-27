from django.conf.urls import url
from volume.views import VolumeView

urlpatterns = [
    url(r'^$', VolumeView.as_view(), name="volume"),
]