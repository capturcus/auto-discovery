from app.views import VolumeView, SlidesView, PrintView, AdminView
from django.conf.urls import url

urlpatterns = [
    url(r'^slides/?$', SlidesView.as_view(), name="slides"),
    url(r'^volume/?$', VolumeView.as_view(), name="volume"),
    url(r'^print/?$', PrintView.as_view(), name="print"),
    url(r'^admin/?$', AdminView.as_view(), name="admin"),
]