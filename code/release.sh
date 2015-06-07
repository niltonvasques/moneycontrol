#!/bin/bash

if [[ $# -ne 1 ]]; then
	echo "Usage: $0 keystore"
	exit 1
fi

if [[ ! -f $1 ]]; then
	echo "$1 doesn't exist or isn't a keystore"
	exit 1
fi

keystore=$1

read -s -p "Keystore Password: " STORE_PASS
echo
read -p "Key Alias: " KEY_ALIAS
read -s -p "Key Password: " KEY_PASS
echo

#using "$1" appended the keystore path to the project path, which didn't work for an Android Gradle project structure.
#MyProject
#--MyApp
#----my.keystore
#--gradlew
#--release.sh
#To execute it: ./release.sh MyApp/my.keystore

# remove the trailing directories from the keystore
export ORG_GRADLE_PROJECT_storeFile="${keystore#*/}"
export ORG_GRADLE_PROJECT_storePassword="$STORE_PASS"
export ORG_GRADLE_PROJECT_keyAlias="$KEY_ALIAS"
export ORG_GRADLE_PROJECT_keyPassword="$KEY_PASS"

./gradlew signingReport

read -p "Is this correct? [y/n] " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
	./gradlew build
fi
