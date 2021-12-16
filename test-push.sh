#!/usr/bin/env bash

git commit -am 'code format';
if [ $? -ne 0 ]; then exit; fi

mvn test;
if [ $? -ne 0 ]; then exit; fi

git push;
