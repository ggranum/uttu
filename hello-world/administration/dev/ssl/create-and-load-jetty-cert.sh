#!/bin/sh

. ./ssl/create-certs.sh

PASS_FILE=./credentials.local.cert

if [ -f $PASS_FILE ]; then
    echo "Reading password from $PASS_FILE file"
    CERT_PASS=$(cat "$PASS_FILE")
else
  echo "Cannot read cert password. Missing '$PASS_FILE'. Aborting."
  exit -2
fi


if [ ! -f ./keystore ]; then
  echo "Creating keystore and keystore.properties using a random password."
  STORE_PASS=$(openssl rand -base64 64 | head -c 32;echo;)
  echo "keystore_password=$STORE_PASS
keystore_manager_password=$STORE_PASS
truststore_password=$STORE_PASS" > keystore.properties


  keytool -importkeystore -srckeystore jetty.pkcs12 -alias 1 -srcstoretype PKCS12 -srcstorepass $CERT_PASS -destkeystore keystore -deststorepass $STORE_PASS -destkeypass $STORE_PASS -destalias jetty
  #
  #keytool -keystore keystore\
  # -import\
  # -alias jetty\
  # -trustcacerts\
  # -keypass $STORE_PASS\
  # -storepass $STORE_PASS\
  # -file jetty.crt

else
  echo "Keystore already exists. Nothing to do."
fi
