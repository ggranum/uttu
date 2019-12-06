#!/bin/sh

PASS_FILE=./credentials.local.cert

echo $PASS_FILE



if [ -f $PASS_FILE ]; then
    echo "Reading password from $PASS_FILE file"
    PASSWORD=$(cat "$PASS_FILE")
else
  # Create random password and save it to the pass file for later - e.g. to load the cert into the keystore
  # If .gitIgnore is configured properly the password file WILL NOT be committed.
  PASSWORD=$(openssl rand -base64 64 | head -c 32;echo;)
  echo "PASSWORD=$PASSWORD" > "$PASS_FILE"
  echo "Wrote password to new $PASS_FILE file"
fi

openssl genrsa -des3\
 -passout pass:$PASSWORD\
 -out jetty.key 2048

openssl req -new\
 -x509 -key jetty.key\
 -newkey rsa:2048\
 -subj "/C=US/ST=ExampleState/L=ExampleCity/O=ExampleOrg/CN=localhost"\
 -out jetty.crt\
 -passin pass:$PASSWORD


openssl pkcs12 -inkey jetty.key -in jetty.crt -passin pass:$PASSWORD -export -out jetty.pkcs12 -password pass:$PASSWORD
