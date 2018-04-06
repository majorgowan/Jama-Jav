FROM sunithar/oracle-java8-ant:latest

# Install cron (and procps and nano for debugging tasks)
RUN apt-get update && apt-get install -y vim procps tofrodos

# Make a folder in container for volume
RUN mkdir /Jama-Jav
WORKDIR /Jama-Jav
