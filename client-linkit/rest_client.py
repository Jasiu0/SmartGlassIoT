import urllib2
import json
import mraa
import threading
import sys
import time

moveSensor = mraa.Gpio(20)
moveSensor.dir(mraa.DIR_IN)
soundSensor = mraa.Gpio(21)
soundSensor.dir(mraa.DIR_IN)
fotoSensor = mraa.Gpio(43)
fotoSensor.dir(mraa.DIR_IN)
gasSensor = mraa.Gpio(17)
gasSensor.dir(mraa.DIR_IN)


def update():
    threading.Timer(3.0, update).start()
    moveVal = moveSensor.read()
    if (moveVal == 1):
        moveValue = True
    else:
        moveValue = False
    gasVal = gasSensor.read()
    if (gasVal == 0):
        gasValue = True
    else:
        gasValue = False
    fotoVal = fotoSensor.read()
    if (fotoVal == 1):
        fotoValue = True
    else:
        fotoValue = False
    soundVal = soundSensor.read()
    if (soundVal == 1):
        soundValue = True
    else:
        soundValue = False

    url = 'http://%s:5000/api/rooms/0' % host
    data = json.dumps({'movement': moveValue, 'gas': gasValue, 'light': fotoValue, 'noise': soundValue, 'timestamp': time.time()})
    req = urllib2.Request(url, data, {'Content-Type': 'application/json'})
    f = urllib2.urlopen(req)
    response = f.read()
    f.close()


host = sys.argv[1]
update();
