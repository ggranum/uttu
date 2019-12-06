#!/bin/sh

# TODO Refer to local environment (not committed to git) info for  password and domain configuration.

openssl genrsa -des3 -out jetty_prod.key 2048

openssl req -new\
 -x509 -key jetty.key\
 -newkey rsa:2048\
 -subj "/C=US/ST=ExampleState/L=ExampleCity/O=ExampleOrg./CN=*.example.com"\
 -out jetty.crt\
 -passin pass:xQaN9bEf7kG3


#openssl req -new\
# -newkey rsa:2048\
# -nodes\
# -out star_example_com.csr\
# -keyout star_example_com.key\
