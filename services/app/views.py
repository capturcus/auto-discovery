# coding=utf-8
import subprocess

import os
import gtk
from app.forms import DocumentForm
from django.core.urlresolvers import reverse_lazy
from django.http.response import HttpResponse, JsonResponse
from django.shortcuts import render
from django.views.generic import FormView
from django.views.generic.base import View
from services import settings


class SlidesView(View):
    def get(self, request):
        print "Slides get"
        return render(request, 'slides.html')

    def post(self, request):
        print "Slides post"
        data = dict(self.request.POST.iteritems())
        action = data.pop("action")
        print action
        if action == "left":
            print "left"
            subprocess.call(["xdotool", "key", "Left"])
        elif action == "right":
            print "right"
            subprocess.call(["xdotool", "key", "Right"])
        print "Slides OK"
        return HttpResponse("OK")


class VolumeView(View):
    def get(self, request):
        print "Volume get"
        return render(request, 'volume.html', {"volume": self.get_volume()})

    def post(self, request):
        print "Volume POST"
        data = dict(self.request.POST.iteritems())
        volume = int(data.pop("volume", 0))
        assert (0 <= volume <= 100)
        print "set " + str(volume)
        self.set_volume(volume)
        print "set OK"
        return JsonResponse({"volume": self.get_volume()})

    def get_volume(self):
        task = subprocess.Popen("echo `(pactl list sinks | grep \"Poziom głośności: 0\")| awk '{print $4}'`",
                                shell=True, stdout=subprocess.PIPE)
        data = task.stdout.read()
        assert data.endswith("%\n")
        assert task.wait() == 0
        print "cur volume: " + data[:-2]
        return int(data[:-2])

    def set_volume(self, volume):
        subprocess.call(["pactl", "set-sink-volume", "0", str(volume) + "%"])


class PrintView(FormView):
    form_class = DocumentForm
    template_name = 'print.html'
    success_url = reverse_lazy('print')

    def post(self, request, *args, **kwargs):
        form = self.get_form()
        if form.is_valid():
            return self.form_valid(form)
        else:
            subprocess.call(["lp", "/home/leonid/Dokumenty/auto-discovery/services/media/documents/"
                                   "551507_422554027916522_7430959372314432163_n.png"])
            print "printed"
            return self.get(request, *args, **kwargs)

    def form_valid(self, form):
        print "form valid"
        file = form.instance.file
        file.save(os.path.join(settings.MEDIA_ROOT, file.name), file)
        print "file saved"
        print os.path.join(settings.MEDIA_ROOT, file.name)
        subprocess.call(["lp", os.path.join(settings.MEDIA_ROOT, file.name)])
        print "printed"
        return super(FormView, self).form_valid(form)


class AgarioView(View):
    def get(self, request):
        print "agario GET"
        return render(request, 'agario.html')

    def post(self, request):
        print "agario POST"
        width = gtk.gdk.screen_width()
        height = gtk.gdk.screen_height()
        data = dict(self.request.POST.iteritems())
        x = float(data.pop("x"))
        y = float(data.pop("y"))
        print x, y
        print x * width, y * height
        subprocess.call(["xdotool", "mousemove", str(int(x * width)),
                         str(int(y * height))])
        print "mousemoved"
        return HttpResponse("OK")


class AdminView(FormView):
    def get(self, request):
        return render(request, 'admin.html')
