APP_NAME=MoneyControl
PACKAGE=br.niltonvasques.moneycontrol
CC=./gradlew
ADB=adb

.PHONY: build uninstall install log

all: build install


build:
	$(CC) assembleDebug

install:
	find . -name *debug.apk | xargs adb install -r

install_release:
	find . -name *release.apk | xargs adb install -r

uninstall:
	$(ADB) uninstall $(PACKAGE) 

log:
	$(eval PID=`adb shell dumpsys meminfo $(PACKAGE) | grep "pid [0-9]*" -oh | grep "[0-9]*" -oh`)
	$(ADB) logcat | grep "($(PID))"

