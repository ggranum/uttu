#!/bin/sh
if [ ! -f ./keystore ]; then
  echo "Creating keystore and keystore.properties using a random password."
  STORE_PASS=$(rand -base64 64 | head -c 32;echo;)
  echo "keystore_password=$STORE_PASS"
  keystore_manager_password=$STORE_PASS
  truststore_password=$STORE_PASS > keystore.properties
  keytool -keystore keystore -import -alias jetty -file jetty.crt -trustcacerts -keypass $STORE_PASS

else
  echo "Keystore already exists. Nothing to do."
fi
