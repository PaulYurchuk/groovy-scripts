#!/bin/bash

# fail if anything errors
set -e
# fail if a function call is missing an argument
set -u

username=admin
password=admin123

host=http://192.168.56.24:8081

# add a script to the repository manager and run it
function NewRep {
  name=$1
  file=$2
  groovy -Dgroovy.grape.report.downloads=true -Dgrape.config=grapeConfig.xml addUpdateScript.groovy -u "$username" -p "$password" -n "$name" -f "$file" -h "$host"
  printf "\nPublished $file as $name\n\n"
  curl -v -X POST -u $username:$password --header "Content-Type: text/plain" "$host/service/siesta/rest/v1/script/$name/run"
  printf "\nSuccessfully executed $name script\n\n\n"
}
NewRep raw NewRepository.groovy
NewRep newuser newuser.groovy
