# Transifex configuration

This is a required file to work with transfix:

```
[main]
host = https://www.transifex.com
# just download files if 80% is complete
minimum_perc = 80
# transform file default format to requiered format
lang_map = pt_BR: pt-rBR, ru_RU: ru-rRU, fr_FR: fr-rFR, es_MX: es-rMX, es_ES: es-rES, en_GB: en-rGB, ko_KR: ko-rKR
```

````
[flyve-mdm-android-inventory-agent.stringsxml-android]
# where be placed all files when come from service
 file_filter = app/src/main/res/values-<lang>/strings.xml
# the place of the main file
source_file = app/src/main/res/values/strings.xml
# main language
source_lang = en
# file type
type = ANDROID
```