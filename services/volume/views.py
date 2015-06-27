# coding=utf-8
from subprocess import call
import subprocess

from django.http import JsonResponse
from django.shortcuts import render
from django.views.generic.base import View


class VolumeView(View):
    def get(self, request):
        return render(request, 'index.html', {"volume": self.get_volume()})

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
        call(["pactl", "set-sink-volume", "0", str(volume) + "%"])
