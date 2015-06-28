# coding=utf-8
import os
import subprocess

from app.forms import DocumentForm
from django.core.urlresolvers import reverse_lazy

from django.http.response import HttpResponse, JsonResponse
from django.shortcuts import render
from django.views.generic import FormView
from django.views.generic.base import View
from services import settings


class SlidesView(View):
    def get(self, request):
        return render(request, 'slides.html')

    def post(self, request):
        data = dict(self.request.POST.iteritems())
        action = data.pop("action")
        if action == "left":
            print "left"
            subprocess.call(["xdotool", "key", "Left"])
        elif action == "right":
            print "right"
            subprocess.call(["xdotool", "key", "Right"])
        return HttpResponse("OK")


class VolumeView(View):
    def get(self, request):
        return render(request, 'volume.html', {"volume": self.get_volume()})

    def post(self, request):
        data = dict(self.request.POST.iteritems())
        volume = int(data.pop("volume", 0))
        assert (0 <= volume <= 100)
        self.set_volume(volume)
        return JsonResponse({"volume": self.get_volume()})

    def get_volume(self):
        task = subprocess.Popen("echo `(pactl list sinks | grep \"Poziom głośności: 0\")| awk '{print $4}'`",
                                shell=True, stdout=subprocess.PIPE)
        data = task.stdout.read()
        assert data.endswith("%\n")
        assert task.wait() == 0
        return int(data[:-2])

    def set_volume(self, volume):
        subprocess.call(["pactl", "set-sink-volume", "0", str(volume) + "%"])


class PrintView(FormView):
    form_class = DocumentForm
    template_name = 'print.html'
    success_url = reverse_lazy('print')

    def form_valid(self, form):
        file = form.instance.file
        file.save(os.path.join(settings.MEDIA_ROOT, file.name), file)
        print os.path.join(settings.MEDIA_ROOT, file.name)
        subprocess.call(["lp", os.path.join(settings.MEDIA_ROOT, file.name)])
        return super(FormView, self).form_valid(form)

class AdminView(FormView):
    def get(self, request):
        return render(request, 'admin.html')
