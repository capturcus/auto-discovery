from app.models import Document
from django.core.urlresolvers import reverse_lazy
from django.forms.models import ModelForm


class DocumentForm(ModelForm):
    class Meta:
        model = Document
        fields = ["file"]

    def get_absolute_url(self):
        return reverse_lazy('print')