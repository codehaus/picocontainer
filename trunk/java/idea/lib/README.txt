Put your openapi.jar here.

Should be put on DC's checkout manually:

pscp openapi.jar beaver.codehaus.org:/home/services/dcontrol/build/checkout/picoextras/picoidea/lib

Then login to beaver and do:
chmod o-r openapi.jar